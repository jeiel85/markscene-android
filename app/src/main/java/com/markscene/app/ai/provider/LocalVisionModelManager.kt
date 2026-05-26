package com.markscene.app.ai.provider

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.markscene.app.data.settings.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class LocalVisionModelManager(
    private val context: Context,
    private val userPreferences: UserPreferences
) {
    fun getModelPath(): String? {
        val path = userPreferences.getLocalVlmModelPath() ?: return null
        return path.takeIf { File(it).exists() }
    }

    fun getModelName(): String? = userPreferences.getLocalVlmModelName()

    suspend fun importModel(sourceUri: Uri): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val displayName = resolveDisplayName(sourceUri)
            val targetDir = File(context.filesDir, MODEL_DIR).apply { mkdirs() }
            val targetFile = File(targetDir, MODEL_FILE_NAME)

            context.contentResolver.openInputStream(sourceUri).use { input ->
                requireNotNull(input) { "모델 파일을 열 수 없습니다." }
                targetFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            require(targetFile.length() > 0L) { "모델 파일이 비어 있습니다." }
            userPreferences.setLocalVlmModel(targetFile.absolutePath, displayName)
            displayName
        }
    }

    fun clearModel(): Boolean {
        val path = userPreferences.getLocalVlmModelPath()
        userPreferences.clearLocalVlmModel()
        return path?.let { File(it).delete() } ?: true
    }

    private fun resolveDisplayName(uri: Uri): String {
        val fromCursor = context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex >= 0) cursor.getString(nameIndex) else null
        }
        return fromCursor?.takeIf { it.isNotBlank() } ?: uri.lastPathSegment ?: "local-vlm-model.task"
    }

    private companion object {
        const val MODEL_DIR = "local_vlm"
        const val MODEL_FILE_NAME = "markscene_local_vlm.task"
    }
}
