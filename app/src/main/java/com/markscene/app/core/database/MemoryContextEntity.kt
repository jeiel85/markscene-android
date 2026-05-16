package com.markscene.app.core.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "memory_contexts",
    foreignKeys = [
        ForeignKey(
            entity = PhotoRecordEntity::class,
            parentColumns = ["id"],
            childColumns = ["recordId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("recordId", unique = true),
        Index("primaryMemoryType"),
        Index("isWorthRecalling"),
        Index("createdAt")
    ]
)
data class MemoryContextEntity(
    @PrimaryKey val id: String,
    val recordId: String,
    val primaryMemoryType: String?,
    val mood: String?,
    val energy: Int?,
    val contextType: String?,
    val isWorthRecalling: Boolean,
    val recallReason: String?,
    val createdAt: Long,
    val updatedAt: Long
)
