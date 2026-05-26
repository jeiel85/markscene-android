package com.markscene.app.ai.provider

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.genai.llminference.GraphOptions
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import com.google.mediapipe.tasks.genai.llminference.LlmInferenceSession
import com.markscene.app.core.model.PhotoRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class LocalVlmAdvancedVisionProvider(
    private val context: Context,
    private val modelManager: LocalVisionModelManager
) {
    suspend fun analyze(record: PhotoRecord): Result<MockAdvancedAnalysisResult> = withContext(Dispatchers.IO) {
        runCatching {
            val modelPath = modelManager.getModelPath() ?: error("로컬 AI 모델이 설정되지 않았습니다.")
            val bitmap = decodeImage(record.imageUri)
            val mpImage = BitmapImageBuilder(bitmap).build()

            val inferenceOptions = LlmInference.LlmInferenceOptions.builder()
                .setModelPath(modelPath)
                .setMaxTokens(512)
                .setMaxNumImages(1)
                .build()
            val sessionOptions = LlmInferenceSession.LlmInferenceSessionOptions.builder()
                .setTopK(10)
                .setTemperature(0.2f)
                .setGraphOptions(GraphOptions.builder().setEnableVisionModality(true).build())
                .build()

            LlmInference.createFromOptions(context, inferenceOptions).use { inference ->
                LlmInferenceSession.createFromOptions(inference, sessionOptions).use { session ->
                    session.addQueryChunk(buildPrompt(record))
                    session.addImage(mpImage)
                    parseAnalysisResponse(session.generateResponse())
                }
            }
        }
    }

    private fun buildPrompt(record: PhotoRecord): String {
        return """
            Analyze this image as a private visual note for MarkScene.
            Return strict JSON only. No markdown, no code fence.
            Schema:
            {
              "sceneSummary": "short Korean summary with cautious wording",
              "objects": [{"nameKo":"string","nameEn":"string","confidence":"high|medium|low"}],
              "suggestedTags": ["short Korean or common tag strings"],
              "warnings": ["short Korean cautions"]
            }
            Existing metadata:
            - Title: ${record.title ?: "없음"}
            - Memo: ${record.memo ?: "없음"}
            - Existing tags: ${record.tags.joinToString { it.name }.ifBlank { "없음" }}
            Do not claim certainty. Prefer useful searchable tags. Use 4 to 10 tags.
        """.trimIndent()
    }

    private fun parseAnalysisResponse(raw: String): MockAdvancedAnalysisResult {
        val cleaned = raw
            .replace("```json", "")
            .replace("```", "")
            .trim()
        val start = cleaned.indexOf('{')
        val end = cleaned.lastIndexOf('}')
        require(start >= 0 && end > start) { "로컬 AI 응답에서 JSON을 찾을 수 없습니다." }
        val jsonText = cleaned.substring(start, end + 1)
        val parsed = JSONObject(jsonText)
        val suggestedTags = parsed.optJSONArray("suggestedTags") ?: JSONArray()
        val objects = parsed.optJSONArray("objects") ?: JSONArray()
        val warnings = parsed.optJSONArray("warnings") ?: JSONArray()

        val objectTags = List(objects.length()) { index ->
            val item = objects.optJSONObject(index)
            item?.optString("nameKo")?.takeIf { it.isNotBlank() }
                ?: item?.optString("nameEn")?.takeIf { it.isNotBlank() }
        }.filterNotNull()

        return MockAdvancedAnalysisResult(
            sceneSummary = parsed.optString("sceneSummary", "로컬 AI가 장면을 분석했지만 요약을 만들지 못했습니다."),
            suggestedTags = (List(suggestedTags.length()) { i -> suggestedTags.optString(i) } + objectTags)
                .map { it.trim().lowercase() }
                .filter { it.isNotBlank() }
                .distinct()
                .take(12),
            warnings = List(warnings.length()) { i -> warnings.optString(i) }
                .filter { it.isNotBlank() }
                .ifEmpty { listOf("로컬 AI 결과는 수정 가능한 제안입니다.") }
        )
    }

    private fun decodeImage(uriString: String): Bitmap {
        val uri = Uri.parse(uriString)
        context.contentResolver.openInputStream(uri).use { input ->
            val original = BitmapFactory.decodeStream(input) ?: error("이미지를 읽을 수 없습니다.")
            return resizeIfNeeded(original)
        }
    }

    private fun resizeIfNeeded(bitmap: Bitmap): Bitmap {
        val maxSize = 768
        val largest = maxOf(bitmap.width, bitmap.height)
        if (largest <= maxSize) return bitmap
        val ratio = maxSize.toFloat() / largest
        return Bitmap.createScaledBitmap(
            bitmap,
            (bitmap.width * ratio).toInt().coerceAtLeast(1),
            (bitmap.height * ratio).toInt().coerceAtLeast(1),
            true
        )
    }
}
