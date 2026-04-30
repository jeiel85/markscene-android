# MarkScene

MarkScene는 사진을 **검색 가능한 비주얼 메모**로 바꾸는 Android 앱입니다.

기본 기능은 API Key 없이 로컬 우선으로 동작합니다. 사용자는 사진을 찍거나 선택한 뒤, 앱이 생성한 빠른 태그 초안을 수정하고 저장할 수 있습니다. 고급 AI 분석은 사용자가 직접 API Key를 입력하는 BYOK(Bring Your Own Key) 방식의 선택 기능으로 제공합니다.

## 프로젝트 식별자

```text
App Name: MarkScene
Repository: markscene-android
Android applicationId: com.markscene.app
Root package: com.markscene.app
```

## 핵심 컨셉

> 사진을 찍으면 주요 물건과 장면이 태그로 정리되고, 나중에 쉽게 찾을 수 있는 비주얼 메모 앱.

## MVP 핵심 플로우

```text
사진 촬영 또는 선택
  -> 즉시 이미지 미리보기
  -> 로컬 태그 초안 생성
  -> 태그 칩 수정/삭제/추가
  -> 로컬 기록 저장
  -> 태그, 제목, 메모로 검색
```

## 제품 원칙

- 빠른 체감 속도를 최우선으로 합니다.
- 최신 Material 3 기반의 깔끔한 UI를 지향합니다.
- API Key 없이도 기본 사용 경험이 살아 있어야 합니다.
- 사진, 태그, 메모는 기본적으로 기기 내부에 저장합니다.
- 고급 AI 분석은 사용자가 명시적으로 실행한 경우에만 수행합니다.
- AI 및 로컬 분석 결과는 수정 가능한 제안으로 표시합니다.

## 개인정보 및 보안 원칙

- 기본 플로우에서 사용자 사진을 외부 서버로 업로드하지 않습니다.
- Android Photo Picker를 우선 사용하고, 전체 갤러리 접근을 피합니다.
- `MANAGE_EXTERNAL_STORAGE`를 사용하지 않습니다.
- 위치 정보는 MVP 범위에서 제외합니다.
- BYOK API Key는 기기 내부에 암호화 저장합니다.
- API Key, 사진 바이트, 프롬프트, AI 응답, 개인정보를 로그에 남기지 않습니다.

## 권장 기술 스택

- Kotlin
- Jetpack Compose
- Material 3
- CameraX
- Android Photo Picker
- Room
- DataStore
- Coroutines / Flow
- Coil
- Android Keystore 기반 암호화 저장소

## 문서 구조

```text
.
├─ AGENTS.md
├─ README.md
├─ HISTORY.md
├─ CHANGELOG.md
├─ docs/
│  ├─ AGENT_SPEC.md
│  ├─ PRODUCT_BRIEF.md
│  ├─ PRD.md
│  ├─ ARCHITECTURE.md
│  ├─ PRIVACY_AND_SECURITY.md
│  ├─ AI_PROVIDER_STRATEGY.md
│  ├─ LOCAL_TAGGING.md
│  ├─ UX_FLOW.md
│  ├─ DESIGN_SYSTEM.md
│  ├─ DATA_MODEL.md
│  ├─ ROADMAP.md
│  ├─ PLAY_STORE_CHECKLIST.md
│  ├─ RELEASE_CHECKLIST.md
│  ├─ CONTRIBUTING_GUIDE.md
│  └─ CHANGELOG_GUIDE.md
└─ .agent/
   ├─ tasks.md
   ├─ progress.md
   ├─ decisions.md
   └─ prompts.md
```

## 에이전트 작업 방식

이 저장소의 단일 진입 규칙은 `AGENTS.md`입니다.

에이전트는 작업 전 다음 문서를 순서대로 읽어야 합니다.

1. `AGENTS.md`
2. `docs/AGENT_SPEC.md`
3. `docs/PRODUCT_BRIEF.md`
4. `docs/PRD.md`
5. `docs/ARCHITECTURE.md`
6. `docs/PRIVACY_AND_SECURITY.md`
7. `docs/AI_PROVIDER_STRATEGY.md`
8. `docs/LOCAL_TAGGING.md`
9. `docs/UX_FLOW.md`
10. `docs/DESIGN_SYSTEM.md`
11. `docs/DATA_MODEL.md`
12. `docs/ROADMAP.md`
13. `.agent/tasks.md`
14. `.agent/progress.md`
15. `.agent/decisions.md`
16. `HISTORY.md`
17. `CHANGELOG.md`

## 초기 개발 목표

첫 번째 구현 목표는 실제 AI 연동이 아니라, 앱의 기본 뼈대를 빠르게 검증하는 것입니다.

- Android 프로젝트 생성 또는 확인
- Compose / Material 3 기본 구조 구성
- Home 화면 생성
- Capture Photo / Import Photo / Settings 액션 노출
- Create Record / Search or List / Settings 목적지 준비
- 실제 AI 호출 없이 Mock 또는 placeholder 기반 진행
- 광범위한 저장소/갤러리 권한 추가 금지

## 라이선스

아직 결정하지 않았습니다. 공개 저장소로 운영할 경우 출시 전 라이선스를 명확히 선택하세요.
