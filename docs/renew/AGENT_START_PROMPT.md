# Agent Start Prompt

아래 프롬프트를 AI 코딩 에이전트에게 첫 메시지로 전달하면 된다.

```text
We are extending the MarkScene Android app.

The new product direction is:

“MarkScene — 내 하루의 장면을 검색 가능한 기억으로.”

Your first task is not to code immediately.
First inspect the repository and read the required documents.

Read in this order:
1. AGENTS.md
2. docs/AGENT_SPEC.md
3. docs/PRD.md
4. docs/ARCHITECTURE.md
5. docs/DATA_MODEL.md
6. docs/UX_FLOW.md
7. docs/DESIGN_SYSTEM.md
8. docs/SCENE_MEMORY_EXTENSION_SPEC.md
9. docs/DESIGN_DIRECTION.md

Then produce a short implementation plan for Phase 1: Scene Timeline.

Phase 1 goal:
Add or improve a Today screen that shows existing PhotoRecord items grouped by date as a Scene Timeline.

Constraints:
- Keep the existing local-first behavior.
- Do not add broad media permissions.
- Do not add background location.
- Do not add AI behavior.
- Do not break capture/import/search/settings.
- Keep the architecture separated into UI, domain, and data layers.

After the plan, implement Phase 1 only.

Acceptance criteria:
- Existing records appear grouped by local date.
- Today’s records are visible.
- Empty state appears when there are no records.
- SceneCard shows thumbnail, time, title or memo fallback, and top tags.
- Capture and Import actions remain easy to access.
- The app builds.

Verification:
Run:
- ./gradlew :app:assembleDebug
- ./gradlew :app:testDebugUnitTest

If a command cannot run, document why.

When complete, summarize:
1. Files changed
2. What was implemented
3. Verification results
4. Follow-up tasks

Only then output:

RALPH_COMPLETE
```
