# AI Provider Strategy

## Strategy Summary

MarkScene is local-first and must not depend on external AI API keys for photo analysis.

The app uses two levels of intelligence:

1. Basic on-device tagging/OCR without any key.
2. Optional on-device VLM advanced analysis after the app downloads an approved compatible model file.

Gemini/BYOK external provider fallback is removed from the product and code path.

## Local VLM Path

Current implementation:

- Settings downloads a MediaPipe LLM Inference compatible local model file into app-private storage.
- The default configured model is Google Gemma 3n E2B INT4 LiteRT-LM.
- License-gated model downloads may require a HuggingFace read token; this token is for model download only, not for analysis.
- If a license-gated model is configured, Settings should show a guided setup path with direct links for model-license acceptance and read-token creation.
- Settings presents a small model catalog. Only models verified for MediaPipe compatibility and the required modality should expose a download action; text-only or unverified models remain marked as planned or compatibility-check items.
- Record detail advanced analysis uses the local VLM model only.
- Local VLM analysis uses image + prompt input and asks for cautious structured JSON.
- Suggested objects and tags are saved as editable suggestions with `LocalVlm` tag source.
- Visual Q&A uses the same local VLM model when the model is available.

The app must not bundle a multi-GB model in the APK unless a future release explicitly accepts the store-size and licensing impact.

## Product Rule

API key setup must not be part of onboarding, settings, or record detail flows.

The user should first experience the basic product:

1. Capture or select a photo.
2. See local tag suggestions.
3. Save a record.

Then the app may present local advanced AI as an optional enhancement. If the local model is missing, the app should guide the user to download it instead of falling back to an external provider.

## Local VLM Rules

- Use only explicitly approved model files from HTTPS download sources.
- Store the model in app-private storage.
- Do not upload the selected image, prompt, model path, or model output.
- Resize images before local inference to reduce latency and memory pressure.
- Prefer high-end devices for acceptable performance.
- Treat output as suggestions and allow editing before saving.
- Show that local analysis can be slow and may be inaccurate.
- Do not log prompts, image bytes, local model raw responses, or token values.

Known constraints:

- The app needs a MediaPipe-compatible model file; arbitrary GGUF or Hugging Face files do not run directly.
- Large VLMs can require several GB of storage/RAM and may fail on low-memory phones.
- JSON output can still be malformed, so parsing must fail gracefully.
- Downloading a gated model may require the user to accept the model license and provide a read token.

## Removed External Providers

The app does not provide:

- Gemini API key input.
- External provider connection test.
- External advanced analysis warning.
- External provider fallback when the local model is missing.
- Developer-owned API keys.

Any future reintroduction of external analysis must be treated as a policy change, documented in this file, and reviewed against privacy/store requirements before implementation.

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
Analyze this image as a private visual note.
Return only valid JSON matching the requested schema.
Do not claim certainty when uncertain.
Prefer common object names.
If object count is uncertain, use null.
Use cautious wording.
```

## Failure States

- Missing local model.
- Missing model download token for a license-gated model.
- Unsupported or invalid local model.
- Device memory/runtime failure.
- Model download network error.
- Model download permission error.
- JSON parse failure.
- Empty or low-confidence result.

Each state must have a user-friendly message and must not affect local records.
