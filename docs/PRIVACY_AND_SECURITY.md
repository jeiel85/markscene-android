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
- Optional local VLM model file downloaded by the app after user action.
- Optional model-download read token when the configured model source requires license-gated access.

### Sent Outside the Device

MarkScene does not send photos, prompts, or analysis output to an external AI provider.

Network access is used only for app-requested model download from the configured HTTPS model source. If the source requires authorization, the download-only read token is sent to that model host as an Authorization header.

The MVP must not send data to a developer-owned backend.

### Processed On Device For Advanced Local AI

When a local VLM model is configured:

- The selected image is analyzed on device.
- The prompt and model output stay on device.
- The model file is stored in app-private storage.
- Results are still suggestions and can be wrong.

## Token Rules

- External AI analysis API keys are not supported.
- Model-download tokens are optional and used only for license-gated model downloads.
- Model-download tokens must be stored only on the device.
- Model-download tokens must be encrypted using Android Keystore-backed storage or equivalent.
- Model-download tokens must never be logged.
- Model-download tokens must never be committed.
- Model-download tokens must never be included in crash reports.
- Model-download tokens must be deletable from settings.

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
Advanced AI analysis uses a local model when you download one.
```

### Before Local VLM Analysis

Suggested copy:

```text
This image will be analyzed by the local AI model on this device.
It will not be sent to an external server, but analysis may take time and the result is only a suggestion.
```

### Model Download Token Setup

Suggested copy:

```text
Your model download token is stored only on this device and is used only to download the selected local AI model.
MarkScene does not use this token for photo analysis.
```

## Logging Rules

Never log:

- Model download tokens.
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

