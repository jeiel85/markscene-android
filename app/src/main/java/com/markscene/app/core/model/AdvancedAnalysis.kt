package com.markscene.app.core.model

data class AdvancedAnalysis(
    val id: String,
    val recordId: String,
    val provider: String,
    val sceneSummary: String,
    val createdAt: Long
)
