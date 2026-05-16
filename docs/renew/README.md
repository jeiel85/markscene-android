# MarkScene Scene Memory Design Bundle

이 묶음은 MarkScene 앱을 기존의 **검색 가능한 비주얼 메모 앱**에서
**장면 기반 개인 기억 저장소**로 확장하기 위한 설계 문서 세트입니다.

## 포함 문서

1. `SCENE_MEMORY_EXTENSION_SPEC.md`
   - 제품 방향성
   - 핵심 기능 설계
   - 데이터 모델
   - AI 확장 전략
   - 개인정보/보안 원칙
   - 단계별 개발 계획

2. `DESIGN_DIRECTION.md`
   - 새 디자인 방향성
   - Material 3 기반 화면 설계
   - 컬러/타이포그래피/컴포넌트 가이드
   - 주요 화면별 UI 방향

3. `GITHUB_ISSUE_DRAFTS.md`
   - 바로 GitHub Issue로 옮길 수 있는 작업 단위 초안
   - Scene Timeline, Memory Tags, Recall Box, Daily Recap 등

4. `RALPH_LOOP_CODING_PROMPTS.md`
   - Ralph Loop 코딩용 요청 프롬프트
   - 에이전트 반복 실행에 적합한 종료 조건/검증 조건 포함

5. `AGENT_START_PROMPT.md`
   - AI 코딩 에이전트에게 바로 줄 수 있는 첫 작업 프롬프트

## 권장 저장 위치

```text
docs/SCENE_MEMORY_EXTENSION_SPEC.md
docs/DESIGN_DIRECTION.md
.agent/scene_memory_issue_drafts.md
.agent/ralph_loop_coding_prompts.md
.agent/scene_memory_start_prompt.md
```

## 핵심 방향

MarkScene의 기존 원칙인 local-first, API Key 불필요, BYOK AI, 사용자 주도 AI,
수정 가능한 제안, 개인정보 보호 원칙을 유지하면서 다음 기능을 확장합니다.

- Scene Timeline
- Memory Tags
- Daily Scene Recap
- Recall Box
- Ask My Scenes
