package com.markscene.app.ai.provider

import android.net.Uri

interface TextRecognizer {
    suspend fun recognizeText(imageUri: Uri): Result<String>
}
