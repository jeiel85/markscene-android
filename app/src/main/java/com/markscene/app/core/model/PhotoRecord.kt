package com.markscene.app.core.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class PhotoRecord(
    val id: String,
    val imageUri: String,
    val audioMemoUri: String? = null,
    val title: String?,
    val memo: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val analysisStatus: AnalysisStatus,
    val ocrText: String? = null,
    val space: String? = null,
    val tags: List<PhotoTag>
)
