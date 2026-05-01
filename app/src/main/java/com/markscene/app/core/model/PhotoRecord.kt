package com.markscene.app.core.model

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
