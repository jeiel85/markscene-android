# Agent Prompts

## First Coding Prompt

아래 프롬프트에서 `https://github.com/jeiel85/markscene-android.git`만 실제 저장소 URL로 바꿔 사용하세요.

```text
You are an autonomous coding agent working on MarkScene.

Repository:
https://github.com/jeiel85/markscene-android.git

This is the first implementation iteration. Your job is to make real, measurable progress while keeping every change small, safe, and reviewable.

Before changing anything, sync the latest source and inspect repository status:

1. git fetch origin
2. git checkout main
3. git pull origin main
4. git status

If the working tree is not clean, do not overwrite existing user changes. Report the state and continue only when safe.

Then read these files in order:

1. AGENTS.md
2. docs/AGENT_SPEC.md
3. docs/PRODUCT_BRIEF.md
4. docs/PRD.md
5. docs/ARCHITECTURE.md
6. docs/PRIVACY_AND_SECURITY.md
7. docs/AI_PROVIDER_STRATEGY.md
8. docs/LOCAL_TAGGING.md
9. docs/UX_FLOW.md
10. docs/DESIGN_SYSTEM.md
11. docs/DATA_MODEL.md
12. docs/ROADMAP.md
13. .agent/tasks.md
14. .agent/progress.md
15. .agent/decisions.md
16. HISTORY.md
17. CHANGELOG.md

Non-negotiable rules:

- Do not add MANAGE_EXTERNAL_STORAGE.
- Do not add broad media permissions.
- Do not scan the user's entire gallery.
- Do not add analytics, ads, crash reporting, login, account features, or cloud sync.
- Do not hardcode or commit API keys, tokens, keystores, or credentials.
- The app must work without an API key.
- AI analysis must be optional and user-initiated.
- Do not implement real external AI calls in this first iteration.
- Keep all changes small and reviewable.

First task:

Bootstrap the Android app and implement the smallest useful Compose shell.

1. Create or verify a Kotlin Android project.
2. Add Jetpack Compose and Material 3 baseline.
3. Create a simple Home screen with three visible actions:
   - Capture Photo
   - Import Photo
   - Settings
4. Add placeholder navigation destinations for:
   - Create Record
   - Record List or Search
   - Settings
5. Do not implement real AI yet.
6. Do not request broad storage/media permissions.
7. Update .agent/progress.md with what changed and what checks were run.
8. Update .agent/decisions.md only if you make a durable decision.
9. Update HISTORY.md and CHANGELOG.md when the change is meaningful to users or project operation.
10. Commit the changes with a focused Korean commit message.
11. Push the commit to the remote repository so it can be reviewed from another machine.
12. If GitHub Actions exists or is created, check the CI result. If CI fails, inspect logs, fix, commit, and push again when safe.

Validation:

- Run the fastest available build or compile check.
- Run formatting/lint checks if already configured.
- If any check cannot be run, document exactly why in .agent/progress.md and the final report.

Final report format must follow AGENTS.md.
```

## Follow-Up Iteration Prompt

```text
You are an autonomous coding agent working on MarkScene.

Repository:
https://github.com/jeiel85/markscene-android.git

Continue the project using the rules in AGENTS.md.

Start by syncing the latest source and reading the required documents in AGENTS.md. Then choose the highest-priority unfinished task from .agent/tasks.md.

Constraints:

- Keep the change small and reviewable.
- Preserve the local-first, API-key-optional product experience.
- Do not add broad media permissions.
- Do not add real external AI calls unless the current task explicitly requires BYOK provider work.
- Update .agent/progress.md.
- Update .agent/decisions.md only for durable decisions.
- Update HISTORY.md and CHANGELOG.md if the change affects product behavior, project operation, privacy, build, CI, or release flow.
- Run the fastest relevant checks.
- Commit and push when done.
- Check GitHub Actions if available.

Final report in Korean.
```

## Privacy-Sensitive Change Prompt Add-On

개인정보, 권한, API Key, 외부 전송, AI Provider, 저장소 정책을 바꾸는 작업에는 아래 문구를 추가하세요.

```text
This task may affect privacy, permissions, API keys, or external data transfer.

Before coding:

1. Identify the exact user data involved.
2. Identify whether any data leaves the device.
3. Identify the user-facing disclosure needed.
4. Check docs/PRIVACY_AND_SECURITY.md and docs/PLAY_STORE_CHECKLIST.md.
5. Do not proceed if the change conflicts with AGENTS.md.

After coding:

1. Update privacy/security documentation if behavior changed.
2. Verify no secrets or private data are logged.
3. Verify no broad permission was added accidentally.
4. Record the validation in .agent/progress.md.
```
