package com.markscene.app.ai.provider

import android.net.Uri
import com.markscene.app.domain.tag.TagSuggestion

interface LocalImageTagger {
    suspend fun generateTags(imageUri: Uri): List<TagSuggestion>
}
