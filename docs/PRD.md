# Product Requirements Document

## Objective

Build a first release of MarkScene that validates the core visual record workflow.

## MVP User Story

As a user, I want to take or select a photo, receive automatic tag suggestions, edit them, save the result, and find it later by searching tags or notes.

## Functional Requirements

### R1. Photo Capture

- The app must allow the user to capture a new photo.
- Captured photos must be shown immediately after capture.
- Captured photos must be saved only in app-controlled storage unless the user explicitly exports them.

### R2. Photo Import

- The app must allow the user to select a photo through Android Photo Picker.
- The app must not require broad gallery permissions for basic import.
- The app must only process selected images.

### R3. Local Tag Suggestions

- The app must generate basic tag suggestions without requiring an API key.
- Tag suggestions must be editable.
- Tag suggestions must be removable.
- The user must be able to add custom tags.

### R4. Record Saving

- The app must save a visual record locally.
- A record includes image reference, title, memo, tags, creation time, and analysis metadata.
- Records must be deletable.

### R5. Search

- The app must allow local search by tag, title, and memo.
- Search must work without network access.

### R6. Optional Advanced AI

- The app may support local VLM advanced analysis after the app downloads an approved compatible model file.
- The app must not support Gemini/BYOK external advanced analysis.
- Advanced analysis must use the configured local VLM model only.
- Advanced analysis must be optional.
- Advanced analysis must require explicit user action.
- Before local VLM analysis, the app must disclose that processing happens on device, may take time, and remains a suggestion.

### R7. Settings

- The app must include a settings screen.
- The settings screen must allow downloading or deleting a local VLM model file when local advanced AI is supported.
- The settings screen must not expose external AI API key setup.
- If the configured model source requires license-gated download access, the settings screen may store/delete a download-only read token.
- If license-gated access is required, the settings screen should guide the user through license acceptance, read-token creation, token storage, and download in a clear sequence.
- The user must be able to view privacy-related information.

## Non-Functional Requirements

### Performance

- The app should show selected/captured images immediately.
- Analysis should run asynchronously.
- The user should not be blocked by full-screen loading when avoidable.

### Privacy

- Local-first by default.
- No automatic upload.
- No broad media scanning.
- No background location.
- No analytics in MVP.

### Reliability

- Network failure must not break local features.
- AI provider failure must show a recoverable error.
- Missing local model state must show setup guidance, not a crash.

## Out of Scope for MVP

- Cloud sync.
- Account system.
- Team sharing.
- Web app.
- Automatic whole-gallery indexing.
- Background analysis.
- Subscription/payment system.
- Production backend.

## Success Criteria

The MVP is successful if a user can complete this flow in under one minute:

1. Open app.
2. Capture or select photo.
3. See tag suggestions.
4. Edit at least one tag.
5. Save the record.
6. Find the record through search.

