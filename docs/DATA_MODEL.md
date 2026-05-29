# Data Model

## Core Entities

### PhotoRecord

Represents one saved visual note.

Fields:

- `id`: stable unique ID.
- `imageUri`: app-owned or selected image reference.
- `title`: nullable user title.
- `memo`: nullable user memo.
- `createdAt`: timestamp.
- `updatedAt`: timestamp.
- `analysisStatus`: none, localComplete, advancedComplete, failed.

### PhotoTag

Represents one tag attached to a record.

Fields:

- `id`: stable unique ID.
- `recordId`: parent record ID.
- `name`: normalized display name.
- `rawName`: nullable raw source label.
- `source`: mock, local_image_label, local_object_detection, local_vlm, user, advanced_ai.
- `confidence`: nullable float.
- `userConfirmed`: boolean.
- `createdAt`: timestamp.

### AdvancedAnalysis

Optional analysis result from the local VLM provider.

Fields:

- `id`: stable unique ID.
- `recordId`: parent record ID.
- `provider`: local_vlm.
- `modelName`: nullable string.
- `sceneSummary`: nullable string.
- `createdAt`: timestamp.
- `status`: success or failed.

### DetectedObject

Optional structured object result.

Fields:

- `id`: stable unique ID.
- `recordId`: parent record ID.
- `analysisId`: nullable advanced analysis ID.
- `name`: display name.
- `rawName`: nullable raw name.
- `count`: nullable int.
- `position`: nullable string.
- `confidenceLabel`: high, medium, low, unknown.
- `source`: local_object_detection, local_vlm, advanced_ai, user.
- `userConfirmed`: boolean.

## Suggested Kotlin Models

```kotlin
data class PhotoRecord(
    val id: String,
    val imageUri: String,
    val title: String?,
    val memo: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val analysisStatus: AnalysisStatus
)

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

enum class TagSource {
    Mock,
    LocalImageLabel,
    LocalObjectDetection,
    LocalVlm,
    User,
    AdvancedAi
}

enum class AnalysisStatus {
    None,
    LocalComplete,
    AdvancedComplete,
    Failed
}
```

## Persistence Notes

- Use Room for records and tags.
- Use DataStore for simple non-secret settings.
- Use encrypted storage for model-download tokens when required.
- Keep model-download tokens out of Room.
- Keep raw AI responses out of storage unless needed for debugging and explicitly sanitized.

## Deletion Behavior

When deleting a record:

- Delete related tags.
- Delete related advanced analysis.
- Delete app-owned image copy if the app created one.
- Do not delete original gallery images selected through Photo Picker unless the app owns the copy.

