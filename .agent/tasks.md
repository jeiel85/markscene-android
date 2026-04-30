# Agent Tasks

## Current Priority

Build the smallest useful MVP vertical slice.

## Task Backlog

### Bootstrap

- [x] Create or verify Android project skeleton.
- [x] Add Jetpack Compose baseline.
- [x] Add Material 3 theme.
- [x] Add basic navigation shell.
- [ ] Add placeholder app icon/name only if needed.

### Core Records

- [x] Define domain models for `PhotoRecord` and `PhotoTag`.
- [x] Add Room database entities and DAO.
- [x] Add repository for records.
- [x] Add fake in-memory repository for previews/tests if useful.

### Photo Input

- [x] Add Photo Picker import flow.
- [ ] Add CameraX capture flow.
- [x] Store selected/captured image reference safely.
- [x] Show image preview immediately.

### Local Tagging

- [x] Add `LocalImageTagger` interface.
- [x] Add mock local tagger.
- [x] Generate mock tags after image selection.
- [x] Add editable tag chips.
- [x] Save edited tags with record.
- [x] Replace mock with real on-device tagger later.

### Search

- [x] Add record list screen.
- [x] Add search screen.
- [x] Search by tag/title/memo.

### Settings and BYOK

- [x] Add settings screen.
- [x] Add API key status UI.
- [x] Add encrypted API key storage.
- [x] Add API key delete action.
- [x] Add mock advanced AI provider.
- [x] Add external analysis warning.
- [x] Add Gemini provider after mock flow is stable.

### Privacy and Release

- [x] Add in-app privacy notice.
- [ ] Add no-secrets `.gitignore` entries.
- [ ] Add release checklist.
- [ ] Add privacy policy draft before store release.

## Recommended First Task

Create the Android project skeleton and implement a Compose home screen with three actions:

1. Capture photo.
2. Import photo.
3. Open settings.

No AI provider is needed for the first task.

### Repository / Agent Workflow

- [x] Replace `<TO_BE_FILLED>` in `AGENTS.md` with the real GitHub repository URL.
- [ ] Decide initial license and add `LICENSE` if the repository will be public.
- [x] Add GitHub Actions workflow for fast Android validation.
- [ ] Confirm `HISTORY.md` and `CHANGELOG.md` are updated after each meaningful change.
- [ ] Add `CLAUDE.md`, `GEMINI.md`, or other agent entry files only as thin references to `AGENTS.md` if needed.
