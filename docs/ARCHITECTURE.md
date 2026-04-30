# Architecture

## Architecture Goals

- Local-first.
- Fast perceived response.
- Clear separation between UI, domain, data, and AI providers.
- Replaceable AI provider layer.
- No dependency on a real API key for core development.

## Suggested Package Structure

```text
app/
  core/
    database/
    datastore/
    design/
    image/
    model/
    permissions/
    security/
    util/
  data/
    record/
    tag/
    settings/
  domain/
    record/
    tag/
    analysis/
  ai/
    provider/
    prompt/
    parser/
  feature/
    home/
    camera/
    picker/
    analysis/
    recorddetail/
    search/
    settings/
```

## Layers

### UI Layer

- Jetpack Compose screens.
- ViewModels expose immutable UI state.
- UI sends user actions to ViewModels.
- UI does not directly call ML Kit, network clients, or databases.

### Domain Layer

- Use cases for saving records, generating tags, searching records, and running advanced analysis.
- Contains business rules such as confidence filtering and tag normalization.

### Data Layer

- Room database.
- DataStore for non-secret settings.
- Encrypted storage for API keys.
- Repositories hide persistence details from domain and UI.

### AI Layer

- Local tagger interface.
- Advanced AI provider interface.
- Mock providers for development and testing.
- Gemini implementation added only after the core flow works.

## Core Interfaces

```kotlin
interface LocalImageTagger {
    suspend fun generateTags(input: ImageInput): List<TagSuggestion>
}

interface AdvancedVisionProvider {
    suspend fun analyzeImage(request: AdvancedAnalysisRequest): Result<AdvancedAnalysis>
}

interface ApiKeyStore {
    suspend fun saveApiKey(provider: AiProvider, apiKey: String)
    suspend fun getApiKey(provider: AiProvider): String?
    suspend fun clearApiKey(provider: AiProvider)
}
```

## Data Flow

```text
Photo capture/import
  -> image stored in app storage
  -> local tag generation starts
  -> UI shows image immediately
  -> tag suggestions appear as editable chips
  -> user edits tags
  -> record saved in Room
  -> records searchable locally
```

## Advanced Analysis Flow

```text
User taps Advanced AI Analysis
  -> check API key
  -> show external analysis warning if needed
  -> resize/compress selected image
  -> call selected AI provider
  -> parse structured result
  -> show suggestions as editable data
  -> user confirms or edits
  -> save result locally
```

## Permission Strategy

MVP should only need camera permission for capture. Gallery import should use Android Photo Picker.

Avoid:

- Broad storage permissions.
- Broad photo/video permissions.
- Background location.
- Manage all files access.

## Error Handling Principles

- No API key: show setup prompt.
- Network error: keep local tags and show retry.
- AI parse error: show raw failure state only in debug builds; user-facing message should be simple.
- Local database error: show non-destructive error and avoid data loss.

