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

## Phase 3: Local VLM Advanced AI

Goal: Add optional on-device advanced analysis with an app-downloaded local VLM model.

Tasks:

- Settings screen for local model download/delete.
- Encrypted model-download token storage when a gated model source requires it.
- Local advanced analysis consent dialog.
- Mock advanced AI provider.
- Local VLM provider.
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

## Phase 5: Top 10 Epic Strategy

MVP 이후 플레이 스토어 Top 10 진입을 위한 5개 Epic 전략입니다. 각 Epic은 GitHub Issue로 추적합니다.

| Epic | Issue |
|------|-------|
| UI/UX & Design (사용자 경험 및 디자인 개선) | [#14](https://github.com/jeiel85/markscene-android/issues/14) |
| Core Features & AI (핵심 기능 및 AI 활용) | [#19](https://github.com/jeiel85/markscene-android/issues/19) |
| Performance & Stability (성능 및 안정성) | [#16](https://github.com/jeiel85/markscene-android/issues/16) |
| Security, Privacy & Trust (보안, 프라이버시 및 신뢰) | [#17](https://github.com/jeiel85/markscene-android/issues/17) |
| Marketing, Growth & Retention (마케팅, 성장 및 리텐션) | [#20](https://github.com/jeiel85/markscene-android/issues/20) |

기타 기능 이슈:

| Feature | Issue |
|---------|-------|
| 외부 공유로 기록하기 (Receive Intent) | [#10](https://github.com/jeiel85/markscene-android/issues/10) |

