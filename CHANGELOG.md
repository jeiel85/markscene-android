# CHANGELOG.md

이 문서는 MarkScene의 사용자에게 공개 가능한 변경 사항을 기록합니다.

## v1.4.0 - 2026-05-01 🧠

### Added (Personalized Tagging)
- **사용자 교정 사전**: AI의 태그 제안을 사용자가 수정하면 이를 학습하여 다음번에 더 정확한 태그를 제안하는 개인화 기능을 추가했습니다.
- **지능형 태그 보정**: 사용자의 언어 습관(예: "Portable computer" -> "노트북")을 기억하여 로컬 태깅 결과에 자동으로 반영합니다.
- **학습 사전 관리**: 설정 화면에서 앱이 학습한 태그 교정 내역을 확인하고 삭제할 수 있는 관리 UI를 도입했습니다.

### Changed
- **태깅 파이프라인 확장**: `MlKitLocalImageTagger`에 동적 정규화 사전을 통합하여 개인화된 결과를 도출합니다.
- **데이터베이스 마이그레이션**: 태그 교정 정보 저장을 위해 스키마를 업데이트(v4 -> v5)했습니다.

### Build / CI
- 앱 버전을 `v1.4.0` (versionCode 140)으로 업데이트했습니다.

## v1.3.0 - 2026-05-01 🕰️

### Added (Space Evolution)
- **공간 타임라인**: 특정 공간의 기록들을 시간순으로 역동적으로 탐색할 수 있는 타임라인 뷰를 추가했습니다.
- **비포 & 애프터 비교**: 같은 공간의 두 기록을 선택하여 시각적 변화를 나란히 비교할 수 있는 전용 뷰어를 도입했습니다.
- **대시보드 통합**: 홈 화면의 공간 요약 카드에서 즉시 해당 공간의 타임라인으로 진입할 수 있도록 내비게이션을 연결했습니다.

### Changed
- **UX 폴리싱**: 상세 화면에서 히스토리 아이템 간의 유기적인 탐색 흐름을 강화했습니다.
- **아이콘 라이브러리 확장**: 비교 및 히스토리 UI를 위해 `material-icons-extended` 활용 범위를 넓혔습니다.

### Build / CI
- 앱 버전을 `v1.3.0` (versionCode 130)으로 업데이트했습니다.

## v1.2.0 - 2026-05-01 📍

### Added (Item Location Tracking)
- **물건 위치 추적 ("이 물건 어디 있지?")**: 특정 물건(태그)의 과거 위치 기록을 시계열로 확인할 수 있는 기능을 추가했습니다.
- **위치 히스토리 UI**: 상세 화면 하단에 해당 물건이 머물렀던 공간들과 기록 시점을 보여주는 전용 섹션을 도입했습니다.
- **심리스한 탐색**: 히스토리 아이템을 클릭하여 해당 시점의 기록으로 즉시 이동할 수 있습니다.

### Changed
- **데이터 엔진 고도화**: 특정 태그를 포함한 기록들을 역추적하는 `observeRecordsByTag` 쿼리를 리포지토리에 추가했습니다.
- **상세 화면 최적화**: 위치 정보와 히스토리를 잡지 스타일 레이아웃에 유기적으로 통합했습니다.

### Build / CI
- 앱 버전을 `v1.2.0` (versionCode 120)으로 업데이트했습니다.

## v1.1.0 - 2026-05-01 🚀

### Added (Space-based Organization)
- **공간 기반 관리**: 기록을 '책상', '주방', '창고' 등 특정 공간별로 분류할 수 있는 기능을 추가했습니다.
- **공간 대시보드**: 홈 화면에서 각 공간별 기록 현황을 한눈에 파악할 수 있는 요약 카드를 도입했습니다.
- **공간별 탐색**: 기록 목록에서 공간 필터 칩을 통해 특정 장소의 메모만 모아볼 수 있습니다.

### Changed
- **데이터 모델 확장**: `PhotoRecord`에 공간(`space`) 필드를 추가하고 데이터베이스 마이그레이션(v3 -> v4)을 완료했습니다.
- **생성 UX 개선**: 기록 저장 시 공간을 손쉽게 선택할 수 있는 칩 선택 UI를 추가했습니다.

### Build / CI
- 앱 버전을 `v1.1.0` (versionCode 110)으로 업데이트했습니다.

## v1.0.0 - 2026-05-01 🎉

### Major Milestones
- **MarkScene 정식 출시**: 모든 핵심 요건 구현을 완료하고 대망의 1.0.0 버전을 달성했습니다.
- **통합 지능형 검색 (Issue #7)**: 기기 내 OCR 도입으로 사진 속 텍스트까지 검색 가능한 똑똑한 탐색 환경을 구축했습니다.
- **데이터 주권 보장 (Issue #8)**: ZIP 기반 로컬 백업 및 복구 기능을 통해 사용자의 소중한 기록을 안전하게 관리합니다.
- **강력한 프라이버시 (Issue #9)**: 생체 인식 잠금 레이어를 추가하여 시각적 메모를 타인으로부터 완벽히 보호합니다.
- **심리스한 연결성 (Issue #10)**: 외부 앱 '공유하기' 인텐트 수신을 통해 언제 어디서든 즉시 기록할 수 있습니다.
- **저장 효율 극대화 (Issue #11)**: 이미지 자동 최적화 및 WebP 압축을 통해 기기 저장 공간을 스마트하게 아낍니다.

### Added
- `ImageOptimizer`: 저장 시 자동 리사이징 및 고효율 압축 엔진 도입.
- `BiometricAuthenticator`: 생체 인식 보안 가드 시스템 구축.
- `BackupManager`: 통합 데이터 내보내기/가져오기 시스템 구현.
- `MlKitTextRecognizer`: 한국어/영어 지원 로컬 OCR 엔진 통합.

### Build / CI
- 앱 버전을 상징적인 `v1.0.0` (versionCode 100)으로 업데이트했습니다.
- 프로젝트의 모든 자동화 파이프라인(CI, Release APK)이 최적의 상태임을 확인했습니다.

## v0.9.0 - 2026-05-01

### Added (Issue #10)
- **외부 공유로 기록하기**: 갤러리, 브라우저 등 다른 앱의 '공유하기' 메뉴를 통해 MarkScene에 바로 기록할 수 있는 기능을 추가했습니다.
- **자동 분석 연동**: 공유받은 이미지는 즉시 미리보기와 함께 AI 태그 및 OCR 분석이 수행되어 신속한 기록이 가능합니다.

### Changed
- **내비게이션 고도화**: 인텐트 수신 시 홈 화면을 거치지 않고 바로 '기록 생성' 화면으로 이동하는 딥링크 방식의 UX를 구현했습니다.
- **매니페스트 업데이트**: 이미지 수신을 위한 `intent-filter`를 추가했습니다.

### Build / CI
- 앱 버전을 `v0.9.0` (versionCode 50)으로 업데이트했습니다.

## v0.8.0 - 2026-05-01

### Added (Issue #9)
- **생체 인식 잠금**: 앱 실행 시 지문이나 얼굴 인식을 통한 보안 인증 기능을 추가했습니다.
- **보안 설정**: 설정 화면에서 생체 인식 잠금의 활성화 여부를 자유롭게 토글할 수 있습니다.
- **통합 인증 UI**: 안드로이드 표준 BiometricPrompt를 활용하여 신뢰도 높고 일관된 인증 경험을 제공합니다.

### Changed
- **보안 저장소 확장**: 생체 인식 설정 상태를 `SecurityStore`를 통해 안전하게 영구 저장합니다.
- **앱 시작 프로세스**: 보안 잠금 활성화 시 인증 성공 전까지 데이터를 보호하는 잠금 화면 레이어를 도입했습니다.

### Build / CI
- 앱 버전을 `v0.8.0` (versionCode 40)으로 업데이트했습니다.
- `androidx.biometric` 의존성을 추가했습니다.

## v0.7.0 - 2026-05-01

### Added (Issue #8)
- **로컬 데이터 백업 및 복구**: 모든 기록과 사진을 단일 ZIP 파일로 내보내고 다시 가져올 수 있는 기능을 추가했습니다.
- **데이터 주권 강화**: 클라우드 의존 없이 사용자가 직접 파일 형태로 자신의 데이터를 보관하고 이동할 수 있습니다.
- **통합 백업 UI**: 설정 화면에서 '데이터 내보내기/가져오기' 기능을 직관적으로 사용할 수 있도록 배치했습니다.

### Changed
- **직렬화 지원**: 데이터 전송 및 저장을 위해 핵심 도메인 모델에 `kotlinx-serialization` 지원을 추가했습니다.
- **결과 피드백 강화**: 백업 및 복구 작업의 성공/실패 여부를 사용자에게 즉시 알리는 Snackbar 시스템을 구축했습니다.

### Build / CI
- 앱 버전을 `v0.7.0` (versionCode 30)으로 업데이트했습니다.
- `kotlinx-serialization-json` 의존성 및 플러그인을 추가했습니다.

## v0.6.0 - 2026-05-01

### Added (Issue #7)
- **로컬 OCR 도입**: 사진 속 텍스트를 기기 내에서 자동으로 인식하는 기능을 추가했습니다 (ML Kit Text Recognition v2).
- **텍스트 기반 검색 강화**: 추출된 텍스트(`ocrText`)를 검색 인덱스에 포함하여 영수증, 명함 등의 내용으로도 검색이 가능해졌습니다.
- **한국어 인식 지원**: 한국어와 영어를 모두 지원하는 전용 OCR 엔진을 통합했습니다.

### Changed
- **데이터베이스 마이그레이션**: `ocrText` 필드 추가에 따른 스키마 업데이트 (v2 -> v3) 및 마이그레이션 로직을 구현했습니다.
- **분석 파이프라인 고도화**: 기록 생성 시 태그 추출과 OCR이 백그라운드에서 병렬로 수행되도록 개선했습니다.

### Build / CI
- 앱 버전을 `v0.6.0` (versionCode 20)으로 업데이트했습니다.
- ML Kit Text Recognition (Korean) 의존성을 추가했습니다.

## v0.5.3 - 2026-04-30

### Changed (Design Refresh)
- **전면 UI/UX 개편**: 테스트 앱 수준의 UI를 세련된 상용 서비스 품질로 리프레시했습니다.
- **브랜드 정체성 확립**: MarkScene만의 커스텀 컬러 팔레트와 타이포그래피(Pretendard 기반 스타일)를 적용했습니다.
- **갤러리 스타일 목록**: 기록 목록에 Masonry Grid(Staggered Grid)를 도입하여 사진 비율을 살린 역동적인 뷰를 구현했습니다.
- **대시보드형 홈 화면**: 단순 버튼 나열에서 벗어나 시각적인 퀵 액션과 브랜드 헤더가 포함된 대시보드 스타일로 개편했습니다.
- **잡지 스타일 상세 화면**: 사진의 시각적 몰입감을 극대화한 레이아웃과 'AI Insight' 카드 섹션을 도입했습니다.
- **감각적인 기록 생성**: 상태 인디케이터, 세련된 미리보기 카드, 동적 태그 칩 UI를 통해 생성 경험을 개선했습니다.
- **신뢰감 있는 설정/보안**: 섹션 구분과 아이콘을 활용하여 설정 및 개인정보 고지 화면의 가독성을 높였습니다.

### Build / CI
- 앱 버전을 `v0.5.0` (versionCode 10)으로 업데이트했습니다.
- UI 테스트 및 안정성 확인을 위해 `libs.versions.toml` 및 `build.gradle.kts`를 정비했습니다.

## v0.4.5 - 2026-04-30

### Build / CI
- 릴리즈용 키스토어 생성 및 GitHub Secrets 연동 설정.
- `app/build.gradle.kts`에 CI 환경 변수 기반 서명 로직 추가.
- `.github/workflows/release-apk.yml` 워크플로우를 Secrets 기반 자동 서명 방식으로 고도화.

### Documentation
- MIT License (`LICENSE`) 추가 (#5).
- 배포용 개인정보 처리방침 초안 (`docs/PRIVACY_POLICY.md`) 추가 (#3).
- `RELEASE_SETUP.md` (릴리즈 서명 설정 가이드) 추가.


### Changed
- README.md 개선 (다른 레포 대비 내용 보강).
- 브랜딩 페이지 (`docs/index.html`) 기능별 섹션 추가 및 디자인 개선.
- `docs/RELEASE_CHECKLIST.md` 최신 구현 상태 반영 (#4).
- `.agent/tasks.md` 작업 목록 동기화 및 완료 처리 (#6).
- `app/build.gradle.kts` 테스트 의존성 추가, 릴리즈 서명 설정 추가.
- `app/src/main/AndroidManifest.xml` `roundIcon` 참조 제거 (APK 무효 오류 수정).

### Fixed
- APK 설치 시 "invalid" 오류 원인 파악 및 서명 설정 추가.
- .gitignore 보안 정책 점검 및 커버리지 확인 (#2).

### Security
- 민감 파일 커버리지 확인 (.gitignore 충분히 커버).

### Verification
- 로컬: Android SDK 경로 미설정으로 빌드 제한
- CI: GitHub Actions `Android CI`에서 `lint + test + assembleDebug` 실행 예정
- 테스트: 단위 테스트 파일 생성 완료 (CI 자동 실행)

## v0.4.1 - 2026-04-30

### Fixed
- GitHub Release APK 업로드 경로를 실제 산출물 패턴(`app/build/outputs/apk/release/*.apk`)으로 수정했습니다.

### Changed
- 릴리즈 워크플로에 `FORCE_JAVASCRIPT_ACTIONS_TO_NODE24=true`를 추가했습니다.
- 앱 버전을 `versionName 0.4.1`, `versionCode 3`으로 업데이트했습니다.

### Verification
- CI: `Release APK` 태그 빌드에서 APK 자산 업로드 확인

## v0.4.0 - 2026-04-30

### Added
- GitHub Pages 루트 진입점(`docs/index.html`)을 추가해 브랜딩 페이지 접근성을 개선했습니다.
- 태그 푸시(`v*.*.*`) 시 릴리즈 APK를 자동 빌드/업로드하는 GitHub Actions 워크플로(`release-apk.yml`)를 추가했습니다.

### Changed
- Pages 배포 범위를 `docs/branding`에서 `docs` 전체로 확장해 사이트 루트 URL에서 바로 페이지가 열리도록 변경했습니다.
- 앱 버전을 `versionName 0.4.0`, `versionCode 2`로 업데이트했습니다.

### Verification
- 로컬: Android SDK 경로 미설정으로 빌드 검증 제한
- CI: 푸시 후 `Android CI`, `Deploy GitHub Pages` 확인
- 릴리즈: 태그 `v0.4.0` 푸시 후 `Release APK` 워크플로와 GitHub Release 아티팩트 확인

### Build / CI
- CameraX 요구사항에 맞추기 위해 Android Gradle Plugin 버전을 `8.6.1`로 업데이트했습니다.

## v0.3.8 - 2026-04-30

### Added
- CameraX 기반 캡처 의존성(`camera-core`, `camera-camera2`, `camera-lifecycle`, `camera-view`)을 추가했습니다.

### Changed
- `Create Record`의 촬영 경로를 기존 `TakePicture` 계약 방식에서 CameraX 실시간 프리뷰 + 촬영 방식으로 전환했습니다.
- 캡처 후 결과 이미지를 기존 태그 초안 생성 및 저장 플로우에 그대로 연결했습니다.

### Verification
- 로컬: `./gradlew :app:compileDebugKotlin` 실행 시도
- 로컬 결과: Android SDK 경로 미설정으로 실패 (`ANDROID_HOME` 또는 `local.properties`의 `sdk.dir` 필요)
- CI: 커밋 푸시 후 GitHub Actions에서 검증

### Build / CI
- CI 호환성 문제를 피하기 위해 CameraX 버전을 `1.6.0`에서 `1.5.3`으로 조정했습니다.

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

## v0.2.1 - 2026-04-30

### Build / CI
- GitHub Actions에서 `./gradlew` 실행 권한 문제를 해결하기 위해 `chmod +x gradlew` 단계를 추가했습니다.

### Verification
- CI 실패 로그 확인: `Permission denied` 재현 확인 후 워크플로 수정

## v0.2.2 - 2026-04-30

### Build / CI
- `Theme.Material3.DayNight.NoActionBar` 리소스 링크 오류 해결을 위해 Material Components 의존성을 추가했습니다.

### Verification
- CI 실패 로그 확인 후 의존성 보강으로 수정

## v0.2.3 - 2026-04-30

### Fixed
- CI 빌드를 깨뜨리던 `roundIcon` 리소스 참조 오류를 수정했습니다.

### Verification
- CI 실패 로그 확인: `sym_def_app_icon_round` 리소스 없음

## v0.3.0 - 2026-04-30

### Added
- Room 기반 `PhotoRecord`/`PhotoTag` 영속 저장 계층(엔터티/DAO/DB/Repository)을 추가했습니다.
- 설정 화면에서 BYOK API Key 상태 확인, 저장, 삭제, Mock 연결 테스트를 추가했습니다.
- API Key를 기기 내부 암호화 저장소(`EncryptedSharedPreferences`)에 저장하도록 추가했습니다.

### Changed
- 기록 조회/검색을 인메모리에서 Room 기반으로 전환했습니다.
- 기록 목록에서 삭제 동작을 추가했습니다.

### Privacy
- 외부 AI 호출 없이 Mock 테스트만 수행하도록 유지했습니다.
- 설정 화면에 외부 분석 시 전송 고지 문구를 추가했습니다.

### Verification
- CI 기준 `assembleDebug` 검증 진행

## v0.3.1 - 2026-04-30

### Added
- 카메라 권한 및 `FileProvider` 기반 촬영 URI 저장 경로를 추가했습니다.
- `Create Record`에서 촬영/가져오기 이미지 미리보기를 추가했습니다.
- `Record Detail` 화면 및 목록에서 상세 진입을 추가했습니다.

### Changed
- 기록 생성 화면을 실제 촬영 플로우(`TakePicture`)와 연결했습니다.

### Verification
- CI 기준 `assembleDebug` 검증 진행

## v0.3.2 - 2026-04-30

### Added
- 인앱 `Privacy Notice` 화면을 추가했습니다.
- Record Detail에서 사용자 실행 기반 `Advanced Analysis (Mock)` 기능을 추가했습니다.
- 고급분석 실행 전 외부 전송 안내 다이얼로그를 추가했습니다.

### Changed
- 설정 화면에서 개인정보 고지 화면으로 이동할 수 있도록 변경했습니다.

### Verification
- CI 기준 `assembleDebug` 검증 진행

## v0.3.3 - 2026-04-30

### Changed
- 로컬 태그 생성을 `MockLocalImageTagger` 기본값에서 ML Kit 온디바이스 라벨링으로 전환했습니다.
- 라벨 confidence 임계값 적용 및 태그 정규화 사전을 추가했습니다.
- ML Kit 처리 실패 시 mock 태거로 폴백하도록 보강했습니다.

### Verification
- CI 기준 `assembleDebug` 검증 진행

## v0.3.4 - 2026-04-30

### Added
- `advanced_analysis` 영속 테이블/DAO를 추가했습니다.
- Record Detail에서 적용한 mock 고급분석 결과를 로컬 DB에 저장하도록 추가했습니다.

### Changed
- 고급분석 적용 시 추천 태그를 `AdvancedAi` 소스로 기록에 병합 저장하도록 변경했습니다.
- 레코드 `analysisStatus`를 `AdvancedComplete`로 갱신하도록 변경했습니다.
- 저장된 최신 분석 요약을 상세 화면에 표시하도록 변경했습니다.

### Verification
- CI 기준 `assembleDebug` 검증 진행

## v0.3.5 - 2026-04-30

### Added
- Gemini BYOK 실제 이미지 분석 provider를 추가했습니다.
- 앱에 `INTERNET` 권한을 추가했습니다.

### Changed
- 상세 화면 고급분석 실행 시 API Key가 있으면 Gemini 호출을 시도하고, 실패 시 mock 결과로 폴백하도록 변경했습니다.
- 분석 결과 저장 시 provider(`gemini`/`mock`)를 구분 저장하도록 변경했습니다.

### Verification
- CI 기준 `assembleDebug` 검증 진행

## v0.3.6 - 2026-04-30

### Build / CI
- GitHub Actions 검증 단계를 `lint`, `testDebugUnitTest`, `assembleDebug`로 확장했습니다.
- Actions Node 런타임 경고 완화를 위해 Node 24 강제 환경 변수를 추가했습니다.

### Fixed
- lint 오류 대응을 위해 매니페스트에 `android.hardware.camera`의 `required=false` feature 선언을 추가했습니다.

## v0.3.7 - 2026-04-30

### Added
- MarkScene 브랜드 아이콘(adaptive icon) 리소스를 추가했습니다.
- GitHub Pages 브랜딩 페이지(`docs/branding`)와 배포 워크플로를 추가했습니다.

### Changed
- 매니페스트 앱 아이콘을 기본 시스템 아이콘에서 프로젝트 아이콘(`@mipmap/ic_launcher`)으로 교체했습니다.
- 남은 작업 항목을 GitHub Issues(#1~#6)로 등록하고 `.agent/tasks.md`와 동기화했습니다.

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
