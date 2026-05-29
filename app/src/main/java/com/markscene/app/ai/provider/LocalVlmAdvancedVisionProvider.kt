package com.markscene.app.ai.provider

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.genai.llminference.GraphOptions
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
            modelManager.getModelPath() ?: error("로컬 AI 모델이 설정되지 않았습니다. 설정에서 모델을 다운로드해주세요.")
            val bitmap = decodeImage(record.imageUri)
            val mpImage = BitmapImageBuilder(bitmap).build()

            val inference = try {
                modelManager.acquireInference()
            } catch (oom: OutOfMemoryError) {
                error("이 기기의 메모리로는 로컬 AI 모델을 로드할 수 없습니다. 더 가벼운 모델이 필요합니다.")
            } catch (t: Throwable) {
                error("로컬 AI 모델 로드 실패: ${t.message ?: "알 수 없는 오류"}")
            }

            val sessionOptions = LlmInferenceSession.LlmInferenceSessionOptions.builder()
                .setTopK(10)
                .setTemperature(0.2f)
                .setGraphOptions(GraphOptions.builder().setEnableVisionModality(true).build())
                .build()

            val responseText = LlmInferenceSession.createFromOptions(inference, sessionOptions).use { session ->
                session.addQueryChunk(buildPrompt(record))
                session.addImage(mpImage)
                session.generateResponse()
            }
            parseAnalysisResponse(responseText)
        }
    }

    suspend fun askQuestion(record: PhotoRecord, question: String): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            modelManager.getModelPath() ?: error("로컬 AI 모델이 설정되지 않았습니다. 설정에서 모델을 다운로드해주세요.")
            val bitmap = decodeImage(record.imageUri)
            val mpImage = BitmapImageBuilder(bitmap).build()
            val inference = try {
                modelManager.acquireInference()
            } catch (oom: OutOfMemoryError) {
                error("이 기기의 메모리로는 로컬 AI 모델을 로드할 수 없습니다. 더 가벼운 모델이 필요합니다.")
            } catch (t: Throwable) {
                error("로컬 AI 모델 로드 실패: ${t.message ?: "알 수 없는 오류"}")
            }

            val sessionOptions = LlmInferenceSession.LlmInferenceSessionOptions.builder()
                .setTopK(10)
                .setTemperature(0.2f)
                .setGraphOptions(GraphOptions.builder().setEnableVisionModality(true).build())
                .build()

            LlmInferenceSession.createFromOptions(inference, sessionOptions).use { session ->
                session.addQueryChunk(buildQuestionPrompt(record, question))
                session.addImage(mpImage)
                session.generateResponse().trim().ifBlank {
                    "로컬 AI가 답변을 생성하지 못했습니다. 질문을 조금 더 구체적으로 바꿔 다시 시도해주세요."
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

    private fun buildQuestionPrompt(record: PhotoRecord, question: String): String {
        return """
            You are MarkScene's private on-device visual assistant.
            Answer in Korean based only on the attached image and local record metadata.
            Do not claim certainty. If uncertain, say that it only appears that way.
            Keep the answer concise and useful for adding searchable notes or tags.

            Existing metadata:
            - Title: ${record.title ?: "없음"}
            - Memo: ${record.memo ?: "없음"}
            - Existing tags: ${record.tags.joinToString { it.name }.ifBlank { "없음" }}

            User question:
            $question
        """.trimIndent()
    }

    private fun parseAnalysisResponse(raw: String): MockAdvancedAnalysisResult {
        val cleaned = raw
            .replace("```json", "")
            .replace("```", "")
            .trim()
        val start = cleaned.indexOf('{')
        val end = cleaned.lastIndexOf('}')
        if (start < 0 || end <= start) {
            // 모델이 JSON을 만들지 못한 경우에도 사용자에게 의미 있는 결과를 돌려준다.
            val fallbackSummary = cleaned.lines().firstOrNull { it.isNotBlank() }?.take(200)
                ?: "로컬 AI가 응답을 생성했지만 구조화된 결과를 만들지 못했습니다."
            return MockAdvancedAnalysisResult(
                sceneSummary = fallbackSummary,
                suggestedTags = emptyList(),
                warnings = listOf("로컬 AI 응답을 구조화할 수 없어 요약만 표시했습니다. 결과는 수정 가능한 제안입니다.")
            )
        }
        val jsonText = cleaned.substring(start, end + 1)
        val parsed = runCatching { JSONObject(jsonText) }.getOrElse {
            return MockAdvancedAnalysisResult(
                sceneSummary = "로컬 AI 응답을 해석할 수 없습니다.",
                suggestedTags = emptyList(),
                warnings = listOf("로컬 AI 응답이 JSON 형식이 아니어서 무시했습니다. 다시 시도해주세요.")
            )
        }
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
