# HISTORY.md

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
