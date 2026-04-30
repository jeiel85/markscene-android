package com.markscene.app.domain.tag

import com.markscene.app.core.model.TagSource

data class TagSuggestion(
    val name: String,
    val source: TagSource,
    val confidence: Float? = null
)
