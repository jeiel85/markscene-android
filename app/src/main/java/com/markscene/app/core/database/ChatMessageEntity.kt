package com.markscene.app.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val recordId: String,
    val role: String, // "user", "assistant"
    val content: String,
    val createdAt: Long
)
