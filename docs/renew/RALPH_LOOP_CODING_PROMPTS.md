# Ralph Loop Coding Prompts for MarkScene Scene Memory

## 1. Ralph Loop 사용 방향

Ralph Loop 방식으로 작업할 때는 “큰 기능을 한 번에 완성해줘”보다,
**작은 목표 + 명확한 완료 조건 + 검증 명령 + 종료 신호**를 같이 주는 것이 좋다.

핵심은 다음 구조이다.

```text
1. 목표
2. 작업 범위
3. 절대 하지 말아야 할 것
4. 구현 순서
5. 완료 기준
6. 검증 명령
7. 실패 시 행동
8. 종료 신호
```

Ralph Loop는 에이전트가 반복적으로 계획, 구현, 테스트, 수정, 검증을 수행하게 만드는 방식이므로,
프롬프트 안에 반드시 “완료 조건”과 “검증 방법”을 넣어야 한다.

---

## 2. 공통 시스템 프롬프트

아래 프롬프트는 Ralph Loop 시작 시 가장 먼저 넣는 기본 지시문으로 사용한다.

```text
You are working on the MarkScene Android repository.

Your job is to implement the requested task through an autonomous coding loop:
plan, inspect, implement, test, fix, and verify.

Follow these rules:
- Read AGENTS.md first.
- Then read docs/AGENT_SPEC.md, docs/PRD.md, docs/ARCHITECTURE.md, docs/DATA_MODEL.md, docs/UX_FLOW.md, docs/DESIGN_SYSTEM.md.
- Preserve the existing local-first product principle.
- Do not add broad gallery permissions.
- Do not add background location.
- Do not add cloud sync, account system, subscription, or backend.
- Do not require an API key for the default user flow.
- Treat all AI outputs as editable suggestions.
- Keep architecture separated into UI, domain, data, and AI/provider layers.
- Prefer small, incremental changes.
- Update relevant documentation when behavior or architecture changes.
- Run available tests or Gradle verification commands before declaring completion.
- If tests fail, inspect the cause and fix it.
- Do not stop until the acceptance criteria are satisfied or a real blocker is found.

When complete, output exactly:

RALPH_COMPLETE

Before that final line, provide:
1. Summary of changes
2. Files changed
3. Verification commands run
4. Known limitations or follow-up tasks
```

---

## 3. Phase 1 프롬프트: Scene Timeline 구현

```text
Implement Phase 1 of the MarkScene Scene Memory extension: Today Screen / Scene Timeline.

Read these documents first:
- AGENTS.md
- docs/AGENT_SPEC.md
- docs/PRD.md
- docs/ARCHITECTURE.md
- docs/DATA_MODEL.md
- docs/UX_FLOW.md
- docs/DESIGN_SYSTEM.md
- docs/SCENE_MEMORY_EXTENSION_SPEC.md
- docs/DESIGN_DIRECTION.md

Goal:
Replace or extend the current home/recent records experience with a Today screen that shows saved visual records grouped by date as a Scene Timeline.

Scope:
- Add a Today screen if it does not exist.
- Group existing PhotoRecord items by local date using createdAt.
- Show a SceneCard for each record.
- Each SceneCard should display:
  - thumbnail
  - time
  - title or memo fallback
  - top tags
  - created date/time
- Add an empty state:
  “아직 오늘 남긴 장면이 없습니다. 사진을 찍거나 가져와서 첫 번째 기억을 남겨보세요.”
- Keep Capture and Import actions easy to access.
- Do not introduce new AI behavior in this phase.
- Do not change the existing local tag generation behavior.
- Do not add new permissions.

Acceptance criteria:
- Existing records appear in date groups.
- Today’s records are visible on the Today screen.
- Empty state appears when there are no records.
- Tapping a SceneCard opens the record detail screen if that route exists.
- The app still builds.
- Existing capture/import/search/settings flows are not broken.

Verification:
Run the most appropriate available commands, such as:
- ./gradlew :app:assembleDebug
- ./gradlew :app:testDebugUnitTest
- ./gradlew :app:lintDebug

If a command cannot be run in the environment, document why.

Failure handling:
- If build fails, inspect the error and fix it.
- If tests fail because of existing unrelated failures, document them clearly and avoid hiding them.
- If a required class or route does not exist, implement the smallest compatible version.

Completion:
Only when acceptance criteria are met, output:

RALPH_COMPLETE
```

---

## 4. Phase 2 프롬프트: Memory Context 데이터 모델

```text
Implement Phase 2 of the MarkScene Scene Memory extension: Memory Context data model.

Goal:
Add data support for memory type, mood, context type, and recall state without breaking existing PhotoRecord and PhotoTag behavior.

Scope:
- Add MemoryType enum.
- Add MoodType enum if appropriate.
- Add ContextType enum if appropriate.
- Add MemoryContextEntity.
- Add RecordMemoryTypeCrossRef for multiple memory types.
- Add DAO methods for insert, update, query by recordId, query by memoryType, query recall items.
- Add repository/domain models if the architecture uses them.
- Add Room migration if the project already has a Room database versioning strategy.
- Ensure deleting a PhotoRecord deletes related memory context data.
- Add or update tests where practical.

Do not:
- Do not rewrite the entire database layer.
- Do not remove existing fields.
- Do not break existing saved records.
- Do not add AI integration in this task.
- Do not add new permissions.

Suggested model:
Use docs/SCENE_MEMORY_EXTENSION_SPEC.md section “데이터 모델 설계” as the primary reference.

Acceptance criteria:
- Project builds.
- Existing PhotoRecord and PhotoTag flows still compile.
- MemoryContext can be saved and loaded for a record.
- Multiple memory types can be attached to one record.
- Recall query can return records marked for recall.
- Database migration is safe for existing users.

Verification:
Run:
- ./gradlew :app:assembleDebug
- ./gradlew :app:testDebugUnitTest

If available, add focused unit tests for DAO or repository behavior.

Completion:
Only when acceptance criteria are met, output:

RALPH_COMPLETE
```

---

## 5. Phase 3 프롬프트: Memory Tags UI

```text
Implement Phase 3 of the MarkScene Scene Memory extension: Memory Tags UI.

Goal:
Allow users to select one or more Memory Types when creating or editing a visual record.

Scope:
- Add MemoryTypeChip composable.
- Add Memory Type section to Create/Edit Record screen.
- Supported types:
  - 아이디어
  - 업무
  - 가족
  - 육아
  - 영수증
  - 장소
  - 물건 위치
  - 문서
  - 쇼핑
  - 집
  - 사이드 프로젝트
  - 나중에 보기
  - 감정 기록
  - 기타
- Persist selected memory types through the data layer.
- Restore selected memory types in edit mode.
- Show selected memory types in Record Detail screen.
- Keep saving possible even when no memory type is selected.

Do not:
- Do not make Memory Type mandatory.
- Do not infer sensitive emotion automatically.
- Do not add AI behavior in this phase.
- Do not break existing tag chip editing.

Acceptance criteria:
- User can select multiple Memory Types.
- Selected Memory Types are saved.
- Selected Memory Types are visible in detail.
- Existing tag editing still works.
- App builds and basic tests pass.

Verification:
Run:
- ./gradlew :app:assembleDebug
- ./gradlew :app:testDebugUnitTest
- ./gradlew :app:lintDebug

Completion:
Only when acceptance criteria are met, output:

RALPH_COMPLETE
```

---

## 6. Phase 4 프롬프트: Recall Box

```text
Implement Phase 4 of the MarkScene Scene Memory extension: Recall Box.

Goal:
Add a screen that collects records worth revisiting later.

Scope:
- Add isWorthRecalling support to UI if not already visible.
- Add a Recall toggle in Create/Edit Record screen.
- Add Recall screen.
- Recall screen should include records that match at least one condition:
  - isWorthRecalling = true
  - Memory Type is Idea
  - Memory Type is Later
  - memo contains simple recall keywords: 나중에, 확인, 만들기, 사야 함, 정리, TODO
- Show records using SceneCard or a compact variant.
- Add Recall to bottom navigation if bottom navigation exists.
- If bottom navigation does not exist, add an entry point from Today screen.

Do not:
- Do not add notifications yet.
- Do not add background workers yet.
- Do not add AI recall suggestions yet.
- Do not upload any data.

Acceptance criteria:
- User can mark a record as Recall.
- Recall screen shows manually marked records.
- Recall screen shows idea/later records.
- Recall screen shows keyword-based candidates.
- Deleting a record removes it from Recall screen.
- App builds.

Verification:
Run:
- ./gradlew :app:assembleDebug
- ./gradlew :app:testDebugUnitTest

Completion:
Only when acceptance criteria are met, output:

RALPH_COMPLETE
```

---

## 7. Phase 5 프롬프트: Daily Recap v1

```text
Implement Phase 5 of the MarkScene Scene Memory extension: Daily Scene Recap v1.

Goal:
Show a local, rule-based daily recap on the Today screen without requiring network or AI.

Scope:
- Add GenerateDailyRecapUseCase.
- Calculate for a given local date:
  - record count
  - top 5 tags
  - memory type counts
  - recall count
  - most active recording hour if easy to implement
- Add DailyRecapCard to Today screen.
- Update recap when records are added, edited, or deleted.
- Keep implementation local-only.

Do not:
- Do not call Gemini or any network API.
- Do not store sensitive inferred psychological conclusions.
- Do not block UI while calculating recap.
- Do not add background sync.

Acceptance criteria:
- Today screen shows DailyRecapCard.
- DailyRecapCard works with zero records, one record, and multiple records.
- Summary updates after record changes.
- No API key is required.
- App builds and tests pass.

Verification:
Run:
- ./gradlew :app:assembleDebug
- ./gradlew :app:testDebugUnitTest
- ./gradlew :app:lintDebug

Completion:
Only when acceptance criteria are met, output:

RALPH_COMPLETE
```

---

## 8. Phase 6 프롬프트: Search 확장

```text
Implement Phase 6 of the MarkScene Scene Memory extension: Search expansion.

Goal:
Extend search so users can find records by Memory Type, Recall state, and simple date phrases.

Scope:
- Preserve existing search by tag, title, and memo.
- Add Memory Type filters.
- Add Recall filter.
- Add simple local query parsing:
  - 오늘
  - 어제
  - 이번 주
  - 지난주
  - 아이디어
  - 영수증
  - 나중에 보기
- Update Search UI with filter chips.
- Show results using SceneCard or existing record card.

Do not:
- Do not add AI natural language search yet.
- Do not require network.
- Do not remove existing search behavior.

Acceptance criteria:
- Existing search behavior still works.
- Searching “아이디어” returns idea memory records.
- Searching “영수증” returns receipt memory records or matching text.
- Searching “나중에 보기” returns recall/later records.
- Date filters work for today and this week.
- App builds.

Verification:
Run:
- ./gradlew :app:assembleDebug
- ./gradlew :app:testDebugUnitTest

Completion:
Only when acceptance criteria are met, output:

RALPH_COMPLETE
```

---

## 9. 문서 업데이트 프롬프트

```text
Update the MarkScene documentation to reflect the Scene Memory extension.

Scope:
- Add docs/SCENE_MEMORY_EXTENSION_SPEC.md if missing.
- Add docs/DESIGN_DIRECTION.md if missing.
- Update README.md with the new positioning:
  “내 하루의 장면을 검색 가능한 기억으로.”
- Update docs/PRD.md with Scene Timeline, Memory Tags, Daily Recap, and Recall Box as post-MVP extension features.
- Update docs/UX_FLOW.md with Today, Recall, and Daily Recap flows.
- Update docs/DATA_MODEL.md with MemoryContext and RecordMemoryTypeCrossRef.
- Update docs/DESIGN_SYSTEM.md with Calm Memory UI direction.
- Update CHANGELOG.md and HISTORY.md according to existing project conventions.

Do not:
- Do not claim implemented features are complete unless they are already implemented.
- Clearly separate “planned” from “implemented”.
- Do not remove existing MVP documentation.

Acceptance criteria:
- Documentation is internally consistent.
- Existing MVP principles remain intact.
- Planned features are marked as planned if not implemented.
- App code is not changed unless necessary.

Verification:
- Review markdown formatting.
- Check links and headings.
- If the repo has markdown lint, run it.

Completion:
Only when acceptance criteria are met, output:

RALPH_COMPLETE
```

---

## 10. 전체 실행용 마스터 프롬프트

아래는 Ralph Loop에 장기 목표를 줄 때 사용하는 마스터 프롬프트이다.
한 번에 너무 큰 작업이 되면 Phase 1부터 순서대로 나누어 실행하는 것이 안전하다.

```text
You are implementing the MarkScene Scene Memory extension.

Read:
- AGENTS.md
- docs/AGENT_SPEC.md
- docs/PRD.md
- docs/ARCHITECTURE.md
- docs/DATA_MODEL.md
- docs/UX_FLOW.md
- docs/DESIGN_SYSTEM.md
- docs/SCENE_MEMORY_EXTENSION_SPEC.md
- docs/DESIGN_DIRECTION.md

Long-term goal:
Extend MarkScene from a searchable visual memo app into a scene-based personal memory app.

Implement in phases:
1. Scene Timeline
2. Memory Context data model
3. Memory Tags UI
4. Recall Box
5. Daily Scene Recap v1
6. Search expansion
7. Optional BYOK AI Scene Memory analysis

Important:
Do not implement all phases in one unsafe giant diff.
Start with the smallest phase that can build and verify.
Prefer a clean, incremental PR-sized change.
After finishing each phase, update docs and stop with RALPH_COMPLETE.

Global constraints:
- Keep local-first behavior.
- Do not add broad gallery permissions.
- Do not add background location.
- Do not add cloud sync.
- Do not require API keys for default flow.
- Do not auto-upload images.
- Treat AI outputs as editable suggestions.
- Preserve existing capture/import/tag/search flows.
- Run build/tests before completion.

Completion signal:
Only output RALPH_COMPLETE after a phase is implemented, verified, and summarized.
```
