# Privacy and Security

## Privacy Position

MarkScene is local-first. The basic experience must work without sending user photos, notes, tags, location, or API keys to a developer-owned server.

## Data Categories

### Stored On Device

- Selected or captured image reference or app-owned image copy.
- User-created title.
- User-created memo.
- Tag suggestions.
- User-confirmed tags.
- Analysis metadata.
- Optional API key for advanced AI provider.
- Optional local VLM model file downloaded by the app after user action.

### Sent Outside the Device

Only when the user explicitly runs external BYOK advanced AI analysis:

- Selected image or resized copy.
- Prompt needed for image analysis.
- API key required by the selected provider.

The MVP must not send data to a developer-owned backend.

### Processed On Device For Advanced Local AI

When a local VLM model is configured:

- The selected image is analyzed on device.
- The prompt and model output stay on device.
- The model file is stored in app-private storage.
- Results are still suggestions and can be wrong.

## API Key Rules

- API keys are optional.
- API keys must be stored only on the device.
- API keys must be encrypted using Android Keystore-backed storage or equivalent.
- API keys must never be logged.
- API keys must never be committed.
- API keys must never be included in crash reports.
- API keys must be deletable from settings.

## Photo Handling Rules

- Do not automatically upload photos.
- Do not scan the whole gallery.
- Do not process photos that the user did not capture or select.
- Use Android Photo Picker for gallery selection.
- Store app-created images in app-specific storage unless export is explicitly added.
- Allow users to delete saved records.

## Location Rules

Location is out of scope for MVP unless explicitly approved.

If added later:

- Must be optional.
- Must be user-initiated.
- Must not run in the background.
- Must be clearly explained.
- Must be removable from a record.

## User-Facing Warnings

### First Run

Suggested copy:

```text
MarkScene creates searchable records from photos.
Basic tagging works on your device.
Advanced AI analysis is optional and may send the selected image to your chosen AI provider.
```

### Before Advanced AI Analysis

Suggested copy:

```text
This image will be sent to the selected AI provider for analysis.
Do not analyze images containing sensitive personal information unless you understand the provider's data terms.
```

### Before Local VLM Analysis

Suggested copy:

```text
This image will be analyzed by the local AI model on this device.
It will not be sent to an external server, but analysis may take time and the result is only a suggestion.
```

### API Key Setup

Suggested copy:

```text
Your API key is stored only on this device and is used only when you run advanced AI analysis.
MarkScene does not send your API key to a developer-owned server.
```

## Logging Rules

Never log:

- API keys.
- Full prompts.
- Image bytes.
- Base64 images.
- AI response bodies containing private content.
- Local VLM raw responses containing private content.
- Local file paths that may expose user information.

## Open Source Repository Safety

Do not commit:

- Real API keys.
- Signing keys.
- Keystores.
- `local.properties`.
- `.env`.
- `secrets.properties`.
- Generated release artifacts containing secrets.

## Release Reminder

Before publishing, verify the current Google Play policies, Android permission requirements, AI provider terms, and privacy policy wording. Policy requirements can change.

