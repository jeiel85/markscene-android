# MarkScene

> 사진 속 장면을 태그, 메모, 기억 유형으로 정리해 나중에 바로 찾는 Android 비주얼 메모 앱입니다.

[GitHub Pages](https://jeiel85.github.io/markscene-android/) ·
[APK Releases](https://github.com/jeiel85/markscene-android/releases) ·
[Privacy Policy](https://jeiel85.github.io/markscene-android/privacy/) ·
[Play Store Checklist](docs/PLAY_STORE_CHECKLIST.md)

## 현재 버전

| 항목 | 값 |
| --- | --- |
| 앱 이름 | MarkScene |
| 최신 문서 기준 | v2.6.1 |
| Android Application ID | `com.markscene.app` |
| 루트 패키지 | `com.markscene.app` |
| 저장소 | <https://github.com/jeiel85/markscene-android> |
| 공개 페이지 | <https://jeiel85.github.io/markscene-android/> |
| 개인정보 처리방침 | <https://jeiel85.github.io/markscene-android/privacy/> |
| 라이선스 | [MIT License](LICENSE) |

## 제품 한 줄 설명

MarkScene은 사진을 검색 가능한 개인 기억으로 바꿉니다. 사진을 찍거나 선택하면 로컬 태그와 OCR 초안을 만들고, 사용자는 태그와 메모를 수정해 로컬에 저장한 뒤 나중에 검색과 Recall Box로 다시 찾을 수 있습니다.

기본 기능은 API Key 없이 동작합니다. v2.6.1부터 고급 AI 분석도 앱에서 다운로드한 로컬 VLM 모델이 있으면 외부 전송 없이 기기 안에서 먼저 실행하고, Gemini BYOK는 외부 분석이 필요할 때 선택적으로 사용합니다.

## 핵심 흐름

```text
사진 촬영 또는 선택
  -> 로컬 태그 초안 및 OCR
  -> 태그, 제목, 메모, 기억 유형 편집
  -> 로컬 저장
  -> Scene Timeline, 검색, Recall Box에서 다시 찾기
```

## 주요 기능

- **Today / Scene Timeline**: 날짜별로 정리된 사진 기록과 오늘의 요약을 확인합니다.
- **사진 촬영 및 가져오기**: CameraX 촬영과 Android Photo Picker 가져오기를 지원합니다.
- **로컬 태그와 OCR**: ML Kit 기반 온디바이스 태그/OCR 초안을 생성합니다.
- **수정 가능한 기억 유형**: 아이디어, 업무, 영수증, 장소, 나중에 보기 등 여러 기억 유형을 지정합니다.
- **빠른 검색**: 태그, 제목, 메모, OCR 텍스트, 기억 유형 기반으로 기록을 찾습니다.
- **Recall Box**: 나중에 다시 볼 기록을 별도 탭에 모읍니다.
- **로컬 VLM 고급 분석**: MediaPipe 호환 온디바이스 모델을 앱에서 다운로드하면 API Key 없이 장면 요약과 추천 태그를 생성합니다.
- **BYOK 고급 AI 분석**: 로컬 모델이 없거나 외부 분석을 선택할 때 사용자가 저장한 Gemini API Key로 고급 분석을 실행합니다.
- **보안 옵션**: API Key 암호화 저장, EXIF 제거, 갤러리 숨김, 자동 잠금, 스크린샷 차단 옵션을 제공합니다.
- **공유와 내보내기**: 소셜 공유 템플릿, 이미지 크롭, Markdown/CSV 내보내기를 지원합니다.

## 공개 자료

| 자료 | 위치 |
| --- | --- |
| GitHub Pages 랜딩 | <https://jeiel85.github.io/markscene-android/> |
| 개인정보 처리방침 | <https://jeiel85.github.io/markscene-android/privacy/> |
| Play Store 등록 문구 | [docs/STORE_LISTING_KO.md](docs/STORE_LISTING_KO.md) |
| 앱 아이콘 512x512 | [store-assets/MarkScene_icon_512.png](store-assets/MarkScene_icon_512.png) |
| Feature graphic 1024x500 | [store-assets/MarkScene_feature_graphic_1024x500.png](store-assets/MarkScene_feature_graphic_1024x500.png) |
| 스크린샷 | [store-assets](store-assets) |
| 릴리즈 체크리스트 | [docs/RELEASE_CHECKLIST.md](docs/RELEASE_CHECKLIST.md) |
| Play Store 체크리스트 | [docs/PLAY_STORE_CHECKLIST.md](docs/PLAY_STORE_CHECKLIST.md) |

## 개인정보 및 권한 원칙

- 기본 플로우에서 사용자 사진을 외부 서버로 업로드하지 않습니다.
- Android Photo Picker를 우선 사용하고, 전체 갤러리 스캔을 하지 않습니다.
- `MANAGE_EXTERNAL_STORAGE`, 광범위한 사진/동영상 권한, 백그라운드 위치 권한을 사용하지 않습니다.
- 로컬 VLM 모델을 가져온 경우 고급 분석도 기기 안에서 처리되며 API Key가 필요하지 않습니다.
- API Key는 외부 AI 분석을 위한 선택 기능이며 기기 내부 암호화 저장소에 보관합니다.
- API Key, 이미지 바이트, 프롬프트, AI 응답, 민감 정보는 로그에 남기지 않습니다.
- AI와 로컬 태그 결과는 확정 사실이 아니라 수정 가능한 제안으로 표시합니다.

현재 매니페스트의 주요 권한:

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.INTERNET" />
```

`INTERNET` 권한은 사용자가 BYOK 고급 AI 분석을 실행할 때 필요합니다.

## 기술 스택

| 분야 | 기술 |
| --- | --- |
| 언어 | Kotlin |
| UI | Jetpack Compose, Material 3 |
| 카메라 / 사진 | CameraX, Android Photo Picker |
| 로컬 저장 | Room, DataStore |
| 비동기 | Coroutines / Flow |
| 이미지 | Coil, 앱 내부 이미지 최적화 |
| 로컬 AI | ML Kit Image Labeling, ML Kit Text Recognition, MediaPipe LLM Inference |
| 선택 AI | Gemini BYOK |
| 보안 | Android Keystore, EncryptedSharedPreferences |
| CI/CD | GitHub Actions, GitHub Pages |

## 빌드와 검증

```bash
# 단위 테스트
./gradlew :app:testDebugUnitTest

# 린트
./gradlew :app:lintDebug

# 디버그 APK
./gradlew :app:assembleDebug
```

GitHub Actions가 최종 Android CI와 GitHub Pages 배포 검증을 담당합니다.

## 문서

- [AGENTS.md](AGENTS.md): 에이전트 작업 규칙
- [docs/PRODUCT_BRIEF.md](docs/PRODUCT_BRIEF.md): 제품 브리프
- [docs/PRD.md](docs/PRD.md): 제품 요구사항
- [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md): 아키텍처
- [docs/PRIVACY_AND_SECURITY.md](docs/PRIVACY_AND_SECURITY.md): 개인정보 및 보안 원칙
- [docs/AI_PROVIDER_STRATEGY.md](docs/AI_PROVIDER_STRATEGY.md): AI 제공자 전략
- [docs/LOCAL_TAGGING.md](docs/LOCAL_TAGGING.md): 로컬 태깅 전략
- [docs/UX_FLOW.md](docs/UX_FLOW.md): UX 흐름
- [docs/DESIGN_SYSTEM.md](docs/DESIGN_SYSTEM.md): 디자인 시스템
- [docs/DATA_MODEL.md](docs/DATA_MODEL.md): 데이터 모델
- [docs/ROADMAP.md](docs/ROADMAP.md): 로드맵

## 에이전트 작업 방식

이 저장소의 단일 진입 규칙은 [AGENTS.md](AGENTS.md)입니다. 작업 전 최신 `main`을 기준으로 상태를 확인하고, 문서와 작업 이력을 읽은 뒤 작은 범위로 구현, 검증, 기록, 커밋, 푸시까지 진행합니다.
