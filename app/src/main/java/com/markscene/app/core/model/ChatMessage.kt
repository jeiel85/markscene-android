package com.markscene.app.core.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    val id: String,
    val recordId: String,
    val role: String, // "user", "assistant"
    val content: String,
    val createdAt: Long
)
