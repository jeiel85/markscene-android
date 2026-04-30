# Roadmap

## Phase 0: Repository Bootstrap

Goal: Prepare a clean Android project that agents can safely iterate on.

Tasks:

- Create Android project skeleton.
- Add Kotlin and Compose baseline.
- Add Material 3 theme.
- Add README and docs.
- Add CI skeleton if practical.

## Phase 1: Core Local Visual Notes

Goal: Make the app useful without API keys.

Tasks:

- Home screen.
- Photo capture or Photo Picker.
- Local image preview.
- Mock local tagger.
- Editable tag chips.
- Room database.
- Save record.
- Record list.
- Record detail.
- Delete record.
- Search by tag/title/memo.

## Phase 2: Real Local Tagging

Goal: Replace mock local tagging with an on-device image labeler.

Tasks:

- Add local image labeler implementation.
- Add confidence filtering.
- Add tag normalization.
- Add error handling.
- Keep mock implementation for tests/previews.

## Phase 3: BYOK Advanced AI

Goal: Add optional Gemini-based advanced analysis.

Tasks:

- Settings screen for API key.
- Encrypted API key storage.
- API key test action.
- Advanced analysis consent dialog.
- Mock advanced AI provider.
- Gemini provider.
- Structured response parsing.
- Editable advanced suggestions.

## Phase 4: Polish

Goal: Make the app feel modern and reliable.

Tasks:

- Loading/empty/error states.
- Dark mode review.
- Accessibility pass.
- Performance pass.
- UX copy review.
- Privacy notice screen.
- Play Store checklist.

## Phase 5: Future Differentiators

Possible later features:

- Space-based organization: desk, kitchen, storage, child room.
- Compare records from the same space.
- "Where was this item?" search experience.
- User correction dictionary.
- Export to Markdown or CSV.
- Local backup/restore.
- Optional local model support.

