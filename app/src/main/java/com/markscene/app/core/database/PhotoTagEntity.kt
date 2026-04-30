package com.markscene.app.core.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "photo_tags",
    foreignKeys = [
        ForeignKey(
            entity = PhotoRecordEntity::class,
            parentColumns = ["id"],
            childColumns = ["recordId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["recordId"])]
)
data class PhotoTagEntity(
    @PrimaryKey val id: String,
    val recordId: String,
    val name: String,
    val rawName: String?,
    val source: String,
    val confidence: Float?,
    val userConfirmed: Boolean,
    val createdAt: Long
)
