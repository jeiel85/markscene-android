# Contributing Guide

## Contribution Principles

- Keep changes small.
- Preserve privacy-first behavior.
- Do not add unnecessary dependencies.
- Prefer clear UI states over hidden behavior.
- Avoid broad permissions.
- Do not introduce tracking.

## Pull Request Checklist

- [ ] The change is focused.
- [ ] The app still works without an API key.
- [ ] No secrets are committed.
- [ ] No broad permissions are added.
- [ ] User data flow is unchanged or documented.
- [ ] Tests/checks were run or skipped with explanation.
- [ ] Relevant docs are updated.

## Commit Message Style

Preferred format:

```text
type(scope): short summary
```

Examples:

```text
feat(records): add editable tag chips
fix(settings): prevent API key from being logged
docs(privacy): clarify advanced AI data flow
```

