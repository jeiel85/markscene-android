package com.markscene.app.ai.provider

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.markscene.app.core.model.TagSource
import com.markscene.app.domain.tag.TagSuggestion
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class MlKitLocalImageTagger(
    private val context: Context,
    private val fallback: LocalImageTagger = MockLocalImageTagger()
) : LocalImageTagger {

    private val labeler by lazy {
        val options = ImageLabelerOptions.Builder()
            .setConfidenceThreshold(0.6f)
            .build()
        ImageLabeling.getClient(options)
    }

    override suspend fun generateTags(imageUri: Uri): List<TagSuggestion> {
        return runCatching {
            val image = InputImage.fromFilePath(context, imageUri)
            val labels = process(image)
            labels
                .map { label ->
                    TagSuggestion(
                        name = normalize(label.text),
                        source = TagSource.LocalImageLabel,
                        confidence = label.confidence
                    )
                }
                .distinctBy { it.name }
                .ifEmpty { fallback.generateTags(imageUri) }
        }.getOrElse { fallback.generateTags(imageUri) }
    }

    private suspend fun process(image: InputImage) = suspendCancellableCoroutine<List<com.google.mlkit.vision.label.ImageLabel>> { cont ->
        labeler.process(image)
            .addOnSuccessListener { labels ->
                if (cont.isActive) cont.resume(labels)
            }
            .addOnFailureListener {
                if (cont.isActive) cont.resume(emptyList())
            }
    }

    private fun normalize(raw: String): String {
        val dictionary = mapOf(
            "Cup" to "cup",
            "Mug" to "mug",
            "Table" to "table",
            "Furniture" to "furniture",
            "Food" to "food",
            "Plate" to "plate",
            "Laptop" to "laptop",
            "Computer" to "computer",
            "Keyboard" to "keyboard",
            "Mouse" to "mouse",
            "Indoor" to "indoor",
            "Outdoor" to "outdoor",
            "Plant" to "plant"
        )
        return dictionary[raw] ?: raw.lowercase()
    }
}
