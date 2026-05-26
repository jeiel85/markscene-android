package com.markscene.app.ai.provider

import android.content.Context
import com.markscene.app.data.settings.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

class LocalVisionModelManager(
    private val context: Context,
    private val userPreferences: UserPreferences
) {
    fun getModelPath(): String? {
        val path = userPreferences.getLocalVlmModelPath() ?: return null
        return path.takeIf { File(it).exists() }
    }

    fun getModelName(): String? = userPreferences.getLocalVlmModelName()

    suspend fun downloadModel(modelUrl: String, displayName: String): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            require(modelUrl.isNotBlank()) { "모델 다운로드 URL이 설정되지 않았습니다." }
            val url = URL(modelUrl)
            require(url.protocol == "https") { "로컬 AI 모델은 HTTPS 주소에서만 다운로드할 수 있습니다." }

            val targetDir = File(context.filesDir, MODEL_DIR).apply { mkdirs() }
            val targetFile = File(targetDir, MODEL_FILE_NAME)
            val tempFile = File(targetDir, "$MODEL_FILE_NAME.download").apply { delete() }

            val connection = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 30_000
                readTimeout = 120_000
                instanceFollowRedirects = true
                requestMethod = "GET"
            }

            try {
                val responseCode = connection.responseCode
                require(responseCode in 200..299) { "모델 다운로드 실패: HTTP $responseCode" }
                connection.inputStream.use { input ->
                    tempFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            } finally {
                connection.disconnect()
            }

            require(tempFile.length() > 0L) { "다운로드한 모델 파일이 비어 있습니다." }
            if (targetFile.exists()) targetFile.delete()
            require(tempFile.renameTo(targetFile) || tempFile.copyTo(targetFile, overwrite = true).exists()) {
                "모델 파일을 저장하지 못했습니다."
            }
            tempFile.delete()

            val resolvedDisplayName = displayName.ifBlank { "MarkScene local VLM model" }
            userPreferences.setLocalVlmModel(targetFile.absolutePath, resolvedDisplayName)
            resolvedDisplayName
        }
    }

    fun clearModel(): Boolean {
        val path = userPreferences.getLocalVlmModelPath()
        userPreferences.clearLocalVlmModel()
        return path?.let { File(it).delete() } ?: true
    }

    private companion object {
        const val MODEL_DIR = "local_vlm"
        const val MODEL_FILE_NAME = "markscene_local_vlm.task"
    }
}
