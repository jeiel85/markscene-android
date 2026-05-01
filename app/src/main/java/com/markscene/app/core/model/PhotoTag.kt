package com.markscene.app.core.model

import kotlinx.serialization.Serializable

@Serializable
data class PhotoTag(
    val id: String,
    val recordId: String,
    val name: String,
    val rawName: String?,
    val source: TagSource,
    val confidence: Float?,
    val userConfirmed: Boolean,
    val createdAt: Long
)
