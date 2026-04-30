# Agent Tasks

## Current Priority

Complete remaining issues #2~#6, add test automation, and improve documentation.

## Task Backlog

### Bootstrap

- [x] Create or verify Android project skeleton.
- [x] Add Jetpack Compose baseline.
- [x] Add Material 3 theme.
- [x] Add basic navigation shell.
- [x] Add placeholder app icon/name only if needed.

### Core Records

- [x] Define domain models for `PhotoRecord` and `PhotoTag`.
- [x] Add Room database entities and DAO.
- [x] Add repository for records.
- [x] Add fake in-memory repository for previews/tests if useful.

### Photo Input

- [x] Add Photo Picker import flow.
- [x] Add CameraX capture flow. (#1)
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
- [x] Add no-secrets `.gitignore` entries. (#2) - verified coverage sufficient
- [x] Add release checklist. (#4) - updated with current status
- [x] Add privacy policy draft before store release. (#3) - created docs/PRIVACY_POLICY.md

### Branding and Web

- [x] Replace default launcher icon with product identity icon.
- [x] Add GitHub Pages branding site (`docs/branding`) and deploy workflow.
- [x] Improve README.md with comprehensive content.
- [x] Improve docs/index.html branding page.

### Repository / Agent Workflow

- [x] Replace `<TO_BE_FILLED>` in `AGENTS.md` with the real GitHub repository URL.
- [x] Decide initial license and add `LICENSE` if the repository will be public. (#5) - MIT License added
- [x] Add GitHub Actions workflow for fast Android validation.
- [x] Confirm `HISTORY.md` and `CHANGELOG.md` are updated after each meaningful change. (#6)
- [x] Add `CLAUDE.md`, `GEMINI.md`, or other agent entry files only as thin references to `AGENTS.md` if needed. (#6)
- [x] Add basic unit tests for CI automation.

### Security

- [x] Verify `.gitignore` covers all sensitive patterns. (#2)
- [x] Add LICENSE file (MIT). (#5)
