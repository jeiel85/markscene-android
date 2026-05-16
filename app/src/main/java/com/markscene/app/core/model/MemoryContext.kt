package com.markscene.app.core.model

data class MemoryContext(
    val id: String,
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
