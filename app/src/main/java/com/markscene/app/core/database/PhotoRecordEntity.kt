package com.markscene.app.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photo_records")
data class PhotoRecordEntity(
    @PrimaryKey val id: String,
    val imageUri: String,
    val title: String?,
    val memo: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val analysisStatus: String,
    val ocrText: String?,
    val space: String?
)
