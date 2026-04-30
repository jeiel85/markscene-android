# Agent Progress

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
