# Agent Tasks

## Epic Issues

| Epic | GitHub Issue | 파일 |
|------|-------------|------|
| UI/UX & Design | [#14](https://github.com/jeiel85/markscene-android/issues/14) | `.agent/epic1_ui_ux.md` |
| Core Features & AI | [#19](https://github.com/jeiel85/markscene-android/issues/19) | `.agent/epic2_core_ai.md` |
| Performance & Stability | [#16](https://github.com/jeiel85/markscene-android/issues/16) | `.agent/epic3_perf_stab.md` |
| Security, Privacy & Trust | [#17](https://github.com/jeiel85/markscene-android/issues/17) | `.agent/epic4_sec_priv.md` |
| Marketing, Growth & Retention | [#20](https://github.com/jeiel85/markscene-android/issues/20) | `.agent/epic5_market.md` |
| Receive Intent | [#10](https://github.com/jeiel85/markscene-android/issues/10) | — |

## Current Priority

v2.2.0(2026-05-20)에서 우선순위 3종(스와이프 삭제 실 연결, 개인정보 대시보드 통합, 앱 전체 스크린샷 차단 토글) 통합 완료.
v2.3.0(2026-05-20)에서 추가 3종(Today 로컬 처리 배지, API Key 입력 보안 강화, 카메라 권한 사전 안내 자동화) 통합 완료.
v2.4.0(2026-05-20)에서 우선순위 10종(이미지 파이프라인 OOM 방어, Coil 로더 최적화, Room FTS, 리스트 Jank, Baseline Profile, 저장공간 관리, Dynamic Color 테마, Dark Mode 폴리시, 검색 자동완성, APK 다이어트) 통합 완료.
2026-05-26에 로컬 VLM 고급 분석(모델 다운로드/설정, 상세 화면 로컬 AI 우선 실행, 개인정보 문구 갱신) 구현 완료.
다음 우선순위: Epic #14 UI/UX(Shared Element, 온보딩, 드래그 앤 드롭, 다중 레이아웃), Epic #16 남은 항목(배터리, 카메라 속도, 네트워크 복구), Epic #17 보안(갤러리 숨김, EXIF 제거, 데이터 내보내기), Epic #19 기능(스마트 앨범, 위젯, 프롬프트 템플릿, 크롭, 오디오), Epic #20 마케팅(ASO, 리뷰 유도, 공유 템플릿, 주간 회고).

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
- [x] Add local VLM model download and on-device advanced analysis path.

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
