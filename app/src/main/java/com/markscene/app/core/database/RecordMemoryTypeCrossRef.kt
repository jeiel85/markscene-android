package com.markscene.app.core.database

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "record_memory_types",
    primaryKeys = ["recordId", "memoryType"],
    indices = [
        Index("recordId"),
        Index("memoryType")
    ]
)
data class RecordMemoryTypeCrossRef(
    val recordId: String,
    val memoryType: String,
    val source: String,
    val userConfirmed: Boolean,
    val createdAt: Long
)
