# CHANGELOG.md

이 문서는 MarkScene의 사용자에게 공개 가능한 변경 사항을 기록합니다.

## v0.1.1 - 2026-04-30

### Added
- Android 앱 기본 프로젝트 구조를 추가했습니다.
- Jetpack Compose + Material 3 기반의 첫 화면 셸을 추가했습니다.
- Home 화면에 `Capture Photo`, `Import Photo`, `Settings` 액션을 추가했습니다.
- `Create Record`, `Record List / Search`, `Settings` 플레이스홀더 화면 내비게이션을 추가했습니다.

### Security
- 금지 권한(`MANAGE_EXTERNAL_STORAGE`, 광범위 미디어 권한)을 추가하지 않았습니다.
- 외부 AI 호출 및 API Key 하드코딩 없이 로컬 우선 초기 구조만 구성했습니다.

### Verification
- 로컬: `./gradlew :app:assembleDebug` 실행 시도
- 로컬 결과: `gradle-wrapper.jar` 누락으로 실행 실패
- CI: 아직 구성되지 않음

## v0.1.2 - 2026-04-30

### Added
- `Create Record` 화면 분리 및 `Capture`/`Import` 진입 소스 구분 라우팅을 추가했습니다.
- GitHub Actions 기반 Android 기본 CI 워크플로를 추가했습니다.

### Changed
- 누락되어 있던 `gradle-wrapper.jar`를 추가해 Gradle Wrapper 실행 불가 상태를 복구했습니다.

### Verification
- 로컬: `./gradlew :app:assembleDebug` 실행
- 로컬 결과: Android SDK 경로 미설정으로 실패 (`ANDROID_HOME` 또는 `local.properties` 필요)
- CI: 워크플로 파일 추가 완료 (실행 결과는 다음 푸시 후 확인)

## v0.2.0 - 2026-04-30

### Added
- `PhotoRecord`, `PhotoTag` 중심의 기본 도메인 모델을 추가했습니다.
- `LocalImageTagger` 인터페이스와 `MockLocalImageTagger`를 추가했습니다.
- 인메모리 저장소를 통해 기록 저장/조회 기본 흐름을 추가했습니다.
- `Record List / Search` 화면과 태그/제목/메모 검색 기능을 추가했습니다.

### Changed
- `Create Record` 화면을 import 중심 실제 입력 흐름으로 확장했습니다.
- Photo Picker로 선택한 이미지에 대해 mock 로컬 태그 초안을 생성하고, 사용자가 태그를 삭제/추가 후 저장할 수 있도록 변경했습니다.
- 저장 완료 후 검색 화면으로 이동하도록 내비게이션을 연결했습니다.

### Privacy
- 광범위 미디어 권한 없이 Photo Picker만 사용하도록 유지했습니다.
- 외부 AI 호출 없이 로컬/mock 기반으로만 동작하도록 유지했습니다.

### Verification
- 로컬: Android SDK 경로 미설정으로 빌드 검증 제한
- CI: GitHub Actions 워크플로는 구성됨, 실행 결과는 저장소 Actions 상태 확인 필요

## v0.1.0 - 2026-04-30

### Added
- MarkScene project naming, repository naming, and Android application ID guidance added.
- MarkScene 초기 제품 문서 세트 추가
- AI 코딩 에이전트를 위한 통합 `AGENTS.md` 규칙 추가
- 제품 요구사항, 아키텍처, 개인정보/보안, BYOK 전략, 로컬 태깅, UX, 디자인 시스템 문서 추가
- `.agent/tasks.md`, `.agent/progress.md`, `.agent/decisions.md`, `.agent/prompts.md` 추가

### Changed
- 기존 범용 AGENTS 템플릿의 Automation First, CI 우선 검증, HISTORY/CHANGELOG 중심 이력관리 규칙을 MarkScene 프로젝트 기준으로 통합

### Privacy
- API Key 없는 로컬 우선 기본 경험을 프로젝트 원칙으로 정의
- BYOK 고급 AI 분석은 선택 기능으로 정의
- 광범위한 갤러리 접근, 전체 사진 스캔, 자동 업로드, 개발자 기본 API Key 포함을 금지 규칙으로 명시

### Documentation
- 첫 번째 에이전트 코딩 프롬프트와 반복 작업 프롬프트를 `.agent/prompts.md`에 정리
- Play Store 출시 전 개인정보/권한 체크리스트 추가

### Verification
- 로컬: 문서 파일 생성 및 ZIP 패키징 확인
- CI: 아직 구성되지 않음
