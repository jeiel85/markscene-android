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

Do not force API key setup.

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
Basic tagging works on your device. Advanced AI analysis is optional.
```

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
Connect your own AI API key to generate a detailed scene summary and object list.
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

- Provider.
- API key status.
- Add/update API key.
- Test connection.
- Delete API key.
- External analysis warning toggle if implemented.

## External Analysis Warning

Before sending an image to an AI provider:

```text
This image will be sent to your selected AI provider for analysis.
Do not continue if the image contains sensitive information you do not want to share with that provider.
```

Actions:

- Analyze.
- Cancel.

