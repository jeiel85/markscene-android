# Local Tagging

## Goal

Provide a useful first experience without an API key.

Local tagging should create searchable tag suggestions from a photo. It does not need to perfectly identify every object.

## Product Definition

Local tagging means:

- Fast on-device tag suggestions.
- Editable tag chips.
- Basic search metadata.
- No external upload.
- No API key required.

Local tagging does not guarantee:

- Complete object detection.
- Accurate object counts.
- Fine-grained product recognition.
- Landmark certainty.
- Natural-language scene reasoning.

## Recommended Implementation Path

### Phase 1: Mock Local Tagger

Start with a mock tagger that returns deterministic tags such as:

- desk
- cup
- laptop
- indoor

This enables UI and persistence work first.

### Phase 2: Real On-Device Tagger

Add ML Kit Image Labeling or another approved on-device image labeler.

Important behavior:

- Filter low-confidence labels.
- Normalize English labels to user-facing Korean labels when possible.
- Deduplicate similar tags.
- Store raw labels and display labels separately.
- Mark generated tags as unconfirmed until the user edits or saves them.

### Phase 3: Optional Object Detection

Object detection may be added to support approximate positions or visual object boxes.

Do not rely on local object detection for precise object names or counts in MVP.

### Phase 4: Optional Local VLM Advanced Analysis

Local VLM analysis can be used when the user wants a deeper scene/object summary without sending the photo to an external AI provider.

Current app direction:

- Basic tags still come from the fast local tagger path.
- An app-downloaded, approved MediaPipe-compatible VLM model enables advanced local analysis.
- The advanced local model is triggered manually from the record detail screen.
- The output is parsed into scene summary and suggested tags.
- Local VLM tags use the `local_vlm` source.

This must not become an automatic capture-time requirement because model load time, memory use, and battery impact can be high.

## Tag Normalization

Create a small dictionary first:

```kotlin
object TagNormalizer {
    private val dictionary = mapOf(
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

    fun normalize(raw: String): String = dictionary[raw] ?: raw.lowercase()
}
```

If the UI is Korean, map to Korean display names. Keep raw labels for debugging and future improvements.

## Tag Data Rules

Each tag should track:

- Display name.
- Raw name.
- Source.
- Confidence.
- User confirmed status.
- Creation time.

Sources:

- `mock`
- `local_image_label`
- `local_object_detection`
- `local_vlm`
- `user`
- `advanced_ai`

## UX Rules

- Show tags as chips.
- Allow tap-to-edit.
- Allow delete.
- Allow add custom tag.
- Do not show confidence percentages prominently in MVP unless needed.
- Use confidence internally for ranking.

## Search Rules

Search should prioritize:

1. User-confirmed tags.
2. User-created tags.
3. High-confidence local tags.
4. Advanced AI tags.
5. Title and memo matches.

