# UX Flow

## Core Flow

```text
Open app
  -> Home screen
  -> Capture photo or select photo
  -> Show image immediately
  -> Generate local tags
  -> Show editable tag chips
  -> User edits tags or memo
  -> Save record
  -> Record appears in timeline
  -> Search finds record later
```

## First Run

Do not force API key setup. The product does not expose external AI API key setup.

Suggested first-run flow:

```text
Welcome
  -> Explain local-first visual notes
  -> Start
  -> Home
```

Suggested copy:

```text
Turn photos into searchable visual notes.
Basic tagging works on your device. Advanced AI analysis uses a local model when you download one.
```

### Onboarding Local Model Guidance

- Explain that the base app install remains lightweight because large local AI models are not bundled.
- Tell users that advanced photo analysis is optional and can be enabled later by downloading a recommended model over WiFi.
- Provide a direct action from the model guidance page to Settings so motivated users can review the license/token/download steps immediately.

## Home Screen

Primary actions:

- Capture.
- Import.
- Search.

Content:

- Recent records.
- Empty state when no records exist.

Empty state copy:

```text
No visual notes yet.
Take a photo or select one from your library to create your first searchable record.
```

## Capture/Import Result Screen

Show immediately:

- Image preview.
- Loading tag state.
- Memo field.
- Save button disabled until image is ready.

After local tags:

- Editable tag chips.
- Add tag button.
- Optional advanced AI CTA.

Advanced AI CTA copy:

```text
Want deeper analysis?
Download the local AI model to generate a detailed scene summary and object list on this device.
```

## Tag Editing

Tap a chip to open a small edit sheet:

- Tag name.
- Delete.
- Save.

Optional later fields:

- Count.
- Position.
- Confidence.

## Record Detail Screen

Display:

- Image.
- Title.
- Memo.
- Tags.
- Analysis summary if available.
- Created time.
- Delete action.

## Search Screen

Search by:

- Tag.
- Title.
- Memo.

Results should show:

- Thumbnail.
- Top tags.
- Date.
- Title or memo excerpt.

## Settings Screen

Sections:

1. Advanced AI.
2. Privacy.
3. Appearance.
4. About.

Advanced AI section:

- Local model status.
- Download or re-download local model.
- Delete local model.
- Store or delete a model-download read token when required by the model source.

## Local Analysis Warning

Before running local VLM analysis:

```text
This image will be analyzed by the local AI model on this device.
It will not be sent to an external server, but analysis may take time and the result is only a suggestion.
```

Actions:

- Analyze.
- Cancel.

