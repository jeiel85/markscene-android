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

- The app may support Gemini-based advanced analysis after API key setup.
- Advanced analysis must be optional.
- Advanced analysis must require explicit user action.
- Before external analysis, the app must disclose that the selected image will be sent to the AI provider.

### R7. Settings

- The app must include a settings screen.
- The settings screen must show whether an API key is configured.
- The user must be able to add, test, and delete an API key.
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
- API key absence must show a setup state, not a crash.

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

