# CHANGELOG.md

이 문서는 MarkScene의 사용자에게 공개 가능한 변경 사항을 기록합니다.

## Unreleased

### Added
- 로컬 고급 AI 모델 다운로드가 기본 모델(Google Gemma 3n E2B INT4 LiteRT-LM, 약 3.66GB)로 즉시 동작합니다. 별도 빌드 설정 주입 없이도 다운로드 버튼이 활성화됩니다.
- 설정에서 HuggingFace read 토큰을 안전하게 저장/삭제할 수 있습니다. 토큰은 EncryptedSharedPreferences를 통해 기기 내부에만 암호화 저장되며, 모델 다운로드 시 Authorization 헤더로 전송됩니다.
- 모델 다운로드 진행률(다운로드된 바이트 / 전체 크기, 퍼센트, 진행 막대)이 설정 화면에 표시됩니다.
- 라이선스 게이트가 있는 모델의 경우 HuggingFace 모델 페이지를 바로 여는 버튼이 제공됩니다.

### Changed
- 고급 AI 분석이 로컬 VLM 전용으로 전환되었습니다. 로컬 모델이 없을 때 Gemini/BYOK로 fallback하지 않고, 설정에서 로컬 모델 다운로드를 안내합니다.
- 비주얼 Q&A도 Gemini 대신 다운로드된 로컬 VLM 모델만 사용하도록 변경되었습니다.
- 개인정보 안내, GitHub Pages, README, Play Store 문구를 외부 AI 전송 없음 기준으로 정리했습니다.
- 로컬 AI 모델 매니저가 청크 단위 스트리밍 다운로드, 디스크 여유 공간 사전 검증, HTTP 401/403/404 등에 대한 명확한 한국어 오류 메시지, 코루틴 취소 지원, 10분 단위로 늘어난 읽기 타임아웃을 지원합니다.
- 로컬 VLM 추론 인스턴스를 lazy singleton으로 유지해, 매 분석 호출마다 수 GB 모델을 다시 로드하지 않습니다. 모델 삭제 또는 재다운로드 시 자동으로 release 됩니다.
- 로컬 VLM JSON 파싱 실패 시 사용자에게 의미 있는 fallback 요약을 표시합니다.

### Removed
- Gemini 고급 분석 provider, Gemini API Key 입력/저장/삭제/연결 테스트 UI, 외부 AI 분석 fallback을 제거했습니다.

### Security
- 기존에 저장되어 있던 Gemini API Key 값은 앱 시작 시 보안 저장소에서 제거합니다.

### Build / CI
- `MARKSCENE_LOCAL_VLM_MODEL_FILENAME`, `MARKSCENE_LOCAL_VLM_MODEL_SIZE_MB`, `MARKSCENE_LOCAL_VLM_MODEL_LICENSE_URL` 빌드 설정으로 다른 MediaPipe 호환 모델로 교체할 수 있습니다.

### Verification
- 로컬: `./gradlew.bat :app:compileDebugKotlin --no-daemon`, `./gradlew.bat :app:testDebugUnitTest --no-daemon`, `./gradlew.bat :app:lintDebug --no-daemon`, `./gradlew.bat :app:assembleDebug --no-daemon` 성공.
- 로컬: 생성된 `BuildConfig.java`에 `LOCAL_VLM_MODEL_URL`이 Gemma 3n E2B LiteRT-LM URL로 박혀 있는지 직접 확인.
- 미실시: 실제 기기에서 모델 다운로드 → 추론/Q&A end-to-end 검증은 충분한 RAM과 라이선스 수락된 사용자 토큰이 필요해 보류.

## v2.6.1 - 2026-05-26

### Changed
- 로컬 고급 AI 모델 설정 버튼이 파일 선택기를 열지 않고, 앱에 설정된 HTTPS 모델 URL에서 자동 다운로드하도록 변경되었습니다.
- 로컬 VLM 안내 문구를 모델 파일 가져오기에서 모델 다운로드 기준으로 정리했습니다.

### Build / CI
- Play Store 업로드용 `versionCode` 중복을 피하기 위해 앱 버전을 `2.6.1` / `261`로 올렸습니다.
- `MARKSCENE_LOCAL_VLM_MODEL_URL`, `MARKSCENE_LOCAL_VLM_MODEL_NAME` 빌드 설정으로 승인된 로컬 모델 다운로드 소스를 주입할 수 있습니다.

### Verification
- 로컬: `./gradlew.bat :app:compileDebugKotlin --no-daemon`, `./gradlew.bat testDebugUnitTest --no-daemon`, `./gradlew.bat :app:lintDebug --no-daemon`, `git diff --check` 성공.
- 로컬: `./gradlew.bat bundleRelease --no-daemon`은 10분 제한 초과로 완료 확인하지 못했습니다. Play Store 업로드용 최종 AAB는 CI 산출물에서 확인합니다.

## v2.6.0 - 2026-05-26

### Added
- 설정에서 MediaPipe 호환 로컬 VLM 모델 파일을 앱 내부 저장소에 보관할 수 있습니다.
- 기록 상세 화면의 고급 AI 분석이 로컬 모델을 우선 사용해 사진을 외부 서버로 보내지 않고 장면 요약과 추천 태그를 생성합니다.
- 로컬 VLM 태그 소스를 추가해 외부 AI 태그와 온디바이스 고급 분석 태그를 구분합니다.

### Changed
- 고급 AI 분석 안내 문구를 로컬 VLM 분석과 외부 BYOK Gemini 분석으로 구분했습니다.
- 개인정보 안내 화면과 대시보드가 로컬 고급 AI 모델의 데이터 흐름을 설명하도록 갱신되었습니다.
- Android CI의 에뮬레이터 smoke test를 instrumentation APK 설치 방식에서 debug APK 직접 설치/실행 방식으로 바꿔 MediaPipe 네이티브 라이브러리 추가 후에도 CI 검증이 안정적으로 동작하도록 했습니다.

### Build / CI
- 앱 버전을 `2.6.0` / `260`으로 올렸습니다.
- Release R8 빌드에서 MediaPipe/Protobuf 런타임 클래스가 보존되도록 ProGuard 규칙을 보강했습니다.

### Documentation
- AI Provider Strategy, Local Tagging, Privacy/Security, Architecture, PRD, Data Model 문서에 로컬 VLM 우선 전략과 제약을 반영했습니다.
- GitHub Pages 랜딩 페이지를 v2.6.0 기준으로 갱신해 Scene Timeline, Recall Box, 보안 옵션, 공유/내보내기, 로컬 VLM 기능이 드러나도록 정리했습니다.
- README를 공개 페이지, 릴리즈, 개인정보 처리방침, Play Store 준비 자료 중심으로 재구성했습니다.
- 공개 개인정보 처리방침 URL을 `https://jeiel85.github.io/markscene-android/privacy/`로 연결할 수 있도록 Pages용 페이지를 추가했습니다.
- Play Store 등록 문구를 v2.6.0 기준으로 정리했습니다.

### Verification
- 로컬: `./gradlew.bat assembleDebug` 성공.
- 로컬: `./gradlew.bat testDebugUnitTest`, `./gradlew.bat lintDebug`, `git diff --check` 성공.
- 로컬: `./gradlew.bat assembleRelease` 성공.
- 로컬: `git diff --check`, 정적 링크/이미지 참조 검증, Pages 로컬 HTTP 브라우저 미리보기를 완료했습니다.
- CI: Android CI와 Pages build 성공.

## v2.5.0 - 2026-05-21

### Added
- **백그라운드 자동 잠금 (Auto-Lock)**: 생체 인식 잠금이 활성화된 상태에서 앱이 백그라운드로 전환되면 재진입 시 자동으로 잠금 화면이 표시됩니다. Settings > 보안에서 활성화 가능.
- **EXIF 메타데이터 제거 옵션**: 내보내기 시 사진의 위치정보(GPS), 카메라 기종 등 민감한 EXIF 데이터를 자동으로 삭제하는 토글을 Settings > 보안에 추가했습니다. 기본값 ON.
- **갤러리 숨김 모드**: MarkScene의 사진 저장 폴더에 `.nomedia` 파일을 생성하여 시스템 기본 갤러리 앱에서 MarkScene 사진이 노출되지 않도록 합니다. Settings > 보안에서 제어.
- **소셜 공유 (Polaroid 템플릿)**: 기록 상세 화면에서 공유 버튼을 통해 Polaroid 스타일 템플릿으로 사진과 제목을 다른 앱에 공유할 수 있습니다.
- **AI 분석 프롬프트 템플릿**: 고급 분석 실행 전에 "영수증 분석", "레시피 추출", "상세 설명" 등 자주 쓰는 프롬프트를 칩으로 선택할 수 있습니다.
- **이미지 크롭 저장**: 상세 화면에서 Crop 버튼으로 사진의 중앙을 정사각형으로 크롭하여 새 기록으로 저장할 수 있습니다.
- **갤러리 레이아웃 설정 저장**: 검색 화면에서 선택한 그리드(2열/3열/목록) 레이아웃이 앱 재시작 후에도 유지됩니다.
- **In-App Review**: Google Play 인앱 리뷰 다이얼로그를 앱 사용 5회 이상, 기록 10개 이상 시 자연스럽게 표시합니다.

### Changed
- **배지/업적 시스템 강화**: "탐험가(10종+ 태그)", "AI 탐험가", "오프라인 마스터" 배지가 추가되었습니다.
- **주간 회고 강화**: Settings 화면의 주간 회고에 가장 인기 있는 태그 TOP 3가 표시됩니다.

### Security
- **API Key 저장소 강화**: `allowBackup=false`로 설정하여 Android 자동 백업을 통한 API Key 유출을 방지합니다.
- **Gemini API 타임아웃**: 네트워크 연결/읽기/쓰기 타임아웃을 30초로 설정하여 무한 대기 상태를 방지합니다.

### Build / CI
- Release 빌드에서 R8 코드 축소 및 리소스 축소가 활성화됩니다 (v2.4.0에서 적용).

### Verification
- 로컬: `./gradlew :app:compileDebugKotlin` 성공.
- CI: 푸시 후 `Android CI` / `Release APK` 결과 확인 예정.

## v2.4.0 - 2026-05-20

### Performance
- **이미지 최적화 파이프라인 개선**: 메모리 효율적인 비트맵 디코딩(inJustDecodeBounds)과 적응형 다운샘플링으로 OOM(Out of Memory) 위험을 대폭 낮췄습니다. 이미지 저장 시 RGB_565 컬러 포맷을 사용해 메모리 사용량을 절반으로 줄입니다.
- **Coil 이미지 로더 최적화**: 맞춤형 ImageLoader를 전역 적용하여 메모리 캐시(15%), RGB_565 비트맵, 하드웨어 비트맵으로 이미지 로딩 성능과 메모리 안정성을 개선했습니다.
- **검색 성능 최적화 (FTS 인덱스)**: Room FTS4(Full Text Search) 가상 테이블을 도입하여 레코드 수가 늘어나도 빠른 검색이 가능합니다. 기존 LIKE+JOIN 쿼리와 함께 동작하며 점진적 전환을 지원합니다.
- **리스트 스크롤 성능 개선**: Compose Strong Skipping 모드 활성화 및 `@Immutable` 어노테이션을 적용하여 LazyColumn/StaggeredGrid의 불필요한 리컴포지션을 최소화했습니다.
- **Baseline Profile 적용**: `profileinstaller`를 통해 앱 시작 속도를 개선하는 AOT 컴파일 힌트를 제공합니다. 주요 시작 경로(MainActivity, MarkSceneApp, TodayScreen)가 프로파일링됩니다.
- **저장공간 자동 관리**: 앱 시작 시 7일 이상 된 임시 파일과 과도한 이미지 캐시(300MB 초과)를 자동 정리합니다.

### Changed
- **테마 색상 완성도 향상**: Material 3의 모든 색상 토큰(primary, secondary, tertiary, error 및 각각의 container/on 컬러)을 완전하게 정의했습니다. 다크 모드와 True Black 모드에서도 색상 일관성이 개선되었습니다.
- **검색 자동완성 개선**: "최근 검색 지우기" 버튼이 실제로 동작하도록 연결했습니다.

### Build / CI
- **APK 용량 다이어트**: Release 빌드에 R8 코드 축소(`isMinifyEnabled`)와 리소스 축소(`isShrinkResources`)를 활성화했습니다. ProGuard 규칙을 Kotlin Serialization, Room, Coil, ML Kit, CameraX 등에 맞게 최적화했습니다.
- Release 빌드에서 디버그 로그(`Log.d`, `Log.v`, `Log.i`)가 자동 제거됩니다.

### Verification
- 로컬: `./gradlew :app:compileDebugKotlin`, `./gradlew :app:testDebugUnitTest`, `./gradlew :app:lintDebug` 실행 예정.
- CI: 푸시 후 `Android CI` / `Release APK` 결과 확인 예정.

## v2.3.0 - 2026-05-20

### Added
- **로컬 처리 인증 배지 (Today 화면 상단)**: 'Today' 화면의 상단 액션 영역에 방패 아이콘과 함께 "로컬 처리" 배지가 표시됩니다. 모든 자동 태깅과 OCR이 이 기기에서만 처리되고 있음을 한눈에 확인할 수 있습니다.
- **카메라 권한 사전 안내 자동 표시**: 촬영 화면 진입 시 카메라 권한이 아직 없으면, 시스템 권한 팝업이 뜨기 전에 권한 사용 이유를 먼저 보여줍니다. 다국어 리소스로 정리해 향후 번역이 쉽도록 했습니다.

### Security
- **API Key 입력 필드 보안 강화**: Settings의 Gemini API Key 입력 필드를 비밀번호 형식(`PasswordVisualTransformation`)으로 마스킹하고, 키보드 자동완성/자동수정/추천을 비활성화했습니다. 표시/숨김 토글을 추가해 사용자가 필요할 때만 키를 확인할 수 있습니다. 어깨너머 노출과 입력기/클립보드 스누핑 위험을 낮춥니다.

### Verification
- 로컬: `./gradlew :app:compileDebugKotlin`, `./gradlew :app:testDebugUnitTest` 성공.
- CI: 푸시 후 `Android CI` / `Release APK` 결과 확인 예정.

## v2.2.0 - 2026-05-20

### Added
- **개인정보 대시보드 (Settings → 개인정보 대시보드)**: 저장된 기록 수, 생성된 태그 수, 생체 인식 잠금 / BYOK API Key 활성화 여부를 한 화면에서 확인할 수 있습니다. 모든 태깅과 OCR이 로컬에서만 처리됨을 명시적으로 알려줍니다.
- **앱 전체 스크린샷 차단 토글 (Settings → 보안 및 개인정보 → 앱 전체 스크린샷 차단)**: 켜면 앱의 모든 화면에 시스템 레벨 `FLAG_SECURE`가 적용되어 화면 캡처와 화면 녹화, 최근 앱 미리보기가 차단됩니다. 기본값은 꺼짐이며 사용자가 직접 켤 수 있습니다.

### Changed
- **목록에서 좌/우 스와이프로 삭제**: 검색/목록 화면(2열·3열 그리드)에서 카드를 좌측 또는 우측으로 스와이프하면 삭제 확인 다이얼로그가 나타나도록 동작을 실제로 연결했습니다. 기존에는 컴포넌트만 존재하고 스와이프 제스처가 동작하지 않았습니다.

### Security
- 사용자가 명시적으로 켜는 경우, 앱 루트(`MarkSceneApp`)에서 `SecureScreenEffect`를 활성화해 모든 화면이 FLAG_SECURE 상태로 표시되도록 했습니다.

### Verification
- 로컬: `./gradlew :app:compileDebugKotlin`, `./gradlew :app:testDebugUnitTest` 성공.
- CI: 푸시 후 `Android CI` / `Release APK` 결과 확인 예정.

## v2.1.0 - 2026-05-16

### Added
- **Scene Memory 확장 (전체 Phase)**: MarkScene이 기존 비주얼 메모 앱에서 장면 기반 개인 기억 저장소로 확장되었습니다.
  - **Phase 1 - Scene Timeline**: 기존 홈 화면을 '오늘의 장면' 타임라인으로 교체. 기록이 날짜별로 그룹화되어 표시됩니다.
  - **Phase 2 - Memory Context 데이터 모델**: MemoryType, MoodType, ContextType enum 및 MemoryContextEntity, RecordMemoryTypeCrossRef 테이블 추가. Room DB v9 마이그레이션.
  - **Phase 3 - Memory Tags**: 기록 생성 시 Memory Type(아이디어, 업무, 가족, 영수증 등 14종)을 선택할 수 있습니다. 상세 화면에서 확인 가능.
  - **Phase 4 - Recall Box**: '다시 보기' 하단 탭 추가. 다시 볼 가치가 있는 기록을 별도 관리. 저장 시 Recall 토글 제공.
  - **Phase 5 - Daily Scene Recap**: 오늘 화면에 일일 요약 카드(기록 수, 많이 나온 태그) 추가.
  - **Phase 6 - Search 확장**: 검색 화면에 필터 칩(오늘, 이번 주, 아이디어, 영수증 등) 추가.
  - **Phase 7 - BYOK AI Scene Memory**: 고급 AI 분석 결과에 Memory Type, Recall 추천이 포함됩니다.
- **Bottom Navigation**: 하단 탐색 바(오늘, 검색, 다시 보기, 설정)가 추가되었습니다.
- **SceneCard 컴포넌트**: 썸네일, 시간, 제목/메모, 상위 태그가 표시되는 장면 카드 컴포넌트가 추가되었습니다.
- **DailyRecapCard 컴포넌트**: 오늘의 장면 요약 카드가 추가되었습니다.

### Changed
- 홈 화면이 TodayScreen으로 교체되었습니다.
- FAB(촬영/가져오기)가 TodayScreen에 배치되었습니다.
- 검색, 설정, 다시 보기가 하단 탭으로 이동했습니다.
- 기록 저장 후 검색 화면 대신 Today 화면으로 이동하도록 변경되었습니다.
- Room DB가 v8에서 v9으로 마이그레이션 (memory_contexts, record_memory_types 테이블 추가)

### Documentation
- `docs/renew/` 폴더에 Scene Memory 확장 설계 문서 세트를 추가했습니다.

### External Sharing
- 갤러리, 브라우저 등 다른 앱에서 '공유하기'를 선택하면 MarkScene이 표시되어 즉시 기록 생성 화면으로 이동할 수 있습니다. (GitHub Issue #10)

### Verification
- 로컬: `./gradlew :app:assembleDebug :app:testDebugUnitTest :app:bundleRelease` 성공.
- AAB: `app-release.aab` 생성됨, 서명 적용됨 (릴리즈 키스토어).
- CI: 푸시 후 `Android CI` / `Release APK` 결과 확인.

## v2.0.9 - 2026-05-09

### Fixed
- 일부 단말(예: 갤럭시 S24)에서 첫 실행 시 온보딩 화면이 표시된 직후 앱이 즉시 종료되던 현상을 수정했습니다. ML Kit 한국어 OCR 클라이언트 초기화를 첫 사용 시점까지 지연하고, Room 데이터 구독과 초기 인증/네비게이션 단계의 비동기 예외가 액티비티를 죽이지 않도록 안전망을 추가했습니다.

### Verification
- 로컬: `./gradlew :app:compileDebugKotlin :app:testDebugUnitTest :app:lintDebug :app:assembleDebug` 성공.
- 기기: Lenovo TB320FC(Android 15) 회귀 확인 — `pm clear` 후 첫 실행 시 온보딩 정상 표시 → "걸너뛰기"/"다음" 통과 → 홈 화면 진입 확인.
- 기기: 갤럭시 S24 — 사용자 단말에서 디버그 빌드 기준 온보딩 통과 정상 동작 확인.
- CI: 푸시 후 `Android CI` / `Release APK` 결과 모니터링.

## v2.0.8 - 2026-05-06

### Build / CI
- 앱 버전을 `v2.0.8` (versionCode 208)으로 업데이트했습니다.
- `v2.0.8` 태그 푸시로 릴리즈 배포 워크플로우를 실행하고 결과를 모니터링합니다.

## v2.0.7 - 2026-05-06

### Build / CI
- 앱 버전을 `v2.0.7` (versionCode 207)으로 업데이트했습니다.
- 버전업 커밋 기준으로 GitHub Actions `Android CI` 빌드 성공 여부를 모니터링합니다.

## v2.0.6 - 2026-05-06

### Build / CI
- 앱 버전을 `v2.0.6` (versionCode 206)으로 업데이트했습니다.
- GitHub Actions CI 파이프라인의 에뮬레이터 연결 이슈를 분석하고, 전체 자동화 단계(Lint, Unit Test, Build, UI Test)가 정상적으로 성공함을 재검증했습니다.

## v2.0.5 - 2026-05-05

### Build / CI
- 앱 버전을 `v2.0.5` (versionCode 205)으로 업데이트했습니다.
- 최신 `main` 기준 GitHub Actions `Android CI` 성공 상태를 다시 확인할 수 있도록 버전 기준을 정리했습니다.

## v2.0.4 - 2026-05-02

### Fixed
- **실행 즉시 종료 방지**: 일부 기기에서 Android Keystore 또는 암호화 설정 저장소 초기화가 실패해도 앱의 기본 화면이 열리도록 복구했습니다.
- **설정 저장 실패 안내**: API Key와 생체 잠금 설정을 저장할 수 없는 환경에서는 앱이 종료되지 않고 사용자에게 실패 상태를 표시합니다.

### Verification
- 로컬 `./gradlew test`, `./gradlew lint`, `./gradlew assembleDebug` 통과.
- GitHub Release `v2.0.3` APK를 실제 기기에 새 설치 및 `v1.9.2 -> v2.0.3` 업데이트 설치해 시작 crash가 없음을 확인했습니다.
- 로컬 `:app:assembleRelease`는 릴리즈 서명 키가 없어 실패했으며, 서명 릴리즈 빌드는 GitHub Actions에서 검증합니다.

## v2.0.3 - 2026-05-02 🛡️

### Fixed
- **릴리즈 서명 검증 보강**: GitHub Actions 릴리즈 빌드에서 누락된 signing secret을 더 일찍 감지하고, 실제 경로 해석 방식에 맞는 설정 가이드를 정리했습니다.
- **앱 실행 안정성 강화**: `FragmentActivity` 캐스팅 오류 및 암호화 저장소 초기화 예외를 방지하기 위한 안전장치를 추가했습니다.
- **빌드 환경 최적화**: 로컬 개발 환경에서 SDK 및 키스토어 설정 없이도 빌드와 설치가 가능하도록 개선했습니다.
- **중앙 집중식 버전 관리**: `libs.versions.toml`을 통해 앱 버전(Name: 2.0.3, Code: 203)을 통합 관리하도록 구조를 개선했습니다.
- **실제 기기 배포 검증**: 무선 ADB 환경을 포함한 실제 Android 기기에서의 설치 및 실행 무결성을 확인했습니다.

## v2.0.2 - 2026-05-02 🛠️

### Fixed
- **앱 실행 안정화**: 데이터베이스 마이그레이션 실패 시 발생하던 런타임 크래시를 방지하기 위한 안전장치를 강화했습니다.
- **버전 정보 정규화**: 앱 내부 버전과 배포 태그 간의 불일치 문제를 완전히 해결했습니다.

## v2.0.1 - 2026-05-02

### Fixed
- **데이터 모델 무결성**: `AdvancedAnalysis` 모델의 직렬화 누락 오류를 수정했습니다.
- **DB 스키마 동기화**: 마이그레이션 로직과 실제 DB 정의 버전(v6)을 일치시켰습니다.

## v2.0.0 - 2026-05-02 🚀🎉

### Major Milestones (The "Intelligence & Productivity" Update)
- **비주얼 Q&A 정식 도입**: 상세 화면에서 AI(Gemini)와 채팅하며 사진 속 정보를 추출하는 혁신적인 기능을 완성했습니다.
- **공간 진화 관리**: 특정 공간의 시계열 변화를 확인하는 타임라인과 전후 사진 비교 기능을 통합했습니다.
- **개인화 지능**: 사용자의 태그 수정 습관을 학습하여 시간이 갈수록 정확해지는 태깅 엔진을 구축했습니다.
- **최고의 생산성**: 여러 기록을 한 번에 관리하는 다중 선택 및 일괄 편집(삭제/이동) 기능을 도입했습니다.
- **데이터 개방성**: Markdown 및 CSV 개별 추출 기능을 통해 외부 도구와의 연동성을 극대화했습니다.

### Fixed & Polished
- **빌드 안정성 극대화**: 이미지 최적화 및 비동기 라이브러리 간의 모든 충돌을 해결하고 무결한 빌드 환경을 조성했습니다.
- **감성적 UX**: 전역적인 햅틱 피드백 적용과 접근성 최적화, 그리고 완전한 다국어 리소스화를 완료했습니다.

### Build / CI
- 앱 버전을 상징적인 메이저 버전 `v2.0.0` (versionCode 200)으로 업데이트했습니다.
- 모든 자동화 파이프라인의 성공을 보장하기 위한 최종 정비를 마쳤습니다.

## v1.8.0 - 2026-05-02 💬

### Added (Visual Intelligence)
- **비주얼 Q&A (사진과 대화하기)**: 저장된 사진의 내용을 바탕으로 AI(Gemini)와 실시간으로 대화하며 정보를 추출할 수 있는 기능을 추가했습니다.
- **채팅 인터페이스**: 상세 화면에서 사진 하단에 나타나는 직관적인 채팅 UI를 통해 질문을 던지고 답변을 받을 수 있습니다.
- **대화 내역 로컬 저장**: AI와 나눈 대화들은 기기 내 DB에 안전하게 보관되어 언제든 다시 확인할 수 있습니다.

### Changed
- **데이터 엔진 확장**: 채팅 메시지 저장을 위한 `chat_messages` 테이블 및 관련 DAO를 추가(v5 -> v6 마이그레이션)했습니다.
- **Gemini 엔진 고도화**: 단순 분석을 넘어 이미지와 텍스트를 결합한 멀티모달 질의응답이 가능하도록 기능을 확장했습니다.

### Build / CI
- 앱 버전을 `v1.8.0` (versionCode 180)으로 업데이트했습니다.

## v1.7.0 - 2026-05-01 🚀

### Added (Productivity Overhaul)
- **일괄 편집 및 다중 선택**: 목록 화면에서 여러 기록을 선택하여 한 번에 삭제하거나 다른 공간으로 이동할 수 있는 기능을 추가했습니다.
- **하단 액션 바**: 다중 선택 시 나타나는 전용 UI를 통해 직관적인 일괄 관리 경험을 제공합니다.
- **전체 선택 기능**: 필터링된 현재 목록의 모든 항목을 한 번에 선택할 수 있는 기능을 지원합니다.

### Changed (Global UX Polish)
- **전역 리소스화**: 앱 내의 모든 하드코딩된 한국어 문구를 `strings.xml` 리소스로 추출하여 다국어 대응 기반을 완성했습니다.
- **접근성 최적화**: 모든 시각적 요소에 정교한 `contentDescription`을 부여하여 TalkBack 사용성을 극대화했습니다.
- **햅틱 피드백 강화**: 주요 버튼 클릭 및 상태 변화 시 미세한 진동을 추가하여 조작감을 개선했습니다.

### Build / CI
- 앱 버전을 `v1.7.0` (versionCode 170)으로 업데이트했습니다.

## v1.6.0 - 2026-05-01 ✨

### Added (Issue #7)
- **로컬 OCR 도입**: 사진 속 텍스트를 기기 내에서 자동으로 인식하는 기능을 추가했습니다 (ML Kit Text Recognition v2).
- **텍스트 기반 검색 강화**: 추출된 텍스트(`ocrText`)를 검색 인덱스에 포함하여 영수증, 명함 등의 내용으로도 검색이 가능해졌습니다.
- **한국어 인식 지원**: 한국어와 영어를 모두 지원하는 전용 OCR 엔진을 통합했습니다.

### Changed
- **데이터베이스 마이그레이션**: `ocrText` 필드 추가에 따른 스키마 업데이트 (v2 -> v3) 및 마이그레이션 로직을 구현했습니다.
- **분석 파이프라인 고도화**: 기록 생성 시 태그 추출과 OCR이 백그라운드에서 병렬로 수행되도록 개선했습니다.

### Build / CI
- 앱 버전을 `v1.6.0` (versionCode 160)으로 업데이트했습니다.
- ML Kit Text Recognition (Korean) 의존성을 추가했습니다.

## v1.5.0 - 2026-05-01 📄

### Added (Data Portability)
- **다양한 포맷 내보내기**: 모든 기록을 Markdown(.md) 및 CSV(.csv) 포맷으로 추출할 수 있는 기능을 추가했습니다.
- **문서화 지원**: Markdown 추출 기능을 통해 Obsidian, Notion 등 외부 문서 도구에서 시각적 메모를 손쉽게 통합하여 관리할 수 있습니다.
- **데이터 분석 활용**: CSV 추출 기능을 통해 엑셀이나 스프레드시트에서 기록 데이터를 구조적으로 분석할 수 있습니다.

### Changed
- **내보내기 엔진 통합**: `DataExporter`를 도입하여 복잡한 기록 데이터를 표준 문서 포맷으로 변환하는 파이프라인을 구축했습니다.
- **설정 화면 UI 보강**: 데이터 및 백업 섹션에 'Markdown 추출' 및 'CSV 추출' 항목을 시각적으로 배치했습니다.

### Build / CI
- 앱 버전을 `v1.5.0` (versionCode 150)으로 업데이트했습니다.

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
