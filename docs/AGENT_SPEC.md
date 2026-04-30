# Agent Spec

## Mission

Build MarkScene as a fast, clean, privacy-first Android app for photo-based visual records.

The first release must prove the core loop:

1. Capture or select a photo.
2. Generate basic on-device tags.
3. Let the user edit the tags.
4. Save the record locally.
5. Search saved records later.

Advanced AI analysis is optional and must not block the basic experience.

## Operating Mode

Work in small vertical slices. Prefer one end-to-end feature over many incomplete abstractions.

For every iteration:

1. Read the relevant docs.
2. Check `.agent/tasks.md`.
3. Choose the smallest useful task.
4. Implement it.
5. Run available checks.
6. Update `.agent/progress.md`.
7. Add decisions to `.agent/decisions.md` only when they are durable.

## Definition of Done

A task is done only when:

- It compiles or the exact compile blocker is documented.
- It does not violate privacy rules.
- It does not require a real API key to run the core app.
- UI states exist for loading, empty, success, and error where relevant.
- The change is small enough to review.
- Documentation is updated when behavior changes.

## Forbidden Shortcuts

- Do not add broad permissions to make implementation easier.
- Do not skip the local-first baseline and jump directly to cloud AI.
- Do not hardcode sample API keys.
- Do not put secrets into build files.
- Do not create hidden network calls.
- Do not make AI output non-editable.

## Preferred First Implementation Strategy

1. Create minimal Android project structure.
2. Add Compose navigation shell.
3. Implement local record model and Room database.
4. Add photo selection via Photo Picker.
5. Add local tag generation interface with a mock implementation.
6. Add editable tag chips.
7. Add record saving and list screen.
8. Add search.
9. Add real ML Kit local tagging.
10. Add optional BYOK Gemini provider.

