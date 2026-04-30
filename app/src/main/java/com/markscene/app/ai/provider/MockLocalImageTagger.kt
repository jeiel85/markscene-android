package com.markscene.app.ai.provider

import android.net.Uri
import com.markscene.app.core.model.TagSource
import com.markscene.app.domain.tag.TagSuggestion

class MockLocalImageTagger : LocalImageTagger {
    override suspend fun generateTags(imageUri: Uri): List<TagSuggestion> {
        val seed = imageUri.toString().lowercase()
        val common = listOf(
            TagSuggestion("indoor", TagSource.Mock, 0.78f),
            TagSuggestion("record", TagSource.Mock, 0.72f)
        )

        val sourceSpecific = if (seed.contains("capture")) {
            listOf(
                TagSuggestion("camera", TagSource.Mock, 0.81f),
                TagSuggestion("photo", TagSource.Mock, 0.77f)
            )
        } else {
            listOf(
                TagSuggestion("import", TagSource.Mock, 0.80f),
                TagSuggestion("gallery", TagSource.Mock, 0.74f)
            )
        }

        return (common + sourceSpecific).distinctBy { it.name }
    }
}
