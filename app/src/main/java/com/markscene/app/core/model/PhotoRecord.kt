package com.markscene.app.core.model

import kotlinx.serialization.Serializable

@Serializable
data class PhotoRecord(
    val id: String,
    val imageUri: String,
    val title: String?,
    val memo: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val analysisStatus: AnalysisStatus,
    val ocrText: String? = null,
    val tags: List<PhotoTag>
)
