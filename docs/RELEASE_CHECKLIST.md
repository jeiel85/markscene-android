# Release Checklist

## Before Internal Testing

- [x] App builds locally (`./gradlew :app:assembleDebug`).
- [x] App builds in CI if CI exists (`.github/workflows/android-ci.yml`).
- [x] No secrets committed (`.gitignore` covers `.env`, `secrets.properties`, `local.properties`, `keystore/`, `*.jks`, `*.keystore`).
- [x] Basic local flow works without external AI API key (mock tags, local storage).
- [x] Settings screen works (local model download/delete, model token save/delete).
- [x] External AI API key input is absent.
- [x] Records can be deleted (Record List / Detail delete action).
- [x] App does not request broad media access (Photo Picker only, no `MANAGE_EXTERNAL_STORAGE`).
- [x] CameraX capture flow implemented.
- [x] ML Kit on-device tagging implemented.
- [x] Room database persistence implemented.

## Before Closed Testing

- [x] Privacy policy draft complete (`docs/PRIVACY_POLICY.md`).
- [x] Data Safety draft complete (covered in PRIVACY_POLICY.md).
- [ ] Screenshots prepared.
- [x] Store description avoids overpromising (`README.md` uses cautious wording).
- [x] Local AI warning implemented (on-device analysis dialog).
- [x] User-facing copy reviewed (uses "감지된 태그", "제안", "수정 가능" patterns).
- [x] Error states reviewed (Toast messages, exception handling).
- [x] LICENSE file added (`MIT License`).

## Before Public Release

- [ ] Current Google Play policy checked.
- [ ] Current Android permission behavior checked.
- [ ] Current local model license and Android AI policy checked.
- [x] Privacy policy URL live (`https://jeiel85.github.io/markscene-android/privacy/` on GitHub Pages).
- [ ] Contact email works.
- [x] Release signing configured outside repository (Secrets based automation).
- [x] No debug logs expose private data (verified in `PRIVACY_AND_SECURITY.md`).
- [x] Version name and code set (`versionName "2.6.8"`, `versionCode 268`).

## Current Implementation Status

| Component | Status |
|-----------|--------|
| Version | v2.6.8 (2026-05-30) |
| CameraX Capture | ✅ Implemented |
| Photo Picker Import | ✅ Implemented |
| ML Kit Local Tagging | ✅ Implemented (fallback to mock) |
| Local VLM Advanced Analysis | ✅ Implemented (app-downloaded approved MediaPipe-compatible model) |
| Room Database | ✅ Implemented |
| External AI API Key Input | ❌ Removed |
| Encrypted Model Token Storage | ✅ Implemented (EncryptedSharedPreferences) |
| Privacy Policy | ✅ Draft Complete |
| LICENSE | ✅ MIT Added |
| Unit Tests | ⚠️ Basic tests added (CI runs `testDebugUnitTest`) |
| Integration Tests | ✅ Emulator `connectedDebugAndroidTest` automation |
| CI Pipeline | ✅ lint + unit test + assembleDebug + emulator integration/smoke + release APK automation |

## Notes

- README.md and docs/index.html updated with project branding.
- GitHub Pages site live at: `https://jeiel85.github.io/markscene-android/`
- APK releases automated via `release-apk.yml` on tag push (`v*.*.*`).
