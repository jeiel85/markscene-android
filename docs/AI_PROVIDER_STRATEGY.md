# AI Provider Strategy

## Strategy Summary

MarkScene must not depend on a developer-owned AI key.

The app uses two levels of intelligence:

1. Basic local tagging without an API key.
2. Optional BYOK advanced AI analysis.

## Why BYOK

BYOK reduces developer operating cost and avoids putting a secret key into a public GitHub repository. It also makes the data flow easier to explain: the user's selected image goes from the user's device to the user's chosen AI provider when the user requests advanced analysis.

## Product Rule

API key setup must not be part of mandatory onboarding.

The user should first experience the basic product:

1. Capture or select a photo.
2. See local tag suggestions.
3. Save a record.

Then the app may present advanced AI analysis as an optional enhancement.

## Provider Abstraction

```kotlin
enum class AiProvider {
    Gemini
}

interface AdvancedVisionProvider {
    val provider: AiProvider

    suspend fun analyzeImage(
        request: AdvancedAnalysisRequest,
        apiKey: String
    ): Result<AdvancedAnalysis>
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

## Gemini Provider

Gemini support can be added after the core local flow exists.

Rules:

- Read API key from `ApiKeyStore`.
- Never hardcode a real key.
- Resize/compress images before sending.
- Ask for structured JSON output.
- Treat AI output as suggestions.
- Allow user editing before saving.
- Show explicit external-analysis warning.

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
- Invalid API key.
- Network error.
- Provider quota error.
- Safety block.
- JSON parse failure.
- Empty or low-confidence result.

Each state must have a user-friendly message and must not affect local records.

