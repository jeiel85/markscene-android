# AI Provider Strategy

## Strategy Summary

MarkScene must not depend on a developer-owned AI key.

The app uses three levels of intelligence:

1. Basic local tagging without an API key.
2. Optional on-device VLM advanced analysis.
3. Optional BYOK external advanced AI analysis.

## Preferred Advanced Path

The preferred advanced path is local VLM analysis when the app downloads an approved compatible model file.

Current implementation:

- Settings can download a MediaPipe LLM Inference compatible local model file into app-private storage.
- Record detail advanced analysis prefers the local VLM model when it is available.
- Local VLM analysis uses image + prompt input and asks for strict JSON.
- Suggested objects and tags are saved as editable suggestions with `LocalVlm` tag source.
- If no local model is configured, the app can fall back to BYOK Gemini when an API key exists.

The app must not bundle a multi-GB model in the APK unless a future release explicitly accepts the store-size and licensing impact.

## Why BYOK

BYOK remains the external-provider fallback. It reduces developer operating cost and avoids putting a secret key into a public GitHub repository. It also makes the data flow easier to explain: the user's selected image goes from the user's device to the user's chosen AI provider only when the user requests external advanced analysis.

## Product Rule

API key setup must not be part of mandatory onboarding.

The user should first experience the basic product:

1. Capture or select a photo.
2. See local tag suggestions.
3. Save a record.

Then the app may present advanced AI analysis as an optional enhancement. Local VLM should be shown before external BYOK because it better matches the local-first privacy promise.

## Provider Abstraction

```kotlin
enum class AiProvider {
    LocalVlm,
    Gemini
}

interface AdvancedVisionProvider {
    val provider: AiProvider

    suspend fun analyzeImage(request: AdvancedAnalysisRequest): Result<AdvancedAnalysis>
}
```

## Mock Provider First

Before adding a real Gemini client, implement `MockAdvancedVisionProvider`.

Mock output should include:

- Scene summary.
- Object list.
- Suggested tags.
- Warnings.

This allows UI and persistence work without a real API key.

## Local VLM Provider

Local VLM support uses MediaPipe LLM Inference on Android.

Rules:

- Use only explicitly approved model files from HTTPS download sources.
- Store the model in app-private storage.
- Do not upload the selected image, prompt, model path, or model output.
- Resize images before local inference to reduce latency and memory pressure.
- Prefer high-end devices for acceptable performance.
- Treat output as suggestions and allow editing before saving.
- Show that local analysis can be slow and may be inaccurate.

Known constraints:

- The app needs a MediaPipe-compatible model file; arbitrary GGUF or Hugging Face files do not run directly.
- Large VLMs can require several GB of storage/RAM and may fail on low-memory phones.
- JSON output can still be malformed, so parsing must fail gracefully.

## Gemini Provider

Gemini support is the external fallback after the core local flow exists.

Rules:

- Read API key from `ApiKeyStore`.
- Never hardcode a real key.
- Resize/compress images before sending.
- Ask for structured JSON output.
- Treat AI output as suggestions.
- Allow user editing before saving.
- Show explicit external-analysis warning.
- Do not use Gemini when a valid local VLM model is configured unless the user explicitly chooses the external provider in a future provider selector.

## Suggested Advanced Analysis Schema

```json
{
  "sceneSummary": "string",
  "sceneType": "desk|room|kitchen|storage|outdoor|document|food|unknown",
  "objects": [
    {
      "nameKo": "string",
      "nameEn": "string",
      "count": 1,
      "position": "left|center|right|top|bottom|unknown",
      "confidence": "high|medium|low"
    }
  ],
  "suggestedTags": ["string"],
  "warnings": ["string"]
}
```

## Suggested Prompt Pattern

```text
Analyze this image as a personal visual note.
Return only valid JSON matching the requested schema.
Do not claim certainty when uncertain.
Prefer common object names.
If object count is uncertain, use null.
Use cautious wording.
```

## Failure States

- Missing API key.
- Missing local model.
- Unsupported or invalid local model.
- Device memory/runtime failure.
- Invalid API key.
- Network error.
- Provider quota error.
- Safety block.
- JSON parse failure.
- Empty or low-confidence result.

Each state must have a user-friendly message and must not affect local records.

