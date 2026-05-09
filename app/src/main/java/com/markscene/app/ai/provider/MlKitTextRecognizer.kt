package com.markscene.app.ai.provider

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import kotlinx.coroutines.tasks.await
import java.io.IOException

class MlKitTextRecognizer(private val context: Context) : TextRecognizer {
    // 일부 단말(예: 갤럭시 + Play Services 한국어 OCR 다이나마이트 모듈 미설치 상태)에서
    // getClient(...) 호출이 동기적으로 예외를 던질 수 있어, 첫 사용 시점까지 초기화를 지연한다.
    private val recognizer by lazy {
        runCatching {
            TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())
        }.getOrNull()
    }

    override suspend fun recognizeText(imageUri: Uri): Result<String> {
        val client = recognizer ?: return Result.success("")
        return try {
            val image = InputImage.fromFilePath(context, imageUri)
            val result = client.process(image).await()
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
