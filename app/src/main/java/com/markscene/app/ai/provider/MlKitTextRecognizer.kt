package com.markscene.app.ai.provider

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import kotlinx.coroutines.tasks.await
import java.io.IOException

class MlKitTextRecognizer(private val context: Context) : TextRecognizer {
    private val recognizer = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())

    override suspend fun recognizeText(imageUri: Uri): Result<String> {
        return try {
            val image = InputImage.fromFilePath(context, imageUri)
            val result = recognizer.process(image).await()
            if (result.text.isNotBlank()) {
                Result.success(result.text)
            } else {
                Result.success("")
            }
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
