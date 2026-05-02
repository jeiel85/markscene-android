package com.markscene.app.core.model

import kotlinx.serialization.Serializable

@Serializable
data class AdvancedAnalysis(
    val id: String,
    val recordId: String,
    val provider: String,
    val sceneSummary: String,
    val createdAt: Long
)
