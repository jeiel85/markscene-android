# Agent Progress

## 2026-05-30 (v2.6.6 Release - Back Navigation Depth)

작업 내용:
- 앱 버전을 `2.6.6 / 266`으로 올렸습니다.
- 뒤로가기 depth 보완을 v2.6.6 릴리즈 기준으로 정리했습니다.
- README, GitHub Pages, Play Store listing 문서, release checklist, CHANGELOG를 v2.6.6 기준으로 갱신했습니다.

검증:
- CI: `34eaa22` 기준 Android CI에서 lint, unit test, debug build, Android test APK build, emulator instrumentation, launch smoke test 성공.
- 로컬: `./gradlew.bat :app:bundleRelease --no-daemon --stacktrace` 성공.
- Manifest: release bundle manifest에서 `versionCode=266`, `versionName=2.6.6` 확인.
- 산출물: 바탕화면 `Build` 폴더에 `MarkScene-v2.6.6-vc266.aab`와 `MarkScene-v2.6.6-vc266-release-notes.txt` 내보내기 완료.
- 릴리즈 노트 길이: `ko-KR` 129자, `en-US` 275자.
- 진행 중: v2.6.6 tag release workflow 확인.

## 2026-05-30 (Back Navigation Depth)

작업 내용:
- 앱 공통 뒤로가기 정책을 추가해 현재 화면의 이전 depth를 먼저 따르고, 스택이 얕은 하위/탭 화면은 Today로 복귀하도록 했습니다.
- 검색 목록 다중 선택 모드에서는 하드웨어 뒤로가기가 앱 종료나 화면 이동 대신 선택 해제를 먼저 수행하도록 했습니다.
- 뒤로가기 fallback 정책 단위 테스트를 추가했습니다.

검증:
- 로컬: `./gradlew.bat :app:compileDebugKotlin --no-daemon --stacktrace` 성공.
- 로컬: `./gradlew.bat :app:testDebugUnitTest --no-daemon --stacktrace` 성공.
- 로컬: `./gradlew.bat :app:lintDebug --no-daemon --stacktrace` 성공.
- CI: `34eaa22` 기준 Android CI에서 lint, unit test, debug build, Android test APK build, emulator instrumentation, launch smoke test 성공.

## 2026-05-30 (v2.6.5 Release - Optional Model Onboarding)

작업 내용:
- 앱 버전을 `2.6.5 / 265`로 올렸습니다.
- 온보딩 모델 선택 다운로드 안내를 v2.6.5 릴리즈 기준으로 정리했습니다.
- README, GitHub Pages, Play Store listing 문서, release checklist, CHANGELOG를 v2.6.5 기준으로 갱신했습니다.

검증:
- CI: `1051418` 기준 Android CI에서 lint, unit test, debug build, Android test APK build, emulator instrumentation, launch smoke test 성공.
- 로컬: `./gradlew.bat :app:bundleRelease --no-daemon --stacktrace` 성공.
- Manifest: release bundle manifest에서 `versionCode=265`, `versionName=2.6.5` 확인.
- 산출물: 바탕화면 `Build` 폴더에 `MarkScene-v2.6.5-vc265.aab`와 `MarkScene-v2.6.5-vc265-release-notes.txt` 내보내기 완료.
- 릴리즈 노트 길이: `ko-KR` 129자, `en-US` 265자.
- 진행 중: v2.6.5 tag release workflow 확인.

## 2026-05-30 (Onboarding Optional Model Download)

작업 내용:
- 온보딩에 "모델은 선택 다운로드" 페이지를 추가했습니다.
- 사용자가 고급 사진 분석을 바로 준비하고 싶을 때 온보딩에서 설정의 모델 다운로드 안내로 이동할 수 있게 했습니다.
- 앱 번들에 대용량 로컬 AI 모델을 포함하지 않는 정책을 PRD, AI 전략, UX 흐름 문서에 반영했습니다.

검증:
- 로컬: `./gradlew.bat :app:compileDebugKotlin --no-daemon --stacktrace` 성공.
- 로컬: `./gradlew.bat :app:lintDebug --no-daemon --stacktrace` 성공.
- 로컬: `./gradlew.bat :app:testDebugUnitTest --no-daemon --stacktrace` 성공.
- 로컬: `git diff --check` 성공.
- 로컬: 앱 리소스/asset 안에 대용량 모델 파일(`.task`, `.litertlm`, `.bin`, `.gguf`, `.tflite`)이 포함되어 있지 않음을 확인.
- CI: `1051418` 기준 Android CI에서 lint, unit test, debug build, Android test APK build, emulator instrumentation, launch smoke test 성공.

## 2026-05-29 (v2.6.4 Release - Model Download Accessibility)

작업 내용:
- 앱 버전을 `2.6.4 / 264`로 올렸습니다.
- 로컬 모델 다운로드 접근성 개선, 모델 카탈로그, CI emulator 안정화 변경을 v2.6.4 릴리즈 기준으로 정리했습니다.
- README, GitHub Pages, Play Store listing 문서, release checklist, CHANGELOG를 v2.6.4 기준으로 갱신했습니다.
- CI emulator 안정성을 위해 Android test APK 조립을 emulator 시작 전 단계로 분리했습니다.
- GitHub-hosted Ubuntu runner에서 emulator가 KVM 가속을 사용할 수 있도록 CI에 KVM 권한 활성화 단계를 추가했습니다.

검증:
- CI: `1afd81f` 기준 Android CI에서 lint, unit test, debug build, emulator instrumentation, launch smoke test 성공.
- 로컬: `./gradlew.bat :app:bundleRelease --no-daemon --stacktrace` 성공.
- 로컬: AAB manifest에서 `versionCode=264`, `versionName=2.6.4` 확인.
- 로컬: `C:\Users\jeiel\OneDrive\바탕 화면\Build`에 `MarkScene-v2.6.4-vc264.aab`와 `MarkScene-v2.6.4-vc264-release-notes.txt` 내보내기 성공.
- 로컬: Play Console용 TXT는 `<ko-KR>` 107자, `<en-US>` 220자로 각 500자 이하 확인.
- CI 1차: lint/unit/debug build는 성공했으나 software emulator package service가 `Broken pipe` 이후 사라져 APK 설치 실패. Android test APK 조립을 emulator 시작 전 단계로 분리해 재검증 예정.
- CI 2차: emulator 단계 부담을 줄인 뒤에도 KVM 없이 software emulation으로 부팅되어 package service가 사라짐. KVM 권한 활성화 단계 추가 후 재검증 예정.
- CI 3차: KVM 권한 활성화 후 Android CI에서 lint, unit test, debug build, Android test APK build, emulator instrumentation, launch smoke test 성공.
- 진행 중: v2.6.4 태그/Release APK 워크플로 확인.

## 2026-05-29 (Local Model Download Accessibility)

작업 내용:
- 설정 화면에서 로컬 고급 AI 모델을 받기 위한 절차를 단계별로 보이게 정리했습니다.
- HuggingFace 모델 라이선스 페이지와 토큰 발급 페이지를 각각 바로 열 수 있게 했습니다.
- 토큰이 없는 상태에서도 다운로드 버튼을 누르면 필요한 토큰 저장 안내가 나오도록 바꿔, 버튼 비활성화로 막힌 느낌을 줄였습니다.
- 모델 설정을 카탈로그 구조로 바꿔 사진 분석 권장 모델, 가벼운 텍스트 모델 후보, 직접 모델 가져오기 후보를 구분했습니다.
- 호환성이 확인된 사진 분석 권장 모델에만 다운로드 버튼을 연결하고, 나머지는 준비/검토 상태로 표시했습니다.
- CI emulator 통합 테스트가 덜 부팅된 기기에서 APK 설치를 시작하지 않도록 부팅 완료와 SDK 레벨 확인을 추가했습니다.
- Gradle `connectedDebugAndroidTest` 설치 경로 대신 debug/test APK 직접 설치와 `am instrument` 실행 경로로 CI 통합 테스트를 전환했습니다.
- 에뮬레이터 검증을 별도 bash 스크립트로 분리하고, `adb install` package service 일시 오류에 대비해 APK 설치 재시도를 추가했습니다.

검증:
- 로컬: `./gradlew.bat :app:compileDebugKotlin --no-daemon --stacktrace` 성공.
- 로컬: `./gradlew.bat :app:lintDebug --no-daemon` 성공.
- 로컬: `./gradlew.bat :app:testDebugUnitTest --no-daemon --stacktrace` 성공.
- 로컬: `./gradlew.bat :app:assembleDebugAndroidTest --no-daemon --stacktrace` 성공.
- 로컬: `git diff --check` 성공.
- 참고: `testDebugUnitTest`를 `lintDebug`와 병렬 실행한 첫 시도는 중간 JAR 동시 접근으로 실패했고, 테스트 단독 재실행은 성공.
- CI 1차: lint/unit/build는 성공, emulator 통합 테스트는 기기 API level을 1로 인식해 실패. 부팅 대기 가드 추가 후 재검증 예정.
- CI 2차: emulator runner가 script를 줄 단위로 실행해 다중 행 while 문법이 깨짐. 한 줄 `sh -c` 부팅 대기 명령으로 보정 후 재검증 예정.
- CI 3차: 부팅 가드는 통과했지만 `connectedDebugAndroidTest` 설치 단계가 emulator API level을 1로 읽어 실패. 직접 설치 + `am instrument` 경로로 보정 후 재검증 예정.
- CI 4차: debugAndroidTest 조립은 성공했지만 `adb install` 중 package service가 `Broken pipe`를 반환. 별도 스크립트와 설치 재시도로 보정 후 재검증 예정.

## 2026-05-29 (v2.6.3 Release - Play versionCode Recovery)

작업 내용:
- Play Console에서 `versionCode 262`가 이미 사용 중인 상태를 피하기 위해 앱 버전을 `2.6.3 / 263`으로 올렸습니다.
- v2.6.3 GitHub Release 본문은 `CHANGELOG.md`의 해당 섹션을 사용하고, Play Console용 TXT는 짧은 `<ko-KR>`/`<en-US>` 형식으로 내보냅니다.

검증:
- 로컬: `bundleRelease` 성공. AAB manifest에서 `versionCode=263`, `versionName=2.6.3` 확인.
- 로컬: `C:\Users\jeiel\OneDrive\바탕 화면\Build`에 `MarkScene-v2.6.3-vc263.aab`와 `MarkScene-v2.6.3-vc263-release-notes.txt` 내보내기 성공.
- 로컬: Play Console용 TXT는 `<ko-KR>` 265자, `<en-US>` 384자로 각 500자 이하 확인.
- 진행 중: v2.6.3 태그/Release APK 워크플로 확인.

## 2026-05-29 (v2.6.2 Release - Local VLM Only + Test Automation)

작업 내용:
- 앱 버전을 `2.6.2 / 262`로 올렸습니다.
- Gemini/BYOK 제거와 로컬 VLM 전용 전환을 v2.6.2 릴리즈 기준으로 정리했습니다.
- emulator 통합 테스트와 앱 실행 smoke test 자동화를 v2.6.2 릴리즈 검증 범위에 포함했습니다.

검증:
- 로컬: `compileDebugKotlin`, `testDebugUnitTest`, `lintDebug`, `assembleDebug`는 릴리즈 준비 전 성공.
- CI: Android CI에서 `lintDebug`, `testDebugUnitTest`, `assembleDebug`, emulator `connectedDebugAndroidTest`, launch smoke test 성공.
- 로컬: `bundleRelease` 성공. AAB manifest에서 `versionCode=262`, `versionName=2.6.2` 확인.
- 진행 중: v2.6.2 버전 커밋 후 태그/Release APK 워크플로 확인.

## 2026-05-29 (Local VLM Only Conversion)

작업 내용:
- Gemini/BYOK 고급 분석 provider와 API Key 입력/저장/삭제/테스트 UI를 제거했습니다.
- 설정 화면은 로컬 모델 다운로드/삭제와 모델 다운로드용 HuggingFace read token만 관리하도록 전환했습니다.
- 상세 화면의 고급 분석과 비주얼 Q&A가 다운로드된 로컬 VLM 모델만 사용하도록 연결했습니다.
- 기존 보안 저장소에 남아 있을 수 있는 Gemini API Key는 앱 시작 시 제거하도록 했습니다.
- README, GitHub Pages, 개인정보 문서, AI 전략 문서, 작업/결정 문서를 로컬 VLM 전용 정책으로 갱신했습니다.

검증:
- 로컬: `./gradlew.bat :app:compileDebugKotlin --no-daemon`, `./gradlew.bat :app:testDebugUnitTest --no-daemon`, `./gradlew.bat :app:lintDebug --no-daemon`, `./gradlew.bat :app:assembleDebug --no-daemon` 성공.
- 보류: 실제 기기에서 모델 다운로드 → 로컬 분석/Q&A end-to-end 검증은 라이선스 수락된 HuggingFace 토큰과 충분한 RAM이 필요해 보류.

## 2026-05-29 (Unit + Integration Test Automation)

작업 내용:
- Android CI의 emulator 단계에서 `connectedDebugAndroidTest`를 실행하도록 바꿔 instrumentation 통합 테스트를 자동화했습니다.
- 기존 adb 앱 실행 smoke test는 유지해 테스트 후 실제 MainActivity 실행까지 확인합니다.
- Room 기반 기록 저장/태그 검색/OCR 검색/삭제 cascade를 검증하는 `RecordDaoIntegrationTest`를 추가했습니다.

검증:
- 로컬: `./gradlew.bat :app:testDebugUnitTest --no-daemon`, `./gradlew.bat :app:lintDebug --no-daemon`, `./gradlew.bat :app:assembleDebug --no-daemon` 성공.
- CI 1차: emulator `connectedDebugAndroidTest`는 2개 테스트 모두 성공. 이후 launch smoke에서 테스트 실행 후 앱 APK가 제거되어 `MainActivity`를 찾지 못해 실패.
- 수정: 통합 테스트 후 debug APK를 다시 설치한 뒤 launch smoke를 실행하도록 워크플로 보정.

## 2026-05-26 (Local VLM Auto Download)

작업 내용:
- 설정 화면의 로컬 AI 모델 버튼이 파일 선택기를 열지 않고, 앱에 설정된 HTTPS 모델 URL에서 자동 다운로드하도록 바꿨습니다.
- 모델 다운로드 URL/표시 이름은 `MARKSCENE_LOCAL_VLM_MODEL_URL`, `MARKSCENE_LOCAL_VLM_MODEL_NAME` 빌드 설정으로 주입하도록 했습니다.
- Play Store 업로드용 `versionCode` 중복을 피하기 위해 버전을 `2.6.1 / 261`로 올렸습니다.
- 제품/보안/아키텍처 문서와 사용자 문구를 모델 다운로드 기준으로 정리했습니다.

검증:
- 로컬: `compileDebugKotlin`, `testDebugUnitTest`, `lintDebug`, `git diff --check` 성공.
- 로컬: `bundleRelease`는 10분 제한 초과로 완료 확인하지 못했습니다.
- CI: 커밋/푸시 후 확인 예정.

## 2026-05-26 (v2.6.0 Release - Local VLM Advanced Analysis)

작업 내용:
- 앱 버전을 `2.5.0 / 250`에서 `2.6.0 / 260`으로 올렸습니다.
- 로컬 VLM 고급 분석을 새 릴리즈 기준 기능으로 정리했습니다.
- README, GitHub Pages, Play Store 문구, CHANGELOG를 v2.6.0 기준으로 갱신했습니다.

검증:
- 로컬: `assembleDebug`, `testDebugUnitTest`, `lintDebug`, `assembleRelease`, `git diff --check` 성공.
- CI: 버전 커밋과 태그 푸시 후 Android CI / Release APK 확인 예정.

## 2026-05-26 (Public Surface - GitHub Pages / README / Store Assets)

작업 내용:
- MarkScene 공개 표면을 v2.5.0 기준으로 정리했습니다.
- GitHub Pages 랜딩을 실제 앱 소개용 제품 페이지로 재구성하고, 기존 store asset 이미지를 `docs/assets/`에 배치했습니다.
- Pages 공개 개인정보 처리방침 경로(`/privacy/`)를 추가했습니다.
- README를 공개 링크, 핵심 기능, 개인정보 원칙, 스토어 준비 자료 중심으로 재정리했습니다.
- Play Store 등록 문구를 v2.5.0 기능과 공개 개인정보 URL 기준으로 업데이트했습니다.

검증:
- 로컬: `git diff --check`, HTML 로컬 참조 검사, 이미지 크기 확인, Pages 로컬 HTTP 브라우저 미리보기 완료.
- CI: 커밋/푸시 후 GitHub Actions Pages/Android CI 확인 예정.

## 2026-05-20 (Iteration 6.1 - v2.3.0: Trust/Security 3종)

작업 내용:
- 우선순위 3개를 추가 구현하고 v2.3.0으로 릴리즈했습니다.
  1. **Today 화면 로컬 처리 배지**: TopAppBar 액션에 방패 아이콘 + "로컬 처리" 라벨을 추가. 사용자가 모든 태깅/OCR이 기기 내부에서만 처리됨을 매 진입마다 확인.
  2. **API Key 입력 보안 강화**: 비밀번호 마스킹, 자동완성/자동수정/추천 비활성화, 표시/숨김 토글. 어깨너머 노출 및 입력기 스누핑 위험 완화.
  3. **카메라 권한 사전 안내 자동화**: 캡처 모드 진입 시 권한이 없으면 시스템 다이얼로그 전에 사전 안내 모달을 자동 노출. 다이얼로그 문구를 strings.xml로 정리해 다국어 대응을 준비.
- 버전 `2.2.0 / 220` → `2.3.0 / 230` 상향, CHANGELOG/HISTORY/Epic 갱신.

변경 파일:
- `app/src/main/java/com/markscene/app/ui/screen/TodayScreen.kt`
- `app/src/main/java/com/markscene/app/ui/screen/SettingsScreen.kt`
- `app/src/main/java/com/markscene/app/ui/screen/CreateRecordScreen.kt`
- `app/src/main/res/values/strings.xml`
- `gradle/libs.versions.toml`
- `CHANGELOG.md`, `HISTORY.md`, `.agent/progress.md`, `.agent/tasks.md`, `.agent/epic4_sec_priv.md`

검증:
- 로컬: `./gradlew :app:compileDebugKotlin` 성공, `./gradlew :app:testDebugUnitTest` 성공.
- CI: 푸시 후 결과 모니터링 예정.

## 2026-05-20 (Iteration 6.0 - v2.2.0: 우선순위 UX/보안 3종 통합)

작업 내용:
- 남아있는 Epic 작업 중 자체 검증 가능한 우선순위 3개를 선정해 구현하고 v2.2.0으로 릴리즈했습니다.
  1. **스와이프 삭제 실 연결**: 기존 `SwipeableGalleryItem`이 이름만 있고 실제 제스처가 없던 상태에서 Material3 `SwipeToDismissBox`로 재구현. 좌/우 스와이프 시 햅틱 + 삭제 확인 다이얼로그.
  2. **개인정보 대시보드 통합**: `PrivacyDashboardScreen`은 이미 구현돼 있었으나 네비게이션에 연결되지 않은 상태였음. Settings 보안 섹션에 진입 항목 추가, `PRIVACY_DASHBOARD_ROUTE` 라우트 등록.
  3. **앱 전체 스크린샷 차단 토글**: `UserPreferences`에 저장 API 추가, Settings 보안 섹션에 Switch 추가, 앱 루트(`MarkSceneApp`)에서 사용자 선택에 따라 `SecureScreenEffect(enabled=...)` 적용.
- 버전 `2.1.0 / 210` → `2.2.0 / 220` 상향, CHANGELOG/HISTORY 정리.

변경 파일:
- `app/src/main/java/com/markscene/app/ui/screen/RecordListScreen.kt`
- `app/src/main/java/com/markscene/app/ui/screen/SettingsScreen.kt`
- `app/src/main/java/com/markscene/app/ui/MarkSceneApp.kt`
- `app/src/main/java/com/markscene/app/data/settings/UserPreferences.kt`
- `app/src/main/res/values/strings.xml`
- `gradle/libs.versions.toml`
- `CHANGELOG.md`, `HISTORY.md`, `.agent/progress.md`, `.agent/tasks.md`

검증:
- 로컬: `./gradlew :app:compileDebugKotlin` 성공, `./gradlew :app:testDebugUnitTest` 성공.
- CI: 푸시 후 `Android CI` / `Release APK` 결과 모니터링 예정.

후속 작업:
- `v2.2.0` 태그 푸시 후 GitHub Actions Release APK 산출물 점검.
- 남아있는 Epic 작업(앱 시작 속도, Baseline Profile, 클립보드 보호 등) 다음 이터레이션에서 진행.

## 2026-05-13 (Iteration 5.0 - GitHub Issues 기준 문서 동기화)

작업 내용:
- GitHub Issues를 기준으로 모든 마크다운 문서를 정리하고 동기화했습니다.
- 누락된 Epic(#19 Core AI, #20 Marketing)을 GitHub Issues로 생성했습니다.
- `.agent/epic*.md` 파일에 GitHub Issue 링크를 추가했습니다.
- `.agent/tasks.md`에 Epic Issues 매핑 테이블을 추가했습니다.
- `docs/ROADMAP.md` Phase 5를 Epic Strategy 테이블로 업데이트했습니다.
- `AGENTS.md` 필수 문서 읽기 순서에 epic 파일을 추가했습니다.
- `README.md`에 GitHub Issues 링크와 `.agent/` 디렉토리 epic 파일 구조를 반영했습니다.

변경 파일:
- `.agent/epic1_ui_ux.md`, `.agent/epic2_core_ai.md`, `.agent/epic3_perf_stab.md`, `.agent/epic4_sec_priv.md`, `.agent/epic5_market.md`
- `.agent/tasks.md`
- `.agent/progress.md`
- `docs/ROADMAP.md`
- `AGENTS.md`
- `README.md`
- `HISTORY.md`
- `CHANGELOG.md`

GitHub Issues:
- #19, #20 생성
- 전체 이슈 매핑: #14, #16, #17, #19, #20 (5개 Epic) + #10 (기능)

## 2026-05-06 (Iteration 4.0 - CI 성공 모니터링 및 분석 완료)

작업 내용:
- 최근 GitHub Actions CI 실패 이력을 분석하고 현재 상태가 정상임을 확인했습니다.
- 과거 실패(ID: 25376082806)의 원인이 `connectedDebugAndroidTest` 실행 중 에뮬레이터 호환 장치 연결 실패였음을 파악했습니다.
- 최신 커밋에 대해 신규 CI 실행(ID: 25419852796)을 트리거하고 모든 단계(Lint, Test, Build, Instrumentation Test)의 성공을 모니터링했습니다.
- `HISTORY.md`에 CI 상태 점검 결과를 기록하여 프로젝트 이력을 관리했습니다.

검증:
- CI: `Android CI` 워크플로우 전체 단계 성공 완료 (결론: `success`)

## 2026-05-05 (Iteration 3.0 - v2.0.5 버전 상향 및 CI 확인)

작업 내용:
- 중앙 버전 관리 파일 `gradle/libs.versions.toml`의 앱 버전을 `2.0.5` / `205`로 상향했습니다.
- `CHANGELOG.md`, `docs/RELEASE_CHECKLIST.md`, `HISTORY.md`에 최신 버전 기준과 검증 계획을 반영했습니다.

검증:
- 로컬: `./gradlew test`, `./gradlew lint`, `./gradlew assembleDebug` 성공
- CI: 커밋/푸시 후 `Android CI` 성공 여부 확인 예정

## 2026-04-30 (Iteration 2.9 - Pages 루트/릴리즈 APK 자동화)

작업 내용:
- Pages 루트 URL에서 바로 열리는 브랜딩 진입 페이지(`docs/index.html`)를 추가했습니다.
- Pages 배포 아티팩트 경로를 `docs/branding`에서 `docs` 전체로 확장했습니다.
- 태그 푸시(`v*.*.*`) 시 `assembleRelease` 후 APK를 GitHub Release에 업로드하는 워크플로(`release-apk.yml`)를 추가했습니다.
- 앱 버전을 `versionName 0.4.0`, `versionCode 2`로 올렸습니다.

검증:
- 로컬: Android SDK 경로 미설정으로 빌드 검증 제한
- CI: 커밋 푸시 후 `Android CI` 및 `Deploy GitHub Pages` 확인 예정
- 릴리즈: `v0.4.0` 태그 푸시 후 `Release APK` 확인 예정

추가 수정:
- CameraX AAR metadata 요구사항(AGP 8.6+)에 맞춰 Android Gradle Plugin 버전을 `8.6.1`로 상향했습니다.
- `v0.4.0` 릴리즈에서 APK 첨부 누락(파일명 불일치)을 확인해 릴리즈 업로드 경로를 `*.apk`로 수정했습니다.
- 버전을 `0.4.1`로 상향하고 재릴리즈 태그를 준비했습니다.

## 2026-04-30 (Iteration 2.8 - CameraX 캡처 플로우 전환)

작업 내용:
- CameraX 의존성(`camera-core`, `camera-camera2`, `camera-lifecycle`, `camera-view`)을 추가했습니다.
- `CreateRecordScreen`의 캡처 경로를 `TakePicture` 계약 기반에서 CameraX 프리뷰/촬영 기반으로 전환했습니다.
- 캡처 소스 선택 시:
  - 카메라 권한 확인/요청
  - 실시간 프리뷰 표시
  - 촬영 버튼으로 이미지 저장 후 기존 태그 생성/저장 플로우에 연결
- `.agent/tasks.md`에서 CameraX 작업(#1)을 완료 처리했습니다.

검증:
- 로컬: `./gradlew :app:compileDebugKotlin` 실행 시도
- 로컬 결과: 실패
- 사유: Android SDK 경로 미설정 (`ANDROID_HOME` 또는 `local.properties`의 `sdk.dir` 필요)
- CI: 커밋/푸시 후 GitHub Actions로 검증 예정

추가 수정:
- CI 실패 로그에서 `CameraX 1.6.0`이 현재 AGP(`8.5.2`) / compileSdk(`35`)와 비호환임을 확인했습니다.
- `cameraX` 버전을 `1.5.3`으로 조정해 빌드 호환성을 복구했습니다.

## 2026-04-30 (Iteration 1 - Compose Shell Bootstrap)

작업 내용:
- Android 앱 프로젝트 골격을 추가했습니다 (`applicationId`/`namespace`: `com.markscene.app`).
- Jetpack Compose + Material 3 기본 구성을 추가했습니다.
- Home 화면에 핵심 액션 버튼을 추가했습니다.
  - `Capture Photo`
  - `Import Photo`
  - `Settings`
- 플레이스홀더 내비게이션 목적지를 추가했습니다.
  - `Create Record`
  - `Record List / Search`
  - `Settings`
- 금지 권한(`MANAGE_EXTERNAL_STORAGE`, 광범위 미디어 권한) 및 실제 외부 AI 호출은 추가하지 않았습니다.

검증:
- `./gradlew :app:assembleDebug` 실행 시도
- 결과: 실패
- 사유: `gradle/wrapper/gradle-wrapper.jar`가 없어 Gradle Wrapper를 실행할 수 없음

다음 작업:
- Gradle Wrapper JAR을 추가해 로컬 빌드 가능 상태를 먼저 복구
- Home 액션을 실제 Capture/Import 플로우와 연결

## 2026-04-30 (Iteration 1.1 - 빌드 체인 복구 및 다음 페이즈)

작업 내용:
- `gradle/wrapper/gradle-wrapper.jar`를 추가해 Gradle Wrapper 실행 문제를 복구했습니다.
- `Create Record` 화면을 분리하고 `Capture Photo`/`Import Photo` 진입 소스를 라우트 인자로 구분했습니다.
- GitHub Actions 기본 Android CI 워크플로(`.github/workflows/android-ci.yml`)를 추가했습니다.
- `.agent/tasks.md`에서 완료된 부트스트랩 항목과 CI 항목을 체크했습니다.

검증:
- `./gradlew :app:assembleDebug` 실행
- 결과: 실패
- 사유: 로컬 Android SDK 경로 미설정 (`ANDROID_HOME` 또는 `local.properties`의 `sdk.dir` 필요)

다음 작업:
- 로컬 SDK 경로를 설정한 뒤 `test`, `lint`, `assembleDebug`를 순차 검증
- `Create Record` 화면에 Photo Picker/CameraX 실제 연결

## 2026-04-30 (Iteration 1.2 - MVP 세로 슬라이스 확장)

작업 내용:
- `PhotoRecord`, `PhotoTag`, `TagSource`, `AnalysisStatus` 모델을 추가했습니다.
- `LocalImageTagger` 인터페이스와 `MockLocalImageTagger`를 추가했습니다.
- 인메모리 저장소(`InMemoryRecordRepository`)를 추가했습니다.
- `Create Record` 화면에 Photo Picker import, mock local tag 생성, 태그 삭제/추가, 메모 입력, 저장 기능을 반영했습니다.
- `Record List / Search` 화면을 추가하고 태그/제목/메모 검색을 연결했습니다.
- 저장 완료 시 목록/검색 화면으로 이동하도록 내비게이션을 연결했습니다.

검증:
- `./gradlew :app:assembleDebug` 재실행 시도 예정 (로컬 SDK 경로 미설정 상태 동일)

다음 작업:
- CameraX 기반 실제 캡처 연결
- Room 영속 저장소로 교체

## 2026-04-30 (Iteration 1.3 - CI 실패 대응)

작업 내용:
- GitHub Actions 실패 로그(`Permission denied: ./gradlew`)를 확인했습니다.
- `.github/workflows/android-ci.yml`에 `chmod +x gradlew` 단계를 추가했습니다.

검증:
- CI 재실행은 새 푸시로 트리거하여 확인 예정

## 2026-04-30 (Iteration 1.4 - CI 리소스 링크 실패 대응)

작업 내용:
- CI 실패 원인을 `Theme.Material3.DayNight.NoActionBar` 리소스 누락으로 확인했습니다.
- `com.google.android.material:material` 의존성을 추가해 XML 테마 링크 오류를 수정했습니다.

## 2026-04-30 (Iteration 1.5 - CI 매니페스트 아이콘 오류 대응)

작업 내용:
- CI 실패 로그에서 `sym_def_app_icon_round` 리소스 미존재 오류를 확인했습니다.
- `AndroidManifest.xml`에서 `roundIcon` 참조를 제거해 리소스 링크 오류를 수정했습니다.

## 2026-04-30 (Iteration 2.0 - Room/Settings 기반 확장)

작업 내용:
- `Room` 엔터티/DAO/DB를 추가하고 기록 저장소를 `InMemory`에서 `RoomRecordRepository`로 전환했습니다.
- `Record List / Search`에서 Room 데이터를 조회하고 레코드 삭제를 추가했습니다.
- 설정 화면을 추가하고 BYOK 상태 UI를 연결했습니다.
  - API Key 저장/삭제
  - 암호화 저장소(`EncryptedSharedPreferences`) 적용
  - 외부 호출 없는 Mock 연결 테스트
  - 외부 분석 전송 안내 문구 표시

검증:
- CI(`Android CI`)에서 `assembleDebug` 실행 확인 예정

다음 작업:
- CameraX 캡처 및 이미지 미리보기 실제 구현
- Room 기반 Record Detail/삭제 연계 정리

## 2026-04-30 (Iteration 2.1 - 캡처/상세 화면 확장)

작업 내용:
- `CAMERA` 권한과 `FileProvider`를 추가해 캡처 파일 URI 저장 경로를 구성했습니다.
- `Create Record`에 실제 촬영(`TakePicture`) 플로우를 추가했습니다.
- 선택/촬영 이미지를 화면에서 즉시 미리보기하도록 변경했습니다.
- `Record Detail` 화면을 추가하고 목록에서 상세 진입을 연결했습니다.

검증:
- 로컬 SDK 경로 미설정으로 로컬 빌드는 제한
- CI에서 `assembleDebug` 확인 예정

## 2026-04-30 (Iteration 2.2 - 개인정보 고지/Mock 고급분석)

작업 내용:
- 설정 화면에서 인앱 개인정보 고지 화면으로 이동하도록 연결했습니다.
- 상세 화면에 `Run Advanced Analysis (Mock)`를 추가했습니다.
- 분석 실행 전 외부 전송 고지 다이얼로그를 추가하고, 동의 시 mock 분석 결과(요약/추천 태그/경고)를 표시하도록 구현했습니다.

검증:
- CI에서 `assembleDebug` 확인 예정

## 2026-04-30 (Iteration 2.3 - ML Kit 로컬 태깅 전환)

작업 내용:
- `MlKitLocalImageTagger`를 추가해 온디바이스 이미지 라벨링을 적용했습니다.
- 라벨 confidence 임계값(0.6)과 기본 정규화 딕셔너리를 반영했습니다.
- 태깅 실패/결과 없음 시 기존 `MockLocalImageTagger`로 폴백하도록 안전장치를 넣었습니다.
- 앱 기본 태거를 mock에서 ML Kit 구현으로 전환했습니다.

검증:
- CI에서 `assembleDebug` 확인 예정

## 2026-04-30 (Iteration 2.4 - 고급분석 결과 저장 연동)

작업 내용:
- `advanced_analysis` 테이블과 DAO를 추가하고 DB 버전을 2로 올렸습니다.
- `1 -> 2` 마이그레이션을 추가해 기존 사용자 데이터 손실 없이 테이블을 확장했습니다.
- 상세 화면에서 mock 고급분석 결과를 적용하면:
  - 요약을 `advanced_analysis`에 저장
  - 추천 태그를 `AdvancedAi` 소스로 기록에 병합 저장
  - 레코드 `analysisStatus`를 `AdvancedComplete`로 갱신
- 저장된 최신 분석 요약을 상세 화면에 다시 표시하도록 연결했습니다.

검증:
- CI에서 `assembleDebug` 확인 예정

## 2026-04-30 (Iteration 2.5 - Gemini BYOK 실제 호출 경로)

작업 내용:
- `GeminiAdvancedVisionProvider`를 추가해 BYOK 기반 실제 이미지 분석 요청 경로를 구현했습니다.
- `INTERNET` 권한을 추가했습니다.
- 상세 화면 고급분석 실행 시:
  - API Key가 있으면 Gemini 호출 시도
  - 실패 또는 키 없음이면 mock 결과로 안전 폴백
- 분석 적용 시 provider 정보를 `gemini/mock`로 구분 저장하도록 반영했습니다.

검증:
- CI에서 `assembleDebug` 확인 예정

## 2026-04-30 (Iteration 2.6 - CI 품질 게이트 강화)

작업 내용:
- GitHub Actions 워크플로를 `lint + unit test + assembleDebug` 순으로 확장했습니다.
- `FORCE_JAVASCRIPT_ACTIONS_TO_NODE24=true`를 설정해 Node 20 경고 완화를 적용했습니다.

검증:
- 변경된 워크플로로 CI 재검증 예정

추가 수정:
- CI lint 오류(`PermissionImpliesUnsupportedChromeOsHardware`) 대응을 위해
  `uses-feature android.hardware.camera required=false`를 매니페스트에 추가했습니다.

## 2026-04-30 (Iteration 2.7 - 브랜딩/이슈 동기화)

작업 내용:
- 기본 앱 아이콘을 MarkScene 정체성에 맞는 adaptive icon으로 교체했습니다.
- GitHub Pages 브랜딩 페이지(`docs/branding/index.html`)와 배포 워크플로(`pages.yml`)를 추가했습니다.
- 남은 작업을 GitHub Issues로 등록했습니다: #1 ~ #6
- `.agent/tasks.md`를 이슈 번호와 동기화하고 완료/미완료 상태를 갱신했습니다.

검증:
- CI(`Android CI`) 및 Pages 배포 워크플로 실행 확인 예정

## 2026-04-30

Initial planning documents created.

Current status:

- Product direction defined.
- Privacy-first rules defined.
- BYOK strategy defined.
- Local tagging strategy defined.
- MVP roadmap defined.

Next recommended step:

- Bootstrap Android project and implement the first Compose navigation shell.
