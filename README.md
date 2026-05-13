# MarkScene

> 사진을 **검색 가능한 비주얼 메모**로 바꾸는 Android 앱

기본 기능은 API Key 없이 로컬 우선으로 동작합니다. 사용자는 사진을 찍거나 선택한 뒤, 앱이 생성한 빠른 태그 초안을 수정하고 저장할 수 있습니다. 고급 AI 분석은 사용자가 직접 API Key를 입력하는 BYOK(Bring Your Own Key) 방식의 선택 기능으로 제공합니다.

## 📋 프로젝트 식별자

| 항목 | 값 |
|------|-----|
| 앱 이름 | MarkScene |
| 저장소 | [markscene-android](https://github.com/jeiel85/markscene-android) |
| Application ID | `com.markscene.app` |
| 루트 패키지 | `com.markscene.app` |
| 주요 명세 | [docs/PRD.md](docs/PRD.md) |
| 라이선스 | [MIT License](LICENSE) |

## 🎯 핵심 컨셉

> 사진을 찍으면 주요 물건과 장면이 태그로 정리되고, 나중에 쉽게 찾을 수 있는 비주얼 메모 앱

## 🚀 MVP 핵심 플로우

```
사진 촬영 또는 선택
  ↓
즉시 이미지 미리보기
  ↓
로컬 태그 초안 생성
  ↓
태그 칩 수정/삭제/추가
  ↓
로컬 기록 저장
  ↓
태그, 제목, 메모로 검색
```

## ✨ 주요 기능

### 📸 사진 촬영 & 가져오기
- Android Photo Picker로 안전하게 사진 선택
- CameraX 기반 실시간 프리뷰 촬영

### 🏷 로컬 태깅
- ML Kit 온디바이스 라벨링으로 자동 태그 생성
- 생성된 태그를 수정, 삭제, 추가 가능

### ✏️ 메모 & 편집
- 사진별 제목과 메모 작성
- 저장된 기록 언제든 수정 가능

### 🔍 빠른 검색
- 태그, 제목, 메모 기반 즉시 검색
- 로컬 DB(Room) 기반 고속 검색

### 🤖 BYOK 고급 AI 분석
- 본인의 Gemini API Key 입력
- 선택한 사진에 대한 상세 AI 분석
- Android Keystore 암호화 저장

## 🔒 제품 원칙

| 원칙 | 설명 |
|------|------|
| ⚡ 빠른 체감 속도 | 로컬 처리 우선, 지연 최소화 |
| 🎨 Material 3 | 최신 Material 3 기반의 깔끔한 UI |
| 🔓 API Key 불필요 | 기본 사용 경험은 API Key 없이 완벽 |
| 💾 로컬 저장 | 사진, 태그, 메모는 기본적으로 기기 내부에 |
| 🤖 사용자 주도 AI | 고급 AI 분석은 사용자가 명시적으로 실행 |
| ✏️ 수정 가능 제안 | AI 및 로컬 분석 결과는 수정 가능한 제안으로 표시 |

## 🛡️ 개인정보 및 보안 원칙

- ✅ 기본 플로우에서 사용자 사진을 외부 서버로 업로드하지 않습니다
- ✅ Android Photo Picker를 우선 사용하고, 전체 갤러리 접근을 피합니다
- ✅ `MANAGE_EXTERNAL_STORAGE`를 사용하지 않습니다
- ✅ 위치 정보는 MVP 범위에서 제외합니다
- ✅ BYOK API Key는 기기 내부에 암호화 저장합니다
- ✅ API Key, 사진 바이트, 프롬프트, AI 응답, 개인정보를 로그에 남기지 않습니다

## 🛠️ 권장 기술 스택

<div align="center">

| 분야 | 기술 |
|------|------|
| 언어 | Kotlin |
| UI | Jetpack Compose, Material 3 |
| 카메라 | CameraX |
| 사진 선택 | Android Photo Picker |
| 데이터베이스 | Room (SQLite) |
| 설정 저장 | DataStore |
| 비동기 처리 | Coroutines / Flow |
| 이미지 로딩 | Coil |
| 로컬 AI | ML Kit Image Labeling |
| 키 저장 | Android Keystore (EncryptedSharedPreferences) |
| CI/CD | GitHub Actions |

</div>

## 📁 문서 구조

```
.
├── AGENTS.md                    # AI 에이전트 작업 규칙
├── README.md                    # 프로젝트 개요 (현재 파일)
├── HISTORY.md                   # 작업 이력 기록
├── CHANGELOG.md                # 사용자 대상 변경 요약
├── LICENSE                      # MIT License
├── docs/
│   ├── AGENT_SPEC.md         # 에이전트 상세 명세
│   ├── PRODUCT_BRIEF.md      # 제품 브리프
│   ├── PRD.md                # 제품 요구사항 정의서
│   ├── ARCHITECTURE.md       # 아키텍처 설계
│   ├── PRIVACY_AND_SECURITY.md # 개인정보 및 보안
│   ├── AI_PROVIDER_STRATEGY.md # AI 제공자 전략
│   ├── LOCAL_TAGGING.md      # 로컬 태깅 전략
│   ├── UX_FLOW.md            # 사용자 경험 흐름
│   ├── DESIGN_SYSTEM.md      # 디자인 시스템
│   ├── DATA_MODEL.md         # 데이터 모델
│   ├── ROADMAP.md            # 개발 로드맵
│   ├── PLAY_STORE_CHECKLIST.md # 플레이 스토어 체크리스트
│   ├── RELEASE_CHECKLIST.md  # 릴리즈 체크리스트
│   ├── PRIVACY_POLICY.md    # 개인정보 처리방침 (초안)
│   ├── CONTRIBUTING_GUIDE.md # 기여 가이드
│   └── CHANGELOG_GUIDE.md  # 변경 로그 작성 가이드
└── .agent/
    ├── tasks.md              # 작업 목록
    ├── epic1_ui_ux.md        # Epic: UI/UX & Design (#14)
    ├── epic2_core_ai.md      # Epic: Core Features & AI (#19)
    ├── epic3_perf_stab.md    # Epic: Performance & Stability (#16)
    ├── epic4_sec_priv.md     # Epic: Security, Privacy & Trust (#17)
    ├── epic5_market.md       # Epic: Marketing, Growth & Retention (#20)
    ├── progress.md           # 진행 상황 기록
    ├── decisions.md          # 결정 로그
    └── prompts.md           # 에이전트 프롬프트
```

## 🤖 에이전트 작업 방식

이 저장소의 단일 진입 규칙은 `AGENTS.md`입니다.

에이전트는 작업 전 다음 문서를 순서대로 읽어야 합니다:

1. `AGENTS.md` - 공통 작업 규칙
2. `docs/AGENT_SPEC.md` - 에이전트 상세 명세
3. `docs/PRODUCT_BRIEF.md` - 제품 브리프
4. `docs/PRD.md` - 제품 요구사항 정의서
5. `docs/ARCHITECTURE.md` - 아키텍처 설계
6. `docs/PRIVACY_AND_SECURITY.md` - 개인정보 및 보안
7. `docs/AI_PROVIDER_STRATEGY.md` - AI 제공자 전략
8. `docs/LOCAL_TAGGING.md` - 로컬 태깅 전략
9. `docs/UX_FLOW.md` - 사용자 경험 흐름
10. `docs/DESIGN_SYSTEM.md` - 디자인 시스템
11. `docs/DATA_MODEL.md` - 데이터 모델
12. `docs/ROADMAP.md` - 개발 로드맵
13. `.agent/tasks.md` - 작업 목록
14. `.agent/progress.md` - 진행 상황
15. `.agent/decisions.md` - 결정 로그
16. `HISTORY.md` - 작업 이력
17. `CHANGELOG.md` - 변경 로그

## 🚀 초기 개발 목표

첫 번째 구현 목표는 실제 AI 연동이 아니라, 앱의 기본 뼈대를 빠르게 검증하는 것입니다.

- ✅ Android 프로젝트 생성 및 확인
- ✅ Compose / Material 3 기본 구조 구성
- ✅ Home 화면 생성
- ✅ Capture Photo / Import Photo / Settings 액션 노출
- ✅ Create Record / Search or List / Settings 목적지 준비
- ✅ 실제 AI 호출 없이 Mock 또는 placeholder 기반 진행
- ✅ 광범위한 저장소/갤러리 권한 추가 금지

## 📦 빌드 & 테스트

```bash
# 로컬 빌드
./gradlew :app:assembleDebug

# 린트 검사
./gradlew :app:lintDebug

# 단위 테스트
./gradlew :app:testDebugUnitTest

# CI 검증 (GitHub Actions)
# push 또는 PR 시 자동 실행
```

## 🔗 관련 링크

- 📥 [APK 다운로드](https://github.com/jeiel85/markscene-android/releases)
- 🌐 [브랜딩 페이지](https://jeiel85.github.io/markscene-android/)
- 💻 [GitHub 저장소](https://github.com/jeiel85/markscene-android)
- 📋 [GitHub Issues & Epic](https://github.com/jeiel85/markscene-android/issues)
- 🔒 [개인정보 처리방침](docs/PRIVACY_POLICY.md)
- 📄 [MIT License](LICENSE)

---

<p align="center">
  MarkScene © 2026 • <a href="https://github.com/jeiel85/markscene-android">GitHub</a> • 
  <a href="docs/PRIVACY_POLICY.md">Privacy Policy</a> • 
  <a href="LICENSE">MIT License</a>
</p>
