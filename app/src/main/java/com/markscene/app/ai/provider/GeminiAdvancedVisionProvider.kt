package com.markscene.app.ai.provider

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.markscene.app.core.model.PhotoRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class GeminiAdvancedVisionProvider(
    private val context: Context,
    private val client: OkHttpClient = OkHttpClient()
) {
    suspend fun analyze(record: PhotoRecord, apiKey: String): Result<MockAdvancedAnalysisResult> = withContext(Dispatchers.IO) {
        runCatching {
            val imageBase64 = encodeImage(record.imageUri)
            val prompt = """
                Analyze this image as a personal visual note.
                Return strict JSON only:
                {"sceneSummary":"string","suggestedTags":["string"],"warnings":["string"]}
            """.trimIndent()

            val responseBody = generateContent(prompt, imageBase64, apiKey)
            parseAnalysisResponse(responseBody)
        }
    }

    suspend fun askQuestion(record: PhotoRecord, question: String, apiKey: String): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val imageBase64 = encodeImage(record.imageUri)
            val prompt = """
                You are a helpful visual assistant for a personal note app called MarkScene.
                The user is asking a question about the attached photo.
                Photo Metadata:
                - Title: ${record.title ?: "Untitled"}
                - Space: ${record.space ?: "Unknown"}
                - Tags: ${record.tags.joinToString { it.name }}
                - OCR Text: ${record.ocrText ?: "None detected"}
                
                Question: $question
                
                Answer concisely and accurately based on the visual information and metadata.
            """.trimIndent()

            val responseBody = generateContent(prompt, imageBase64, apiKey)
            parseChatResponse(responseBody)
        }
    }

    private suspend fun generateContent(prompt: String, imageBase64: String, apiKey: String): String {
        val payload = JSONObject()
            .put("contents", JSONArray().put(JSONObject().put("parts", JSONArray()
                .put(JSONObject().put("text", prompt))
                .put(JSONObject().put("inline_data", JSONObject()
                    .put("mime_type", "image/jpeg")
                    .put("data", imageBase64)
                ))
            )))

        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey")
            .post(payload.toString().toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) error("Gemini request failed: ${response.code}")
            return response.body?.string().orEmpty()
        }
    }

    private fun parseAnalysisResponse(raw: String): MockAdvancedAnalysisResult {
        val text = extractFirstText(raw)
        val cleaned = text
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        val parsed = JSONObject(cleaned)
        val tags = parsed.optJSONArray("suggestedTags") ?: JSONArray()
        val warnings = parsed.optJSONArray("warnings") ?: JSONArray()

        return MockAdvancedAnalysisResult(
            sceneSummary = parsed.optString("sceneSummary", "요약 없음"),
            suggestedTags = List(tags.length()) { i -> tags.optString(i).lowercase() }.filter { it.isNotBlank() },
            warnings = List(warnings.length()) { i -> warnings.optString(i) }.filter { it.isNotBlank() }
        )
    }

    private fun parseChatResponse(raw: String): String {
        return extractFirstText(raw)
    }

    private fun extractFirstText(raw: String): String {
        val root = JSONObject(raw)
        return root.optJSONArray("candidates")
            ?.optJSONObject(0)
            ?.optJSONObject("content")
            ?.optJSONArray("parts")
            ?.optJSONObject(0)
            ?.optString("text")
            .orEmpty()
    }

    private fun encodeImage(uriString: String): String {
        val uri = Uri.parse(uriString)
        context.contentResolver.openInputStream(uri).use { input ->
            val original = BitmapFactory.decodeStream(input) ?: error("Cannot decode image")
            val resized = resizeIfNeeded(original)
            val output = ByteArrayOutputStream()
            resized.compress(Bitmap.CompressFormat.JPEG, 80, output)
            return Base64.encodeToString(output.toByteArray(), Base64.NO_WRAP)
        }
    }

    private fun resizeIfNeeded(bitmap: Bitmap): Bitmap {
        val maxSize = 1024 // Slightly smaller for faster chat response
        val width = bitmap.width
        val height = bitmap.height
        val largest = maxOf(width, height)
        if (largest <= maxSize) return bitmap
        val ratio = maxSize.toFloat() / largest
        return Bitmap.createScaledBitmap(bitmap, (width * ratio).toInt(), (height * ratio).toInt(), true)
    }
}
