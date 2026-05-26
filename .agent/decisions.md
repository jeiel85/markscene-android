# Agent Decisions

## D001: Basic Experience Must Work Without API Key

Decision: The app must provide a useful basic experience without requiring an AI API key.

Reason: If API key setup is mandatory, the initial product experience becomes too weak and too technical for general users.

Implication: Local tag suggestions, manual editing, local saving, and local search are MVP requirements.

## D002: BYOK for Advanced AI

Decision: Advanced AI analysis uses a Bring Your Own Key model.

Reason: The repository will be public, and the developer should not embed or pay for a default AI API key in MVP.

Implication: API key storage, warnings, and provider abstraction are required before real advanced AI is enabled.

## D003: Local-First Storage

Decision: Records, tags, and analysis results are stored locally by default.

Reason: The app handles potentially sensitive photos and personal notes.

Implication: No backend is used in MVP.

## D004: Photo Picker Over Broad Gallery Permission

Decision: Gallery import should use Android Photo Picker.

Reason: The app only needs images selected by the user, not broad access to the media library.

Implication: Do not add broad media permissions unless explicitly approved later.

## D005: AI Results Are Suggestions

Decision: AI and local tag outputs must be editable suggestions, not guaranteed facts.

Reason: Image analysis can be wrong, especially for counts, small objects, or ambiguous scenes.

Implication: UI must allow editing, deletion, and manual additions.

## D006: Prefer Local VLM Before External BYOK

Decision: Advanced photo analysis should prefer a user-imported on-device VLM model when available, and use BYOK Gemini only as the external fallback.

Reason: Local VLM analysis better matches MarkScene's privacy promise and avoids requiring an external API key for richer object/tag suggestions.

Implication: Settings must manage the local model file, record detail must disclose on-device processing, and local VLM output must be stored as editable suggestions with a distinct source.


## 2026-04-30 - Project Naming

Decision:
- Use `MarkScene` as the app and project name.
- Use `markscene-android` as the default GitHub repository name.
- Use `com.markscene.app` as the default Android `applicationId` and root package.

Reason:
- The name communicates the core product idea: marking and remembering scenes from photos.
- It fits the local-first visual memo concept while leaving room for object tagging, scene records, and optional AI analysis.
