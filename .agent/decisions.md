# Agent Decisions

## D001: Basic Experience Must Work Without API Key

Decision: The app must provide a useful basic experience without requiring an AI API key.

Reason: If API key setup is mandatory, the initial product experience becomes too weak and too technical for general users.

Implication: Local tag suggestions, manual editing, local saving, and local search are MVP requirements.

## D002: Local VLM Only for Advanced AI

Decision: Advanced AI analysis uses only an app-downloaded local VLM model.

Reason: The product promise is local-first, and the user explicitly chose to remove Gemini/BYOK API Key input and external AI fallback.

Implication: Settings manages local model download/delete and optional model-download tokens only. Record detail must not call external AI providers.

## D003: Local-First Storage

Decision: Records, tags, and analysis results are stored locally by default.

Reason: The app handles potentially sensitive photos and personal notes.

Implication: No backend is used in MVP.

## D004: Photo Picker Over Broad Gallery Permission

Decision: Gallery import should use Android Photo Picker.

Reason: The app only needs images selected by the user, not broad access to the media library.

Implication: Do not add broad media permissions unless explicitly approved later.

## D005: AI Results Are Suggestions

Decision: AI and local tag outputs must be editable suggestions, not guaranteed facts.

Reason: Image analysis can be wrong, especially for counts, small objects, or ambiguous scenes.

Implication: UI must allow editing, deletion, and manual additions.

## D006: Remove External BYOK Fallback

Decision: Advanced photo analysis requires an app-downloaded approved on-device VLM model. BYOK Gemini is not a fallback.

Reason: Local VLM analysis better matches MarkScene's privacy promise and avoids requiring an external API key for richer object/tag suggestions.

Implication: Settings must manage local model download/delete, record detail must disclose on-device processing, and local VLM output must be stored as editable suggestions with a distinct source.


## 2026-04-30 - Project Naming

Decision:
- Use `MarkScene` as the app and project name.
- Use `markscene-android` as the default GitHub repository name.
- Use `com.markscene.app` as the default Android `applicationId` and root package.

Reason:
- The name communicates the core product idea: marking and remembering scenes from photos.
- It fits the local-first visual memo concept while leaving room for object tagging, scene records, and optional AI analysis.

## 2026-11-27 - Play Upload Key Pinned in CI

Decision:
- Play 업로드 키는 로컬 `.keystore/release.keystore`(별칭 markscene, 인증서 SHA-256 `83:1F:8B:D9:6E:BB:5F:D0:8B:BF:3E:64:30:24:AA:CD:22:BA:54:30:03:9B:2B:D9:C2:A7:77:5D:17:5F:C3:3A`)입니다. Play Console 앱 무결성의 업로드 키와 같다는 것을 사용자가 확인했습니다(2026-11-28).
- `play-release.yml`은 이 지문을 `UPLOAD_CERT_SHA256`으로 고정하고, 다른 키로 서명된 번들은 올리기 전에 멈춥니다. 키는 GitHub 환경 `play`의 Secrets에만 둡니다.

Reason:
- GitHub 릴리스 APK(`release-apk.yml`, 저장소 Secrets `RELEASE_*`)는 다른 키(`22:30:C7:84:…:E9:A1:C7`)로 서명됩니다. 키를 섞으면 Play가 업로드를 거부합니다.

Implication:
- GitHub APK와 Play 버전은 서로 덮어 설치할 수 없습니다(지우고 다시 설치).
- 업로드 키를 바꾸면(Play Console 업로드 키 재설정) 워크플로 지문과 환경 Secrets를 함께 바꿉니다.

