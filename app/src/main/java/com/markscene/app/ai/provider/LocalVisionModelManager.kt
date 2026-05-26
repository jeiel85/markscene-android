package com.markscene.app.ai.provider

import android.content.Context
import android.os.StatFs
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import com.markscene.app.data.settings.ApiKeyStore
import com.markscene.app.data.settings.UserPreferences
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class LocalVisionModelManager(
    private val context: Context,
    private val userPreferences: UserPreferences,
    private val apiKeyStore: ApiKeyStore? = null
) {
    @Volatile
    private var cachedInference: LlmInference? = null

    @Volatile
    private var cachedInferenceModelPath: String? = null

    fun getModelPath(): String? {
        val path = userPreferences.getLocalVlmModelPath() ?: return null
        return path.takeIf { File(it).exists() }
    }

    fun getModelName(): String? = userPreferences.getLocalVlmModelName()

    suspend fun downloadModel(
        modelUrl: String,
        displayName: String,
        fileName: String,
        expectedSizeMb: Long,
        authToken: String?,
        onProgress: (bytesRead: Long, totalBytes: Long) -> Unit
    ): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            require(modelUrl.isNotBlank()) { "모델 다운로드 URL이 설정되지 않았습니다." }
            val parsedUrl = URL(modelUrl)
            require(parsedUrl.protocol == "https") { "로컬 AI 모델은 HTTPS 주소에서만 다운로드할 수 있습니다." }

            val safeFileName = fileName.ifBlank {
                parsedUrl.path.substringAfterLast('/').ifBlank { DEFAULT_MODEL_FILE_NAME }
            }.replace("..", "_")

            val targetDir = File(context.filesDir, MODEL_DIR).apply { mkdirs() }
            val targetFile = File(targetDir, safeFileName)
            val tempFile = File(targetDir, "$safeFileName.download").apply { if (exists()) delete() }

            val freeSpaceBytes = runCatching { StatFs(targetDir.absolutePath).availableBytes }.getOrDefault(0L)
            val estimatedBytes = expectedSizeMb.coerceAtLeast(0L) * 1024L * 1024L
            if (estimatedBytes > 0 && freeSpaceBytes in 1 until (estimatedBytes + RESERVE_BYTES)) {
                error(
                    "디스크 여유 공간이 부족합니다. 약 ${expectedSizeMb}MB가 필요하지만 " +
                        "현재 ${freeSpaceBytes / (1024L * 1024L)}MB만 남아 있습니다."
                )
            }

            val connection = openConnection(parsedUrl, authToken)
            try {
                val responseCode = connection.responseCode
                when (responseCode) {
                    in 200..299 -> Unit
                    401, 403 -> error(
                        "모델 다운로드 권한이 거부되었습니다 (HTTP $responseCode). " +
                            "HuggingFace에서 라이선스를 수락하고 read 토큰을 설정에 입력했는지 확인하세요."
                    )
                    404 -> error("모델 파일을 찾을 수 없습니다 (HTTP 404). 모델 URL이 올바른지 확인하세요.")
                    else -> error("모델 다운로드 실패: HTTP $responseCode")
                }

                val contentLength = connection.contentLengthLong.takeIf { it > 0 } ?: -1L
                if (contentLength > 0 && freeSpaceBytes in 1 until (contentLength + RESERVE_BYTES)) {
                    error(
                        "디스크 여유 공간이 부족합니다. 약 ${contentLength / (1024L * 1024L)}MB가 필요하지만 " +
                            "현재 ${freeSpaceBytes / (1024L * 1024L)}MB만 남아 있습니다."
                    )
                }

                connection.inputStream.use { input ->
                    tempFile.outputStream().use { output ->
                        val buffer = ByteArray(BUFFER_SIZE)
                        var totalRead = 0L
                        var lastReported = 0L
                        var read: Int
                        while (true) {
                            currentCoroutineContext().ensureActive()
                            read = input.read(buffer)
                            if (read == -1) break
                            output.write(buffer, 0, read)
                            totalRead += read
                            if (totalRead - lastReported >= PROGRESS_REPORT_BYTES) {
                                onProgress(totalRead, contentLength)
                                lastReported = totalRead
                            }
                        }
                        output.flush()
                        onProgress(totalRead, contentLength)
                    }
                }
            } catch (ce: CancellationException) {
                tempFile.delete()
                throw ce
            } catch (io: IOException) {
                tempFile.delete()
                throw IOException("네트워크 오류로 다운로드가 중단되었습니다: ${io.message}", io)
            } finally {
                runCatching { connection.disconnect() }
            }

            require(tempFile.length() > 0L) { "다운로드한 모델 파일이 비어 있습니다." }

            // 모델이 교체되므로 캐시된 inference 인스턴스를 닫는다.
            closeCachedInference()

            // 기존 모델 폴더에 남아 있는 다른 모델 파일은 정리해 디스크 절약.
            targetDir.listFiles()?.forEach { existing ->
                if (existing.name != tempFile.name && existing.name != safeFileName) {
                    existing.delete()
                }
            }
            if (targetFile.exists()) targetFile.delete()
            if (!tempFile.renameTo(targetFile)) {
                tempFile.copyTo(targetFile, overwrite = true)
                tempFile.delete()
            }

            val resolvedDisplayName = displayName.ifBlank { "MarkScene local VLM model" }
            userPreferences.setLocalVlmModel(targetFile.absolutePath, resolvedDisplayName)
            resolvedDisplayName
        }
    }

    fun clearModel(): Boolean {
        closeCachedInference()
        val path = userPreferences.getLocalVlmModelPath()
        userPreferences.clearLocalVlmModel()
        val deletedTarget = path?.let { File(it).delete() } ?: true
        // 폴더 내 임시/잔여 파일 정리.
        val dir = File(context.filesDir, MODEL_DIR)
        if (dir.exists()) {
            dir.listFiles()?.forEach { it.delete() }
        }
        return deletedTarget
    }

    /**
     * MediaPipe LlmInference 인스턴스를 게으르게 만들어 캐싱한다.
     * 동일한 모델 경로일 때는 재사용해 수 GB짜리 모델을 매번 다시 로드하지 않도록 한다.
     */
    @Synchronized
    fun acquireInference(maxTokens: Int = DEFAULT_MAX_TOKENS): LlmInference {
        val path = getModelPath() ?: error("로컬 AI 모델 파일을 찾을 수 없습니다.")
        val cached = cachedInference
        if (cached != null && cachedInferenceModelPath == path) {
            return cached
        }
        // 모델 경로가 바뀌었거나 처음 로드하는 경우.
        closeCachedInference()
        val options = LlmInference.LlmInferenceOptions.builder()
            .setModelPath(path)
            .setMaxTokens(maxTokens)
            .setMaxNumImages(1)
            .build()
        val inference = LlmInference.createFromOptions(context, options)
        cachedInference = inference
        cachedInferenceModelPath = path
        return inference
    }

    @Synchronized
    fun closeCachedInference() {
        runCatching { cachedInference?.close() }
        cachedInference = null
        cachedInferenceModelPath = null
    }

    private fun openConnection(url: URL, authToken: String?): HttpURLConnection {
        val connection = (url.openConnection() as HttpURLConnection).apply {
            connectTimeout = 30_000
            readTimeout = 10 * 60_000 // 10분: 대용량 모델 다운로드용
            instanceFollowRedirects = true
            requestMethod = "GET"
            setRequestProperty("User-Agent", "MarkScene-Android/1.0 (+local-vlm)")
        }
        val trimmedToken = authToken?.trim().orEmpty()
        if (trimmedToken.isNotEmpty()) {
            connection.setRequestProperty("Authorization", "Bearer $trimmedToken")
        }
        return connection
    }

    private companion object {
        const val MODEL_DIR = "local_vlm"
        const val DEFAULT_MODEL_FILE_NAME = "markscene_local_vlm.task"
        const val BUFFER_SIZE = 256 * 1024
        const val PROGRESS_REPORT_BYTES = 2L * 1024L * 1024L // 2MB마다 progress 보고
        const val RESERVE_BYTES = 256L * 1024L * 1024L // 256MB 여유 공간
        const val DEFAULT_MAX_TOKENS = 1024
    }
}
