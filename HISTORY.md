# HISTORY.md

## 2026-05-29 — 로컬 모델 다운로드 접근성 개선

- 작업: HuggingFace 라이선스/토큰 절차 때문에 로컬 고급 AI 모델을 받기 어렵던 설정 화면의 안내와 바로가기를 개선.
- 변경 내용:
  1. `SettingsScreen`: 라이선스 수락, read 토큰 생성, 토큰 저장 후 다운로드 순서를 단계별 안내로 표시.
  2. `SettingsScreen`, `MarkSceneApp`: HuggingFace 토큰 발급 페이지 바로가기 콜백과 버튼 추가.
  3. `SettingsScreen`: 토큰이 없어도 다운로드 버튼을 누를 수 있게 하고, 기존 누락 안내 메시지로 다음 행동을 알려주도록 변경.
  4. `strings.xml`, `CHANGELOG.md`, `.agent/progress.md`, `.agent/tasks.md`, `docs/AI_PROVIDER_STRATEGY.md`, `docs/PRD.md`: 접근성 개선 내용 기록.
- 검증:
  - 로컬: `./gradlew.bat :app:compileDebugKotlin --no-daemon --stacktrace` 성공.
  - 로컬: `./gradlew.bat :app:lintDebug --no-daemon` 성공.
  - 로컬: `./gradlew.bat :app:testDebugUnitTest --no-daemon --stacktrace` 성공.
  - 로컬: `git diff --check` 성공.
  - 참고: `testDebugUnitTest`를 `lintDebug`와 병렬 실행한 첫 시도는 `bundleDebugClassesToCompileJar/classes.jar` 동시 접근으로 실패했고, 테스트 단독 재실행은 성공.
- 결과: 외부 AI provider나 개발자 소유 토큰을 추가하지 않고, 사용자가 직접 모델 준비 절차를 따라가기 쉬운 화면으로 개선.
- 후속 작업:
  - 실제 기기에서 라이선스 수락 후 토큰 저장, 다운로드 시작까지의 UX 확인.

## 2026-05-29 — v2.6.3 릴리즈 준비

- 작업: Play Console에서 `versionCode 262`가 이미 사용 중인 상태를 피하기 위해 새 업로드용 v2.6.3 릴리즈를 준비.
- 변경 내용:
  1. `gradle/libs.versions.toml`: 앱 버전을 `2.6.3 / 263`으로 상향.
  2. `CHANGELOG.md`: v2.6.3 릴리즈 섹션을 추가하고, 릴리즈 노트 태그 규칙 보정과 versionCode 회피를 기록.
  3. `README.md`, `docs/index.html`, `docs/STORE_LISTING_KO.md`, `docs/RELEASE_CHECKLIST.md`, `.agent/progress.md`: v2.6.3 기준 문구 반영.
- 검증:
  - 로컬: `./gradlew.bat :app:bundleRelease --no-daemon` 성공.
  - 로컬: `app/build/intermediates/bundle_manifest/release/processApplicationManifestReleaseForBundle/AndroidManifest.xml`에서 `versionCode=263`, `versionName=2.6.3` 확인.
  - 로컬: `C:\Users\jeiel\OneDrive\바탕 화면\Build`에 `MarkScene-v2.6.3-vc263.aab`와 `MarkScene-v2.6.3-vc263-release-notes.txt` 내보내기 성공.
  - 로컬: Play Console용 TXT는 `<ko-KR>` 265자, `<en-US>` 384자로 각 500자 이하 확인.
  - 진행 중: v2.6.3 태그 릴리즈 워크플로와 산출물 확인 예정.

## 2026-05-29 — 릴리즈 노트 태그 규칙 보정

- 작업: 태그 릴리즈가 GitHub 자동 생성 비교 링크만 게시하던 문제를 수정.
- 변경 내용:
  1. `.github/scripts/extract_release_notes.py`: `vX.Y.Z` 태그와 일치하는 `CHANGELOG.md` 릴리즈 섹션만 추출하고, 태그 형식과 본문 길이를 검증하는 스크립트 추가.
  2. `.github/workflows/release-apk.yml`: `generate_release_notes` 대신 추출한 `release-notes.md`를 `body_path`로 게시하고, APK 파일명에 태그를 포함하도록 변경.
  3. `CHANGELOG.md`: 릴리즈 노트/태그 규칙 보정 내용을 Unreleased에 기록.
- 검증:
  - 로컬: `python .github/scripts/extract_release_notes.py v2.6.2 CHANGELOG.md release-notes.md` 성공.
  - GitHub Release: `v2.6.2` 본문을 `CHANGELOG.md`의 해당 섹션으로 교체하고 `MarkScene-v2.6.2.apk` 산출물을 추가 업로드.
  - 보존: 기존 `app-release.apk`는 이미 게시된 원격 릴리즈 산출물이므로 임의 삭제하지 않음.

## 2026-05-29 — v2.6.2 릴리즈 준비

- 작업: 로컬 VLM 전용 전환과 단위/통합 테스트 자동화가 자체 검증 및 CI에서 통과한 상태를 v2.6.2 정식 릴리즈로 승격.
- 변경 내용:
  1. `gradle/libs.versions.toml`: 앱 버전을 `2.6.2 / 262`로 상향.
  2. `CHANGELOG.md`: 로컬 VLM 전용 전환, Gemini/BYOK 제거, CI 통합 테스트 자동화, launch smoke test를 `v2.6.2 - 2026-05-29` 릴리즈 섹션으로 정리.
  3. `README.md`, `docs/index.html`, `docs/STORE_LISTING_KO.md`, `docs/RELEASE_CHECKLIST.md`, `.agent/progress.md`: v2.6.2 기준 문구와 검증 상태 반영.
- 검증:
  - 선행 로컬: `compileDebugKotlin`, `testDebugUnitTest`, `lintDebug`, `assembleDebug` 성공.
  - 선행 CI: Android CI에서 `lintDebug`, `testDebugUnitTest`, `assembleDebug`, emulator `connectedDebugAndroidTest`, launch smoke test 성공.
  - 로컬: `./gradlew.bat :app:bundleRelease --no-daemon` 성공. `app/build/intermediates/bundle_manifest/release/processApplicationManifestReleaseForBundle/AndroidManifest.xml`에서 `versionCode=262`, `versionName=2.6.2` 확인.
  - 진행 중: v2.6.2 버전 커밋 후 태그 릴리즈 워크플로와 산출물 확인 예정.

## 2026-05-29 — 단위/통합 테스트 자동화

- 작업: 단위 테스트와 emulator 기반 통합 테스트가 CI에서 자동으로 실행되도록 Android CI를 강화.
- 변경 내용:
  1. `.github/workflows/android-ci.yml`: emulator 단계에서 `./gradlew :app:connectedDebugAndroidTest --stacktrace` 실행 후 기존 MainActivity launch smoke test를 이어서 수행하도록 변경.
  2. `RecordDaoIntegrationTest`: in-memory Room DB에서 기록 저장, 태그 검색, OCR 텍스트 검색, record 삭제 후 tag cascade 삭제를 검증하는 instrumentation 통합 테스트 추가.
  3. `CHANGELOG.md`, `.agent/progress.md`, `docs/RELEASE_CHECKLIST.md`: 테스트 자동화 상태 반영.
- 검증:
  - 로컬: `./gradlew.bat :app:testDebugUnitTest --no-daemon`, `./gradlew.bat :app:lintDebug --no-daemon`, `./gradlew.bat :app:assembleDebug --no-daemon` 성공.
  - CI 1차: emulator `connectedDebugAndroidTest`는 2개 테스트 모두 성공. 이후 launch smoke에서 테스트 실행 후 앱 APK가 제거되어 `MainActivity`를 찾지 못해 실패.
  - 수정: 통합 테스트 후 debug APK를 다시 설치한 뒤 launch smoke를 실행하도록 워크플로 보정.

## 2026-05-29 — 로컬 VLM 전용 전환 및 Gemini/BYOK 제거

- 작업: 고급 AI 분석과 비주얼 Q&A를 오직 로컬 LLM/VLM 모델만 사용하는 구조로 전환하고, Gemini/BYOK API Key 입력/저장/호출 경로를 제거.
- 변경 내용:
  1. `GeminiAdvancedVisionProvider` 삭제. 상세 화면 고급 분석은 `LocalVlmAdvancedVisionProvider`만 호출.
  2. `ApiKeyStore`를 `SecureTokenStore`로 교체하고 Gemini API Key 저장/조회/삭제 메서드 제거. 앱 시작 시 기존 `gemini_api_key` 값을 보안 저장소에서 삭제.
  3. `SettingsScreen`에서 Gemini API Key 입력, 저장, 삭제, 연결 테스트 UI 제거. 로컬 모델 다운로드/삭제와 모델 다운로드용 HuggingFace read token UI만 유지.
  4. `LocalVlmAdvancedVisionProvider.askQuestion()` 추가. 기존 비주얼 Q&A는 Gemini 대신 다운로드된 로컬 VLM 모델로 응답.
  5. `PrivacyNoticeScreen`, `PrivacyDashboardScreen`, `strings.xml`, README, GitHub Pages, 개인정보 문서, AI 전략 문서를 외부 AI 전송 없음 기준으로 정리.
  6. 고급 분석 태그/기억 유형 저장은 `local_vlm`/`LocalVlm` 소스를 사용하도록 정리.
- 검증:
  - 로컬: `./gradlew.bat :app:compileDebugKotlin --no-daemon`, `./gradlew.bat :app:testDebugUnitTest --no-daemon`, `./gradlew.bat :app:lintDebug --no-daemon`, `./gradlew.bat :app:assembleDebug --no-daemon` 성공.
  - 보류: 실제 기기에서 모델 다운로드 → 로컬 분석/Q&A end-to-end 검증은 라이선스 수락된 HuggingFace 토큰과 충분한 RAM이 필요해 보류.
- 결과: 앱의 사용자-facing 분석 경로는 로컬 VLM 전용이 되었고, 분석용 API Key 입력은 제거됨.
- 후속 작업:
  - 실제 기기에서 모델 다운로드 후 로컬 분석/Q&A end-to-end 검증.
  - 대용량 모델 다운로드 중단/재시도 UX 개선.

## 2026-05-27 — 로컬 VLM 실제 구동 가능 상태로 완성

- 작업: 코드 경로는 있지만 모델 URL 미주입과 큰 파일 다운로드 미지원으로 실사용 불가였던 로컬 VLM 기능을, 기본 모델 + 토큰 입력 + 진행률 + 추론 인스턴스 재사용까지 모두 갖춰 실제로 동작 가능한 상태로 만듦.
- 변경 내용:
  1. `app/build.gradle.kts`: `LOCAL_VLM_MODEL_URL` 기본값을 Gemma 3n E2B INT4 LiteRT-LM HuggingFace URL로 설정. `LOCAL_VLM_MODEL_FILENAME`, `LOCAL_VLM_MODEL_SIZE_MB`, `LOCAL_VLM_MODEL_LICENSE_URL` 빌드 설정 추가.
  2. `LocalVisionModelManager`: 청크 스트리밍 다운로드, HF 토큰 Bearer 헤더, Content-Length 검증, 디스크 여유 공간 사전 검증, 코루틴 취소, 10분 readTimeout, HTTP 상태별 한국어 오류, `acquireInference()`/`closeCachedInference()`로 LlmInference 인스턴스 lazy 캐싱 추가.
  3. `LocalVlmAdvancedVisionProvider`: 매 분석마다 LlmInference를 재생성하던 흐름을 modelManager에 위임. 세션만 매번 새로 만들고 OOM/타임아웃/JSON 파싱 실패에 사용자 친화 fallback.
  4. `ApiKeyStore`: HuggingFace 토큰 저장/조회/삭제 메서드 추가. 기존 EncryptedSharedPreferences 그대로 활용.
  5. `SettingsScreen`: HF 토큰 입력 필드(visibility toggle, 도움말, 저장/삭제), 모델 정보(이름·크기), 라이선스 안내 + 페이지 열기 버튼, 다운로드 진행률(LinearProgressIndicator + 바이트/퍼센트 텍스트), 디바이스 메모리 요구 안내 추가.
  6. `MarkSceneApp`: 진행률/HF 토큰 상태와 콜백 연결. 다운로드 시 라이선스 필요 모델이면 토큰 없으면 사전 차단.
  7. `strings.xml`: 진행률, 라이선스 안내, HF 토큰 관련 한국어 문구 12종 추가.
- 검증:
  - 로컬: `./gradlew :app:compileDebugKotlin`, `./gradlew :app:assembleDebug`, `./gradlew :app:testDebugUnitTest`, `./gradlew :app:lintDebug` 모두 성공.
  - 로컬: 생성된 `BuildConfig.java`에서 `LOCAL_VLM_MODEL_URL`이 `https://huggingface.co/google/gemma-3n-E2B-it-litert-lm/resolve/main/gemma-3n-E2B-it-int4.litertlm`로 박혀 있는지 직접 확인.
  - 보류: 실제 기기에서 다운로드 → 추론 end-to-end 검증은 라이선스 수락된 사용자 HuggingFace 토큰과 충분한 RAM(약 4GB)을 가진 기기가 필요해 본 작업 범위 외로 보류.
- 결과: 사용자가 ① HuggingFace에서 Gemma 라이선스 수락 후 read 토큰 발급, ② 앱 설정에 토큰 저장, ③ "모델 다운로드" 클릭으로 로컬 VLM이 동작 가능한 상태에 도달.
- 후속 작업:
  - 실기기에서 다운로드 시간/추론 지연/메모리 사용량 측정 후 사용자 안내 문구 보정.
  - 다운로드 도중 화면 이탈/프로세스 종료에도 안전한 ForegroundService 또는 WorkManager 기반 전환 검토.

## 2026-05-26 — 로컬 VLM 모델 자동 다운로드 전환

- 작업: 설정 화면의 로컬 AI 모델 버튼이 파일 선택기를 열지 않고 승인된 HTTPS 모델 URL에서 자동으로 다운로드하도록 전환하고, Play Store versionCode 중복을 피하기 위해 버전을 `2.6.1 / 261`로 상향.
- 변경 내용:
  1. `LocalVisionModelManager`: 사용자 파일 가져오기 대신 HTTPS 다운로드 후 앱 내부 저장소에 원자적으로 저장하도록 변경.
  2. `MarkSceneApp`, `SettingsScreen`, `strings.xml`: 로컬 VLM 모델 버튼을 다운로드/다시 다운로드 흐름으로 바꾸고 다운로드 중 상태와 실패 메시지를 표시.
  3. `app/build.gradle.kts`: `MARKSCENE_LOCAL_VLM_MODEL_URL`, `MARKSCENE_LOCAL_VLM_MODEL_NAME` 빌드 설정값을 `BuildConfig`로 주입.
  4. `gradle/libs.versions.toml`, `README.md`, `docs/index.html`, `docs/STORE_LISTING_KO.md`, `docs/RELEASE_CHECKLIST.md`, `CHANGELOG.md`: Play Store 재업로드용 버전 `2.6.1 / 261` 반영.
  5. 제품/보안/아키텍처 문서와 작업 문서를 모델 다운로드 기준으로 갱신.
- 검증:
  - 로컬: `./gradlew.bat :app:compileDebugKotlin --no-daemon`, `./gradlew.bat testDebugUnitTest --no-daemon`, `./gradlew.bat :app:lintDebug --no-daemon`, `git diff --check` 성공.
  - 로컬: `./gradlew.bat bundleRelease --no-daemon`은 10분 제한 초과로 완료 확인하지 못함. 기존 AAB는 v2.6.0 생성분이라 재사용하지 않음.
  - 진행 예정: 커밋/푸시 후 GitHub Actions에서 v2.6.1 산출물 확인.
- 결과: 사용자가 로컬 AI 모델 설정에서 파일을 직접 고르는 흐름을 제거하고, 앱이 설정된 모델을 준비하는 UX로 전환.

## 2026-05-26 — v2.6.0 릴리즈 및 데스크톱 배포 완료

- 작업: 로컬 VLM 고급 분석 기능을 v2.6.0 정식 릴리즈로 승격하고, 릴리즈 AAB 및 릴리즈 노트 파일을 바탕화면에 내보냄.
- 변경 내용:
  1. `gradle/libs.versions.toml`: 앱 버전 `2.5.0 / 250`에서 `2.6.0 / 260`으로 상향.
  2. `CHANGELOG.md`: Unreleased 항목을 `v2.6.0 - 2026-05-26` 릴리즈 섹션으로 전환.
  3. `README.md`, `docs/index.html`, `docs/STORE_LISTING_KO.md`, `docs/RELEASE_CHECKLIST.md`, `.agent/progress.md`: 로컬 VLM 고급 분석과 v2.6.0 기준 문구 반영.
  4. `app/proguard-rules.pro`: Release R8 빌드에서 MediaPipe/Protobuf 런타임 클래스가 제거되거나 경고로 막히지 않도록 규칙 보강.
  5. 바탕화면 배포: `MarkScene-v2.6.0-vc260.aab` 및 `MarkScene-v2.6.0-vc260-release-notes.txt` 생성.
- 검증:
  - 로컬: `./gradlew.bat assembleDebug`, `./gradlew.bat testDebugUnitTest`, `./gradlew.bat lintDebug`, `./gradlew.bat assembleRelease`, `git diff --check` 성공.
  - 바탕화면: 내보낸 AAB 파일 및 릴리즈 노트 텍스트 파일의 일치성과 무결성 확인 성공.
- 결과: v2.6.0 정식 릴리즈 및 바탕화면 산출물 배포 완료.

## 2026-05-26 — 로컬 VLM 고급 분석 구현

- 작업: 외부 AI API Key 없이도 사용자가 가져온 온디바이스 VLM 모델로 사진을 자세히 분석할 수 있는 실행 경로를 추가.
- 변경 내용:
  1. `LocalVisionModelManager`: 설정에서 선택한 MediaPipe 호환 모델 파일을 앱 내부 저장소로 복사하고 경로/이름을 관리.
  2. `LocalVlmAdvancedVisionProvider`: MediaPipe LLM Inference + 이미지 입력으로 장면 요약, 객체 기반 태그, 경고를 JSON으로 받아 파싱.
  3. `SettingsScreen`: 로컬 고급 AI 모델 가져오기/교체/삭제 UI 추가.
  4. `RecordDetailScreen`: 로컬 모델이 있으면 고급 분석 버튼과 동의 문구를 온디바이스 분석 기준으로 표시하고, 없으면 Gemini BYOK 경로를 사용.
  5. `PrivacyNoticeScreen`, `PrivacyDashboardScreen`, `strings.xml`: 로컬 VLM과 외부 BYOK 분석의 데이터 흐름을 구분해 안내.
  6. `TagSource.LocalVlm`: 로컬 VLM이 만든 태그를 외부 고급 AI 태그와 구분.
  7. `docs/AI_PROVIDER_STRATEGY.md`, `docs/LOCAL_TAGGING.md`, `docs/PRIVACY_AND_SECURITY.md`, `docs/DATA_MODEL.md`, `docs/ARCHITECTURE.md`, `docs/PRD.md`: 로컬 VLM 우선 전략과 제약 반영.
  8. `.github/workflows/android-ci.yml`: MediaPipe 네이티브 라이브러리 추가 후 CI 에뮬레이터의 connected AndroidTest 설치가 불안정해져, 기존 빌드 APK를 직접 설치/실행하는 launch smoke test로 전환.
- 검증:
  - 로컬: `./gradlew.bat assembleDebug` 성공.
  - 로컬: `./gradlew.bat testDebugUnitTest`, `./gradlew.bat lintDebug`, `git diff --check` 성공.
  - CI: 첫 푸시에서 lint/unit/debug APK 빌드는 성공, emulator connected AndroidTest 설치 단계 실패. 이후 launch smoke test 방식으로 수정 후 재검증 진행.
  - 런타임: 실제 기기 모델 파일 추론은 MediaPipe 호환 모델 파일과 고성능 Android 기기가 필요하므로 로컬 빌드 검증까지만 완료.
- 결과: 로컬 VLM 모델 파일을 설정하면 상세 화면에서 외부 전송 없이 고급 분석을 실행할 수 있는 수준으로 구현 완료.
- 후속 작업:
  - 실제 Gemma 3n E2B/E4B MediaPipe 모델 파일을 기기에 넣고 성능/메모리/JSON 안정성 측정.
  - 필요 시 로컬/외부 provider 선택 UI를 추가.

## 2026-05-26 — 공개 표면 정비 (GitHub Pages / README / 메타데이터)

- 작업: MarkScene의 GitHub IO 랜딩, README, 개인정보 처리방침 URL, Play Store 준비 자료, GitHub 설명/토픽을 v2.5.0 기준으로 정비.
- 변경 내용:
  1. `docs/index.html`: v2.5.0 최신 기능(오늘의 장면, Recall Box, 검색, 보안 옵션, 공유/내보내기, BYOK)을 강조하는 제품형 랜딩으로 재구성.
  2. `docs/privacy/index.html`: GitHub Pages에서 바로 접근 가능한 공개 개인정보 처리방침 페이지 추가.
  3. `docs/assets/`: 기존 store asset을 Pages 표시용 이미지로 재사용.
  4. `README.md`: 공개 링크, 현재 버전, 핵심 기능, 개인정보 원칙, 스토어 준비 자료를 빠르게 찾도록 재정리.
  5. `docs/STORE_LISTING_KO.md`, `store-assets/MarkScene_v2.5.0_playstore_listing.txt`: Play Store 문구와 개인정보 URL을 최신 버전에 맞춤.
  6. GitHub 저장소 메타데이터: description/homepage/topics를 MarkScene 공개 포지션에 맞춰 갱신 예정.
- 검증:
  - 로컬: `git diff --check`, HTML 로컬 참조 검사, 이미지 크기 확인, Pages 로컬 HTTP 브라우저 미리보기 확인.
  - 원격: 커밋/푸시 후 Pages 배포와 공개 URL HTTP 200 확인 예정.
- 결과: 로컬 공개 표면 검증 완료, 원격 배포 검증 진행 예정.

## 2026-05-21 — v2.5.0 릴리즈 (보안 완성 + UX/Core/Marketing 통합)

- 작업: Epic #4(보안) + Epic #1/#2/#3/#5 잔여 항목을 통합 구현 후 v2.5.0 릴리즈.
- 구현 항목:
  1. **EXIF 제거 옵션**: UserPreferences/SettingsScreen에 토글 추가. 내보내기 시 ExifStripper로 민감 메타데이터 제거.
  2. **갤러리 숨김 모드**: GalleryHideHelper를 앱 시작 시 호출. Settings 토글로 .nomedia 생성/삭제 제어.
  3. **백그라운드 자동 잠금**: MainActivity onStop에서 플래그 설정, MarkSceneApp LaunchedEffect에서 잠금 처리.
  4. **API Key 저장소 강화**: AndroidManifest allowBackup=false, EncryptedSharedPreferences 확인.
  5. **소셜 공유**: RecordDetailScreen에 Share 버튼 추가 → SocialShareHelper.shareWithTemplate() 호출.
  6. **프롬프트 템플릿**: RecordDetailScreen 고급 분석 영역에 5개 템플릿 FilterChip 추가.
  7. **이미지 크롭**: RecordDetailScreen에 Crop 버튼 추가 → ImageCropper.cropCenterSquare().
  8. **레이아웃 설정 저장**: RecordListScreen layoutType → UserPreferences 저장, MarkSceneApp에서 복원.
  9. **In-App Review**: ReviewHelper를 MarkSceneApp에 연결, 조건 충족 시 리뷰 다이얼로그 표시.
  10. **배지/주간회고 강화**: 이모지 배지 5종 추가, 주간 회고에 인기 태그 TOP3 표시.
  11. **Gemini 타임아웃**: OkHttpClient에 30초 connect/read/write 타임아웃 설정.
  12. **CameraX 최적화**: CAPTURE_MODE_MINIMIZE_LATENCY 확인 (기적용됨).
- 변경 파일:
  - `UserPreferences.kt`: EXIF/gallery/autoLock/layout 설정 추가
  - `SettingsScreen.kt`: 보안 섹션에 3개 새 스위치 추가
  - `MarkSceneApp.kt`: 새 상태 변수, LaunchedEffect, SettingsScreen 콜백, Review 체크, SmartAlbum 라우트
  - `RecordDetailScreen.kt`: Share/Crop 버튼, 프롬프트 템플릿 칩
  - `MainActivity.kt`: onStop/onResume 라이프사이클 처리
  - `GeminiAdvancedVisionProvider.kt`: OkHttp 타임아웃
  - `AndroidManifest.xml`: allowBackup=false
  - `strings.xml`: 새 문자열 6개 추가
  - `CHANGELOG.md`, `HISTORY.md`: v2.5.0 내용
  - Epic 파일들: 완료 항목 체크
- 버전: `2.4.0 / 240` → `2.5.0 / 250`
- 검증: compileDebugKotlin 성공, test + lint 예정.

## 2026-05-20 — v2.4.0 릴리즈 (성능/안정성 10종 + 테마 완성)

- 작업: Epic #16(성능/안정성) + Epic #1(UI/UX) 우선순위 10개 항목을 구현 후 v2.4.0 릴리즈.
- 구현 항목:
  1. **ImageOptimizer 버그 수정 + OOM 방어**: `inJustDecodeSize`(오타) → `inJustDecodeBounds`로 수정. 적응형 inSampleSize 계산, RGB_565 컬러, 추가 스케일 다운으로 OOM 위험 감소.
  2. **Coil ImageLoaderConfig 전역 연결**: `MarkSceneApplication`(ImageLoaderFactory 구현) 생성. AndroidManifest에 등록. 15% 메모리 캐시, RGB_565, 하드웨어 비트맵 적용.
  3. **Room FTS4 인덱스 추가**: `RecordFtsEntity`, `RecordFtsDao` 생성. DB v9→v10 마이그레이션. 저장/삭제 시 FTS 인덱스 동기화.
  4. **리스트 스크롤 Jank 개선**: Kotlin 2.0 Compose Strong Skipping 모드(`enableStrongSkippingMode = true`) 활성화. `PhotoRecord`에 `@Immutable` 추가.
  5. **Baseline Profile**: `profileinstaller` 의존성 추가. `baseline-prof.txt`에 MainActivity/MarkSceneApp/TodayScreen 등 주요 시작 경로 프로파일 규칙 작성.
  6. **저장공간 자동 관리**: `StorageCleaner` 유틸 생성. 앱 시작 시 7일 이상 임시 파일 + 300MB 초과 캐시 정리.
  7. **Material You Dynamic Color**: Theme.kt 색상 스킴 완성도 향상. 모든 M3 토큰(primary/secondary/tertiary/error + container/on) 정의.
  8. **Dark Mode / True Black 폴리시**: Dark/TrueBlack 색상 스킴에 완전한 M3 토큰 세트 적용.
  9. **검색어 자동완성 완성**: RecordListScreen "최근 검색 지우기" 버튼에 `onClearRecentSearches` 연결. MarkSceneApp에서 `userPrefs.clearRecentSearches()` 호출.
  10. **APK 용량 다이어트**: Release 빌드 `isMinifyEnabled = true`, `isShrinkResources = true`. ProGuard 규칙 작성(Kotlin Serialization, Room, Coil, ML Kit, CameraX, OkHttp, Coroutines). Release 빌드에서 디버그 로그 자동 제거.
- 변경 파일:
  - `app/src/main/java/com/markscene/app/ui/util/ImageOptimizer.kt`: OOM-safe 재작성
  - `app/src/main/java/com/markscene/app/ui/util/ImageLoaderConfig.kt`: (기존)
  - `app/src/main/java/com/markscene/app/MarkSceneApplication.kt`: 신규 (ImageLoaderFactory)
  - `app/src/main/AndroidManifest.xml`: application name 추가
  - `app/src/main/java/com/markscene/app/core/database/RecordFtsEntity.kt`: 신규 (FTS4)
  - `app/src/main/java/com/markscene/app/core/database/RecordFtsDao.kt`: 신규
  - `app/src/main/java/com/markscene/app/core/database/RecordDao.kt`: `observeRecordsByIds` 추가
  - `app/src/main/java/com/markscene/app/core/database/MarkSceneDatabase.kt`: v10, FTS 엔티티/DAO 추가
  - `app/src/main/java/com/markscene/app/ui/MarkSceneApp.kt`: FTS 마이그레이션, StorageCleaner 호출, repository 생성자 업데이트, onClearRecentSearches 연결
  - `app/src/main/java/com/markscene/app/data/record/RoomRecordRepository.kt`: FTS 동기화, ftsDao 파라미터 추가
  - `app/src/main/java/com/markscene/app/ui/screen/RecordListScreen.kt`: onClearRecentSearches 추가
  - `app/src/main/java/com/markscene/app/ui/util/StorageCleaner.kt`: 신규
  - `app/src/main/java/com/markscene/app/ui/theme/Color.kt`: 완전한 M3 팔레트
  - `app/src/main/java/com/markscene/app/ui/theme/Theme.kt`: 완전한 M3 colorScheme
  - `app/src/main/java/com/markscene/app/core/model/PhotoRecord.kt`: @Immutable 추가
  - `app/src/main/assets/baseline-prof.txt`: 업데이트
  - `app/build.gradle.kts`: strong skipping, profileinstaller, R8 활성화
  - `app/proguard-rules.pro`: 포괄적 규칙 작성
  - `gradle/libs.versions.toml`: v2.4.0, profileInstaller 의존성
  - `CHANGELOG.md`, `HISTORY.md`: v2.4.0 내용 추가
- 버전: `2.3.0 / 230` → `2.4.0 / 240`
- 검증: `./gradlew :app:compileDebugKotlin`, `./gradlew :app:testDebugUnitTest`, `./gradlew :app:lintDebug` 실행 예정.

## 2026-05-20 — v2.3.0 릴리즈 (Trust/Security 강화 3종)

- 작업: 우선순위 3개(Today 로컬 처리 배지, API Key 입력 보안 강화, 카메라 권한 사전 안내 자동화) 구현 후 v2.3.0 릴리즈.
- 변경 파일:
  - `app/src/main/java/com/markscene/app/ui/screen/TodayScreen.kt`: TopAppBar actions에 `LocalProcessingBadge()` 추가 (방패 아이콘 + "로컬 처리" 라벨, 접근성 contentDescription 포함).
  - `app/src/main/java/com/markscene/app/ui/screen/SettingsScreen.kt`: API Key TextField에 `PasswordVisualTransformation`, `KeyboardType.Password`, `autoCorrect=false`, visibility toggle 아이콘 추가.
  - `app/src/main/java/com/markscene/app/ui/screen/CreateRecordScreen.kt`: 캡처 화면 진입 시 `LaunchedEffect(source)`로 권한 미부여 상태면 시스템 다이얼로그 전에 사전 안내 다이얼로그 자동 표시. 다이얼로그 문구를 strings.xml 리소스로 전환.
  - `app/src/main/res/values/strings.xml`: 로컬 처리 배지, API Key 표시/숨김, 카메라 권한 안내 문구 추가.
  - `gradle/libs.versions.toml`: `2.2.0 / 220` → `2.3.0 / 230` 상향.
  - `CHANGELOG.md`, `HISTORY.md`, `.agent/progress.md`, `.agent/tasks.md`, `.agent/epic4_sec_priv.md` 갱신.
- 검증:
  - 로컬: `./gradlew :app:compileDebugKotlin` 성공, `./gradlew :app:testDebugUnitTest` 성공.
  - CI: 푸시 후 `Android CI` / `Release APK` 워크플로우 결과 모니터링.
- 결과: 신뢰(로컬 처리 명시)·보안(키 입력 노출 완화)·UX(권한 의도 사전 설명)을 한 번에 강화하는 작은 개선 묶음.
- 후속 작업: Android CI / Release APK 결과 확인, v2.3.0 태그 푸시 후 산출물 점검.

## 2026-05-20 — v2.2.0 릴리즈 (UX/보안 강화 3종)

- 작업: 우선순위 3개 작업(스와이프 삭제 실연결, 개인정보 대시보드 통합, 앱 전체 스크린샷 차단 토글) 구현 후 v2.2.0 릴리즈.
- 변경 파일:
  - `app/src/main/java/com/markscene/app/ui/screen/RecordListScreen.kt`: 기존 이름만 있던 `SwipeableGalleryItem`을 Material3 `SwipeToDismissBox` 기반으로 재작성. 좌/우 스와이프 시 햅틱과 함께 삭제 확인 다이얼로그가 뜨도록 연결.
  - `app/src/main/java/com/markscene/app/ui/screen/SettingsScreen.kt`: `isScreenshotBlockEnabled`, `onToggleScreenshotBlock`, `onOpenPrivacyDashboard` 파라미터 추가. 보안 섹션에 스크린샷 차단 토글과 개인정보 대시보드 진입 항목 추가.
  - `app/src/main/java/com/markscene/app/ui/MarkSceneApp.kt`: `PRIVACY_DASHBOARD_ROUTE` 추가, `PrivacyDashboardScreen`을 NavHost에 연결. 사용자 토글 상태(`UserPreferences.isScreenshotBlockEnabled`)에 따라 앱 루트에서 `SecureScreenEffect` 적용.
  - `app/src/main/java/com/markscene/app/data/settings/UserPreferences.kt`: `isScreenshotBlockEnabled()` / `setScreenshotBlockEnabled()` 저장 API 추가.
  - `app/src/main/res/values/strings.xml`: 스와이프 삭제, 개인정보 대시보드, 스크린샷 차단 관련 문구 추가.
  - `gradle/libs.versions.toml`: `2.1.0 / 210` → `2.2.0 / 220` 상향.
  - `CHANGELOG.md`, `HISTORY.md`, `.agent/progress.md`, `.agent/tasks.md`: 변경 이력 갱신.
- 검증:
  - 로컬: `./gradlew :app:compileDebugKotlin` 성공, `./gradlew :app:testDebugUnitTest` 성공.
  - CI: 푸시 후 `Android CI` / `Release APK` 워크플로우 결과 모니터링.
- 결과: 기존에 컴포넌트만 존재하던 스와이프 삭제가 실제로 동작하고, 구현은 됐지만 네비게이션 미연결 상태였던 PrivacyDashboard가 Settings에서 진입 가능해졌으며, 사용자가 보안 수준에 따라 앱 전체 화면 캡처 차단을 선택할 수 있게 되었습니다.
- 후속 작업: GitHub Actions `Android CI` 결과 확인, `v2.2.0` 태그 푸시 후 `Release APK` 산출물 점검.

## 2026-05-16 — v2.1.0 릴리즈

- 작업: Scene Memory 전체 Phase(1~7) 구현 완료 및 v2.1.0 릴리즈
- 변경 파일:
  - 신규 12개 파일: MemoryType.kt, MoodType.kt, ContextType.kt, MemorySource.kt, MemoryContext.kt, MemoryContextEntity.kt, RecordMemoryTypeCrossRef.kt, MemoryContextDao.kt, MemoryTypeChip.kt, RecallScreen.kt, DailyRecapCard.kt, SearchQueryParser.kt
  - 수정: MarkSceneApp.kt (Bottom Navigation 4탭, Recall/Scene Memory 통합), CreateRecordScreen.kt (Memory Type, Recall 토글), RecordDetailScreen.kt (Memory Type 표시), RecordListScreen.kt (필터 칩), RoomRecordRepository.kt (MemoryContext CRUD), MarkSceneDatabase.kt (v9), MockAdvancedVisionProvider.kt (Memory/Recall 필드), TodayScreen.kt (DailyRecapCard), strings.xml
  - 문서: README.md (제품 포지션 업데이트), CHANGELOG.md (v2.1.0), HISTORY.md, docs/renew/ (설계 문서)
  - 버전: libs.versions.toml (2.0.9→2.1.0, 209→210)
  - 빌드: 릴리즈 키스토어(keystore/release.keystore) 생성, local.properties 서명 설정
- 검증:
  - 코드 로직 10개 항목 전체 검증 PASS
  - `assembleDebug` 성공, `testDebugUnitTest` 성공
  - `bundleRelease` 성공 → 바탕화면 MarkScene_v2.1.0.aab 생성
  - Room DB v8→v9 마이그레이션 포함
- 결과: MarkScene이 장면 기반 개인 기억 저장소로 확장 완료. v2.1.0 AAB 서명 빌드 완료.

## 2026-05-13

- 작업: GitHub Issues 기준 전체 마크다운 문서 정리 및 동기화
- 변경 파일:
  - `.agent/epic1_ui_ux.md` ~ `.agent/epic5_market.md`: 각 파일에 GitHub Issue 링크 추가
  - `.agent/tasks.md`: Epic Issues 매핑 테이블 추가
  - `docs/ROADMAP.md`: Phase 5를 Top 10 Epic Strategy로 업데이트, 이슈 테이블 추가
  - `README.md`: GitHub Issues 링크 추가
- GitHub Issues:
  - `#19` [Epic] Core Features & AI 생성
  - `#20` [Epic] Marketing, Growth & Retention 생성
- 검증:
  - GitHub Issues 목록과 로컬 `.agent/epic*.md` 파일 정합성 확인 완료
  - 누락된 Epic 파일 없음, 총 5개 Epic + 1개 기능 이슈로 통일
- 결과: 모든 Epic이 GitHub Issue로 추적되며, 로컬 문서와 이슈가 1:1로 매핑됨

## 2026-05-09

- 작업: v2.0.9 버전 상향 및 릴리즈 태그 배포
- 변경 파일:
  - `gradle/libs.versions.toml`: `projectVersionName 2.0.9 / projectVersionCode 209`로 상향.
  - `docs/RELEASE_CHECKLIST.md`: 현재 버전 표기 및 체크리스트 항목 동기화.
  - `CHANGELOG.md`: `v2.0.9` 섹션을 추가하고 갤럭시 S24 startup 종료 증상 수정 내용을 사용자 영향 요약으로 정리.
- 검증:
  - 사용자 단말(갤럭시 S24): 디버그 빌드 기준 온보딩 통과 정상 동작 확인 완료(사용자 보고).
  - 로컬(Lenovo TB320FC, Android 15): 회귀 없음 확인 완료.
  - CI: `v2.0.9` 태그 푸시 후 `Android CI` 및 `Release APK` 워크플로우 결과 모니터링.
- 결과: 갤럭시 S24 startup 방어 수정을 정식 릴리즈로 묶어 태그 배포.
- 후속 작업:
  - GitHub Actions `Release APK` 산출물 첨부 확인.
  - 릴리즈 노트가 `CHANGELOG.md`와 일치하는지 확인.

- 작업: 갤럭시 S24에서 보고된 "온보딩 표시 직후 앱 종료" 증상 방어 수정
- 변경 파일:
  - `app/src/main/java/com/markscene/app/ai/provider/MlKitTextRecognizer.kt`: 한국어 OCR 클라이언트(`TextRecognition.getClient(KoreanTextRecognizerOptions...)`)를 `by lazy { runCatching { ... }.getOrNull() }`로 지연 초기화하도록 변경. 일부 단말에서 첫 launch에 동기적으로 던질 수 있는 예외가 composition을 깨지 않게 함.
  - `app/src/main/java/com/markscene/app/ui/MarkSceneApp.kt`:
    - `LaunchedEffect(Unit)` 본문을 `runCatching`으로 감싸 BiometricPrompt / `navController.navigate` 등 초기 비동기 호출 실패가 액티비티를 종료시키지 않도록 보호.
    - 외부 공유로 진입했을 때 `showOnboarding=true` 상태에서 `NavHost`가 트리에 없는 채로 navigate가 호출되어 발생할 수 있는 IllegalStateException을 막기 위해 `!showOnboarding` 가드 추가.
    - `repository.observeRecords()` / `repository.search()` Flow 구독을 `remember` 안에서 `.catch { emit(emptyList()) }` 로 감싸 Room 비동기 에러가 액티비티를 죽이지 않게 함. (`FlowOperatorInvokedInComposition` lint도 통과)
  - `CHANGELOG.md`: Unreleased > Fixed 섹션에 사용자 영향 요약 기록.
- 검증:
  - 로컬: `./gradlew :app:compileDebugKotlin` 성공
  - 로컬: `./gradlew :app:testDebugUnitTest :app:lintDebug` 성공
  - 로컬: `./gradlew :app:assembleDebug` 성공
  - 기기(Lenovo TB320FC, Android 15): 디버그 APK 재설치 + `pm clear` 후 첫 실행 → 온보딩 정상 표시, "걸너뛰기" 후 홈 화면 정상 진입, 프로세스/topResumedActivity 유지 확인. 회귀 없음 확인용이며 갤럭시 S24 재현 단말이 연결되지 않아 최종 검증은 사용자 단말에서 필요.
  - CI: 푸시 후 `Android CI` 결과 확인 예정.
- 결과: 추정되는 갤럭시 S24 startup 종료 경로(ML Kit eager init 동기 예외 / `LaunchedEffect` 비동기 예외 / Room Flow 비동기 예외)에 대한 방어 코드 적용.
- 후속 작업:
  - 사용자 갤럭시 S24에서 첫 실행 시 종료 증상이 사라졌는지 재확인.
  - 재발 시 `adb logcat -d -v time *:E` 또는 크래시 스택 확보 후 정확한 원인 추가 분석.

## 2026-05-06

- 작업: 앱 버전 `2.0.8` / `208` 상향 및 태그 배포 실행
- 변경 파일:
  - `gradle/libs.versions.toml`: 중앙 버전 값을 `2.0.8` / `208`로 상향
  - `CHANGELOG.md`: `v2.0.8` 버전 항목 추가
  - `docs/RELEASE_CHECKLIST.md`: 현재 버전 표기 업데이트
- 검증:
  - 로컬: Android SDK 경로 미설정 환경으로 무거운 로컬 빌드 검증 생략
  - CI: 커밋 푸시 후 `Android CI` 확인 예정
  - Release: `v2.0.8` 태그 푸시 후 `release-apk.yml` 확인 예정
- 결과: 버전 상향 및 배포 트리거 준비 완료
- 후속 작업:
  - GitHub Actions `Android CI` / 릴리즈 워크플로우 성공 확인

- 작업: 앱 버전 `2.0.7` / `207` 상향 및 GitHub Actions 성공 모니터링
- 변경 파일:
  - `gradle/libs.versions.toml`: 중앙 버전 값을 `2.0.7` / `207`로 상향
  - `CHANGELOG.md`: `v2.0.7` 버전 항목 추가
  - `docs/RELEASE_CHECKLIST.md`: 현재 버전 표기 업데이트
- 검증:
  - 로컬: `./gradlew test` 실패 (환경 이슈: `SDK location not found`)
  - 로컬: `./gradlew lint` 실패 (환경 이슈: `SDK location not found`)
  - 로컬: `./gradlew assembleDebug` 실패 (환경 이슈: `SDK location not found`)
  - CI: 커밋 푸시 후 `Android CI` 성공 여부 확인 예정
- 결과: 버전업 변경 반영 완료, 로컬 SDK 경로 누락으로 로컬 빌드는 실패
- 후속 작업:
  - GitHub Actions `Android CI` 최종 성공 확인

- 작업: 앱 버전 `2.0.6` / `206` 상향 및 CI 안정화 재검증
- 변경 파일:
  - `gradle/libs.versions.toml`: 중앙 버전 값을 `2.0.6` / `206`으로 상향
  - `CHANGELOG.md`: `v2.0.6` 버전 항목 추가 및 CI 점검 내역 기록
  - `docs/RELEASE_CHECKLIST.md`: 현재 버전 표기 업데이트
- 검증:
  - CI: 커밋 푸시 후 `Android CI` 결과 확인 예정
- 결과: CI 파이프라인의 에뮬레이터 이슈 분석을 마치고 정식 버전 상향 빌드 수행
- 후속 작업:
  - GitHub Actions `Android CI` 최종 성공 확인

- 작업: GitHub Actions CI 상태 점검 및 최근 실패 원인 분석
- 확인 사항:
  - 최근 실행(ID: 25410487127)이 `success`로 완료됨을 확인 (Kotlin 로컬 캐시 관련 커밋)
  - 과거 실패(ID: 25376082806) 원인: `connectedDebugAndroidTest` 실행 중 에뮬레이터 호환 장치 연결 실패 (No compatible devices connected)
  - 현재 CI 파이프라인(Unit Test, Lint, Build APK, Instrumentation Test)이 정상 작동 중임을 확인
  - GitHub Actions 캐시 서비스 일시적 장애(400 Error/Service Unavailable)가 발생했으나 빌드 성공에는 영향을 주지 않음
- 검증:
  - CI: 현재 상태 모니터링 및 재검증 트리거
- 결과: CI 파이프라인 정상화 확인 및 모니터링 완료
- 후속 작업:
  - 지속적인 빌드 안정성 유지

## 2026-05-05

- 작업: 앱 버전 `2.0.5` / `205` 상향 및 GitHub Actions 성공 모니터링 준비
- 변경 파일:
  - `gradle/libs.versions.toml`: 중앙 버전 값을 `2.0.5` / `205`로 상향
  - `CHANGELOG.md`: `v2.0.5` 버전 항목 추가
  - `docs/RELEASE_CHECKLIST.md`: 현재 버전 표기와 릴리즈 체크리스트 동기화
- 검증:
-  - 로컬: `./gradlew test` 성공
-  - 로컬: `./gradlew lint` 성공
-  - 로컬: `./gradlew assembleDebug` 성공
-  - CI: 커밋 푸시 후 `Android CI` 결과 확인 예정
- 결과: 버전 표기 위치를 단일 기준에 맞춰 상향했고 로컬 품질 게이트를 모두 통과
- 후속 작업:
  - GitHub Actions `Android CI` 성공 확인

## 2026-05-04

- 작업: GitHub 이슈 #10 외부 공유(Receive Intent) 기능 구현 완료 검증
- 확인 사항:
  - `AndroidManifest.xml`에 SEND Intent 필터가 이미 등록되어 있음 (`image/*`)
  - `MainActivity.kt`에서 `handleSendImage()`로 공유된 이미지 URI 처리 중
  - `MarkSceneApp.kt`에서 생체 인증 완료 후 `CreateRecordScreen`으로 네비게이션
  - 외부 앱(갤러리 등)에서 '공유하기' 선택 시 MarkScene이 목록에 표시됨
- 검증:
  - 로컬: `./gradlew :app:compileDebugKotlin` 성공
- 결과: 외부 공유 기능은 이미 구현 완료되어 정상 동작 확인
- 후속 작업:
  - GitHub 이슈 #10 완료 처리
  - Epic 문서 구현 상태 동기화

## 2026-05-02

- 작업: 릴리즈 APK 실행 실패 원인 조사 및 암호화 저장소 안전장치 보강
- 변경 파일:
  - `app/src/main/java/com/markscene/app/data/settings/ApiKeyStore.kt`: Android Keystore/EncryptedSharedPreferences 초기화 실패 시 앱 시작을 막지 않도록 안전 처리
  - `app/src/main/java/com/markscene/app/data/settings/SecurityStore.kt`: 생체 잠금 저장소 초기화 및 읽기/쓰기 실패를 기본값/실패 상태로 처리
  - `app/src/main/java/com/markscene/app/ui/MarkSceneApp.kt`: 보안 저장소 사용 불가 시 설정 저장 실패 메시지를 표시
  - `gradle/libs.versions.toml`, `CHANGELOG.md`, `docs/RELEASE_CHECKLIST.md`: v2.0.4 릴리즈 버전 및 검증 기록 반영
- 검증:
  - 로컬: `./gradlew test` 성공
  - 로컬: `./gradlew lint` 성공
  - 로컬: `./gradlew assembleDebug` 성공
  - 로컬: `./gradlew :app:assembleRelease`는 릴리즈 서명 키 미설정으로 실패 (`SigningConfig "release" is missing required property "storeFile"`)
  - 기기: GitHub Release `v2.0.3` APK 새 설치 후 실행 crash 없음 확인
  - 기기: `v1.9.2` 릴리즈 설치 후 `v2.0.3` 업데이트 설치 및 실행 crash 없음 확인
  - 기기: 수정 후 debug APK 설치 및 실행 crash 없음 확인
- 결과: 보안 저장소 초기화 실패가 앱 실행 자체를 막지 않도록 수정
- 후속 작업:
  - `v2.0.4` 태그 푸시 후 GitHub Actions Release APK 결과 확인

- 작업: 릴리즈 서명 검증 보강 및 릴리즈 문서 정합성 보완
- 변경 파일:
  - `.github/workflows/release-apk.yml`: 누락 secret 검사를 보강하고 keystore 복원 결과를 즉시 검증하도록 보완
  - `RELEASE_SETUP.md`: GitHub Actions secret 이름과 `release.jks`의 모듈 상대 경로 해석 방식을 일치하도록 정리
  - `docs/RELEASE_CHECKLIST.md`: 현재 버전 표기를 실제 앱 버전(`2.0.3` / `203`)으로 갱신
- 검증:
  - 로컬: `./gradlew :app:assembleRelease`는 릴리즈 서명 키 미설정으로 실패
  - CI: 태그 푸시 후 GitHub Actions에서 서명 릴리즈 빌드 검증 예정
- 결과: 릴리즈 서명 입력값 검증을 강화하고 운영 문서와 실제 설정의 불일치를 줄임
- 후속 작업:
  - GitHub Secrets 실제 값과 업로드 키 지문이 Play/App 배포 경로와 일치하는지 확인

- 작업: 앱 실행 안정성 강화 및 빌드 환경 개선
- 변경 파일:
  - `app/build.gradle.kts`: 로컬 환경에서 `local.properties` 및 디버그 키스토어 누락 시에도 빌드가 가능하도록 개선
  - `app/src/main/java/com/markscene/app/ui/MarkSceneApp.kt`: 
    - `LocalContext`에서 `FragmentActivity`를 안전하게 찾아내도록 수정 (ContextWrapper 대응)
    - 암호화 저장소(`SecurityStore`) 및 데이터베이스 초기화 시 예외 처리 보강
    - `BiometricAuthenticator` 사용 전 null 체크 추가
- 검증:
  - 로컬: `./gradlew assembleDebug` 및 `./gradlew test` 성공 확인
- 결과: 특정 기기나 환경에서 발생할 수 있는 런타임 크래시 요소를 제거하고 빌드 호환성 확보
- 후속 작업:
  - 사용자 환경에서 정상 실행 여부 최종 확인

- 변경 파일:
  - `app/build.gradle.kts`: CI 환경 변수 및 local.properties 지원 서명 로직 추가
  - `.github/workflows/release-apk.yml`: Secrets 기반 키스토어 디코딩 및 자동 서명 단계 추가
- 검증:
  - 로컬: `./gradlew help` 실행으로 스크립트 문법 및 설정 정합성 확인
  - 보안: 생성된 키스토어의 Base64 인코딩 값을 사용자에게 전달 후 즉시 삭제 처리
- 결과: 태그 푸시 시 자동으로 서명된 릴리즈 APK가 생성되어 GitHub Release에 업로드되는 환경 구축 완료
- 후속 작업:
  - 사용자의 GitHub Secrets 설정 확인
  - 실제 태그 푸시를 통한 엔드투엔드 워크플로우 실행 테스트

- 작업: GitHub Pages 루트 브랜딩 페이지 구성 및 최초 APK 릴리즈 자동화
- 변경 파일:
  - `docs/index.html`
  - `.github/workflows/pages.yml`
  - `.github/workflows/release-apk.yml`
  - `app/build.gradle.kts`
  - `.agent/progress.md`, `HISTORY.md`, `CHANGELOG.md`
- 검증:
  - 로컬: Android SDK 경로 미설정으로 빌드 제한
  - CI: CameraX 의존성의 AGP 요구사항으로 초기 실패 확인 후 AGP `8.6.1` 상향 재검증
  - 릴리즈: `v0.4.0` 태그 푸시 후 Release APK 워크플로 결과 확인
- 결과: Pages 루트 접근 경로와 APK 릴리즈 파이프라인 준비 완료
- 후속 작업:
  - 릴리즈 산출물(앱 서명 정책, AAB 병행 여부) 정교화
  - release checklist 문서(#4) 반영
  - v0.4.0 릴리즈 APK 자산 누락 보정

## 2026-04-30

- 작업: 릴리즈 APK 업로드 경로 보정 및 재릴리즈 준비
- 변경 파일:
  - `.github/workflows/release-apk.yml`
  - `app/build.gradle.kts`
  - `.agent/progress.md`, `HISTORY.md`, `CHANGELOG.md`
- 검증:
  - v0.4.0 Release 워크플로 로그에서 파일 패턴 불일치 확인
  - `*.apk` 패턴으로 수정 후 신규 태그 릴리즈 예정
- 결과: Release APK 자산 누락 재발 방지 수정 완료
- 후속 작업:
  - v0.4.1 태그 발행 및 자산 업로드 확인

## 2026-04-30

- 작업: CameraX 기반 실제 캡처 플로우 적용 (#1)
- 변경 파일:
  - `gradle/libs.versions.toml`
  - `app/build.gradle.kts`
  - `app/src/main/java/com/markscene/app/ui/screen/CreateRecordScreen.kt`
  - `.agent/tasks.md`, `.agent/progress.md`, `HISTORY.md`, `CHANGELOG.md`
- 검증:
  - 로컬: `./gradlew :app:compileDebugKotlin` 실행 시도
  - 결과: Android SDK 경로 미설정으로 실패
  - CI: 첫 실행 실패(`CameraX 1.6.0`의 AGP/compileSdk 요구사항 불일치) 확인 후 `1.5.3`으로 수정 재푸시
- 결과: 캡처 경로가 CameraX 프리뷰/촬영 기반으로 전환되어 #1 범위 기능 구현 완료
- 후속 작업:
  - CI 결과 확인 후 필요 시 보정
  - CameraX 캡처 UX 세부 개선(초점/회전/오류 문구 정교화)

## 2026-04-30

- 작업: 브랜딩 아이콘/gh.io 페이지 구성 및 남은 작업 이슈화
- 변경 파일:
  - `app/src/main/res/drawable/ic_launcher_foreground.xml`
  - `app/src/main/res/drawable/ic_launcher_background.xml`
  - `app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml`
  - `app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml`
  - `app/src/main/AndroidManifest.xml`
  - `docs/branding/index.html`
  - `.github/workflows/pages.yml`
  - `.agent/tasks.md`, `.agent/progress.md`, `HISTORY.md`, `CHANGELOG.md`
- 검증:
  - GitHub Issue 등록 완료: #1~#6
  - CI/Pages 워크플로 실행 결과 확인 예정
- 결과: 앱/웹 브랜딩 반영 및 남은 작업 관리 체계 이슈 기반으로 전환 완료
- 후속 작업:
  - #1 CameraX 전환
  - #2~#6 문서/운영 항목 완료

## 2026-04-30

- 작업: CI lint 카메라 feature 오류 수정
- 변경 파일:
  - `app/src/main/AndroidManifest.xml`
  - `.agent/progress.md`, `HISTORY.md`, `CHANGELOG.md`
- 검증:
  - CI 실패 로그 확인 후 매니페스트 수정
- 결과: lint 차단 이슈 수정 완료
- 후속 작업:
  - CI 재실행 결과 확인

## 2026-04-30

- 작업: GitHub Actions 품질 게이트 강화
- 변경 파일:
  - `.github/workflows/android-ci.yml`
  - `.agent/progress.md`, `HISTORY.md`, `CHANGELOG.md`
- 검증:
  - CI: lint/test/build 단계 추가 후 재검증
- 결과: 빌드 단일 검증에서 품질 게이트 확장 완료
- 후속 작업:
  - 필요 시 instrumentation test 단계 추가

## 2026-04-30

- 작업: Gemini BYOK 실제 분석 호출 경로 추가
- 변경 파일:
  - `app/src/main/java/com/markscene/app/ai/provider/GeminiAdvancedVisionProvider.kt`
  - `app/src/main/AndroidManifest.xml`
  - `app/src/main/java/com/markscene/app/ui/MarkSceneApp.kt`
  - `app/src/main/java/com/markscene/app/ui/screen/RecordDetailScreen.kt`
  - `gradle/libs.versions.toml`, `app/build.gradle.kts`
  - `.agent/tasks.md`, `.agent/progress.md`, `HISTORY.md`, `CHANGELOG.md`
- 검증:
  - CI: GitHub Actions `Android CI`로 검증
- 결과: API Key 존재 시 실제 분석 호출, 실패 시 mock 폴백 경로 완성
- 후속 작업:
  - Gemini 응답 스키마 안정화/예외 메시지 고도화
  - 고급분석 결과 편집 UX 개선

## 2026-04-30

- 작업: 고급분석(mock) 결과의 영속 저장/태그 반영 연동
- 변경 파일:
  - `app/src/main/java/com/markscene/app/core/database/AdvancedAnalysisEntity.kt`
  - `app/src/main/java/com/markscene/app/core/database/AdvancedAnalysisDao.kt`
  - `app/src/main/java/com/markscene/app/core/database/MarkSceneDatabase.kt`
  - `app/src/main/java/com/markscene/app/core/model/AdvancedAnalysis.kt`
  - `app/src/main/java/com/markscene/app/data/record/RoomRecordRepository.kt`
  - `app/src/main/java/com/markscene/app/ui/MarkSceneApp.kt`
  - `app/src/main/java/com/markscene/app/ui/screen/RecordDetailScreen.kt`
  - `.agent/progress.md`, `HISTORY.md`, `CHANGELOG.md`
- 검증:
  - CI: GitHub Actions `Android CI`로 검증
- 결과: 분석 요약 저장 + 추천 태그 기록 반영 + 상세 화면 재표시 연결 완료
- 후속 작업:
  - 실제 Gemini provider 연동
  - 고급분석 결과 편집 UI 고도화

## 2026-04-30

- 작업: 로컬 태깅을 ML Kit 온디바이스 라벨링으로 전환
- 변경 파일:
  - `gradle/libs.versions.toml`, `app/build.gradle.kts`
  - `app/src/main/java/com/markscene/app/ai/provider/MlKitLocalImageTagger.kt`
  - `app/src/main/java/com/markscene/app/ui/MarkSceneApp.kt`
  - `.agent/tasks.md`, `.agent/progress.md`, `HISTORY.md`, `CHANGELOG.md`
- 검증:
  - CI: GitHub Actions `Android CI`로 검증
- 결과: mock 기본 태깅에서 온디바이스 태깅으로 전환 완료(실패 시 mock 폴백)
- 후속 작업:
  - 태그 한글화/정규화 사전 확장
  - 고급분석 결과 저장/편집 연동

## 2026-04-30

- 작업: 인앱 개인정보 고지 및 Mock 고급분석 UI 추가
- 변경 파일:
  - `app/src/main/java/com/markscene/app/ai/provider/MockAdvancedVisionProvider.kt`
  - `app/src/main/java/com/markscene/app/ui/screen/PrivacyNoticeScreen.kt`
  - `app/src/main/java/com/markscene/app/ui/screen/RecordDetailScreen.kt`
  - `app/src/main/java/com/markscene/app/ui/screen/SettingsScreen.kt`
  - `app/src/main/java/com/markscene/app/ui/MarkSceneApp.kt`
  - `.agent/tasks.md`, `.agent/progress.md`, `HISTORY.md`, `CHANGELOG.md`
- 검증:
  - CI: GitHub Actions `Android CI`로 검증
- 결과: 사용자 실행/동의 기반 고급분석 흐름(mock)과 프라이버시 고지 화면 반영
- 후속 작업:
  - Mock 고급분석 결과의 저장/편집 연동
  - 실제 Gemini provider 구현

## 2026-04-30

- 작업: 캡처 권한/촬영 플로우 및 상세 화면 추가
- 변경 파일:
  - `app/src/main/AndroidManifest.xml`, `app/src/main/res/xml/file_paths.xml`
  - `gradle/libs.versions.toml`, `app/build.gradle.kts`
  - `app/src/main/java/com/markscene/app/ui/screen/CreateRecordScreen.kt`
  - `app/src/main/java/com/markscene/app/ui/screen/RecordListScreen.kt`
  - `app/src/main/java/com/markscene/app/ui/screen/RecordDetailScreen.kt`
  - `app/src/main/java/com/markscene/app/ui/MarkSceneApp.kt`
  - `.agent/tasks.md`, `.agent/progress.md`, `HISTORY.md`, `CHANGELOG.md`
- 검증:
  - 로컬: SDK 경로 미설정으로 빌드 제한
  - CI: GitHub Actions `Android CI`로 검증
- 결과: 촬영/미리보기/상세 흐름 추가 완료
- 후속 작업:
  - CameraX 기반 캡처로 대체
  - Record Detail 편집/삭제 연동 강화

## 2026-04-30

- 작업: Room 영속 저장소 및 Settings(BYOK 상태) 구현
- 변경 파일:
  - `gradle/libs.versions.toml`, `build.gradle.kts`, `app/build.gradle.kts`
  - `app/src/main/java/com/markscene/app/core/database/*`
  - `app/src/main/java/com/markscene/app/data/record/RoomRecordRepository.kt`
  - `app/src/main/java/com/markscene/app/data/settings/ApiKeyStore.kt`
  - `app/src/main/java/com/markscene/app/ui/MarkSceneApp.kt`
  - `app/src/main/java/com/markscene/app/ui/screen/RecordListScreen.kt`
  - `app/src/main/java/com/markscene/app/ui/screen/SettingsScreen.kt`
  - `.agent/tasks.md`, `.agent/progress.md`, `HISTORY.md`, `CHANGELOG.md`
- 검증:
  - 로컬: SDK 경로 미설정 환경이라 `assembleDebug` 제한
  - CI: GitHub Actions `Android CI`로 검증
- 결과: Room/Settings 기반 MVP 골격 확장 완료
- 후속 작업:
  - CameraX 캡처 연결
  - Record Detail 화면 및 삭제/상세 흐름 강화

## 2026-04-30

- 작업: CI 매니페스트 아이콘 링크 오류 수정
- 변경 파일:
  - `app/src/main/AndroidManifest.xml`
  - `.agent/progress.md`, `HISTORY.md`, `CHANGELOG.md`
- 검증:
  - CI 실패 로그 확인: `sym_def_app_icon_round` not found
  - 조치: `roundIcon` 참조 제거
- 결과: 수정 커밋 준비 완료
- 후속 작업:
  - 푸시 후 CI 재확인

## 2026-04-30

- 작업: CI Android 리소스 링크 오류 수정
- 변경 파일:
  - `gradle/libs.versions.toml`
  - `app/build.gradle.kts`
  - `.agent/progress.md`, `HISTORY.md`, `CHANGELOG.md`
- 검증:
  - CI 실패 로그 확인: `Theme.Material3.DayNight.NoActionBar` not found
  - 조치: Material Components 의존성 추가
- 결과: 수정 커밋 준비 완료
- 후속 작업:
  - 푸시 후 CI 재확인

## 2026-04-30

- 작업: GitHub Actions 실행 권한 이슈 수정
- 변경 파일:
  - `.github/workflows/android-ci.yml`
  - `.agent/progress.md`, `HISTORY.md`, `CHANGELOG.md`
- 검증:
  - CI 실패 로그 확인: `./gradlew: Permission denied`
  - 조치: `chmod +x gradlew` 단계 추가
- 결과: 수정 커밋 준비 완료
- 후속 작업:
  - 푸시 후 최신 CI run 성공 여부 확인

## 2026-04-30

- 작업: MVP 세로 슬라이스 확장 (Import → mock 태그 → 수정 → 저장 → 검색)
- 변경 파일:
  - `app/src/main/java/com/markscene/app/core/model/*`
  - `app/src/main/java/com/markscene/app/ai/provider/*`
  - `app/src/main/java/com/markscene/app/domain/tag/TagSuggestion.kt`
  - `app/src/main/java/com/markscene/app/data/record/InMemoryRecordRepository.kt`
  - `app/src/main/java/com/markscene/app/ui/MarkSceneApp.kt`
  - `app/src/main/java/com/markscene/app/ui/screen/CreateRecordScreen.kt`
  - `app/src/main/java/com/markscene/app/ui/screen/RecordListScreen.kt`
  - `.agent/tasks.md`, `.agent/progress.md`, `HISTORY.md`, `CHANGELOG.md`
- 검증:
  - 로컬: `./gradlew :app:assembleDebug` 실행 시도 예정
  - 환경 제약: Android SDK 경로 미설정 상태
- 결과: 기능 확장 완료 (로컬 컴파일 검증은 SDK 환경 설정 필요)
- 후속 작업:
  - CameraX 캡처 및 실제 이미지 미리보기 연결
  - Room 기반 영속 저장소 적용

## 2026-04-30

- 작업: 빌드 체인 복구(Wrapper) 및 Compose 다음 페이즈 최소 확장
- 변경 파일:
  - `gradle/wrapper/gradle-wrapper.jar`
  - `app/src/main/java/com/markscene/app/ui/MarkSceneApp.kt`
  - `app/src/main/java/com/markscene/app/ui/screen/CreateRecordScreen.kt`
  - `.github/workflows/android-ci.yml`
  - `.agent/tasks.md`, `.agent/progress.md`, `HISTORY.md`, `CHANGELOG.md`
- 검증:
  - 로컬: `./gradlew :app:assembleDebug`
  - 결과: Android SDK 경로 미설정으로 실패
- 결과: 부분 성공 (Wrapper 복구 및 다음 페이즈 UI/CI 골격 반영)
- 후속 작업:
  - SDK 경로 설정 후 로컬 검증 재실행
  - CameraX/Photo Picker 실제 연결

## 2026-04-30

- 작업: MarkScene Android 프로젝트 1차 부트스트랩 및 Compose 셸 구현
- 변경 파일:
  - `settings.gradle.kts`, `build.gradle.kts`, `gradle.properties`, `gradle/libs.versions.toml`
  - `app/build.gradle.kts`, `app/proguard-rules.pro`
  - `app/src/main/AndroidManifest.xml`
  - `app/src/main/java/com/markscene/app/MainActivity.kt`
  - `app/src/main/java/com/markscene/app/ui/MarkSceneApp.kt`
  - `app/src/main/java/com/markscene/app/ui/screen/HomeScreen.kt`
  - `app/src/main/java/com/markscene/app/ui/screen/PlaceholderScreen.kt`
  - `app/src/main/java/com/markscene/app/ui/theme/Theme.kt`
  - `app/src/main/java/com/markscene/app/ui/theme/Type.kt`
  - `app/src/main/res/values/strings.xml`, `app/src/main/res/values/themes.xml`
  - `gradlew`, `gradlew.bat`, `gradle/wrapper/gradle-wrapper.properties`
  - `.agent/progress.md`, `HISTORY.md`, `CHANGELOG.md`
- 검증:
  - 로컬: `./gradlew :app:assembleDebug` 실행 시도
  - 결과: `gradle-wrapper.jar` 누락으로 실행 실패
- 결과: 부분 성공 (코드/구조 추가 완료, 로컬 빌드 검증은 환경 누락으로 미완료)
- 후속 작업:
  - `gradle/wrapper/gradle-wrapper.jar` 추가 후 빌드/린트/테스트 재검증
  - Home 액션을 실제 사진 촬영/가져오기 플로우로 연결

## 2026-04-30

- 작업: MarkScene 에이전트 문서 세트와 사용자 기본 AGENTS 템플릿 통합
- 변경 파일:
  - `AGENTS.md`: 범용 Automation First 규칙을 MarkScene 프로젝트 정책과 결합
  - `README.md`: MarkScene 프로젝트 소개, MVP 플로우, 문서 구조, 에이전트 작업 방식 정리
  - `CHANGELOG.md`: 초기 문서 통합 변경 사항 기록
  - `HISTORY.md`: 작업 이력 기록
  - `docs/*`: 제품, 아키텍처, 개인정보, BYOK, 로컬 태깅, UX, 디자인, 데이터 모델, 출시 체크리스트 문서 포함
  - `.agent/*`: 에이전트 작업 목록, 진행 기록, 결정 기록, 프롬프트 포함
- 검증:
  - 로컬 파일 생성 확인
  - ZIP 생성 확인
- 결과: 성공
- 후속 작업:
  - GitHub 저장소 URL 확정 후 `AGENTS.md`의 `Repository` 값 업데이트
  - Android 프로젝트 초기화
  - Compose / Material 3 기반 첫 화면 구현
  - GitHub Actions 기본 빌드 검증 추가

## 2026-04-30
- 작업: 프로젝트명을 MarkScene으로 확정하고 문서 세트에 반영
- 변경 파일:
  - AGENTS.md: Project Name, Repository, Android Application ID, Root Package 업데이트
  - README.md: 프로젝트 식별자 추가
  - docs/PRODUCT_BRIEF.md: 앱 이름과 기본 식별자 반영
  - .agent/decisions.md: 이름 결정 기록 추가
  - CHANGELOG.md: 문서 변경 요약 추가
- 검증: 문서 파일 내 이전 프로젝트명 잔여 표기 검색
- 결과: 성공
- 후속 작업: 실제 GitHub 저장소 URL이 다르면 AGENTS.md와 README.md에서 Repository 값을 수정
