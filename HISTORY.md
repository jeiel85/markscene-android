# HISTORY.md

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
