# Release Checklist

## Before Internal Testing

- [x] App builds locally (`./gradlew :app:assembleDebug`).
- [x] App builds in CI if CI exists (`.github/workflows/android-ci.yml`).
- [x] No secrets committed (`.gitignore` covers `.env`, `secrets.properties`, `local.properties`, `keystore/`, `*.jks`, `*.keystore`).
- [x] Basic local flow works without API key (mock tags, local storage).
- [x] Settings screen works (API key save/delete, encrypted storage).
- [x] API key can be deleted (Settings screen delete action).
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
- [x] External AI warning implemented (analysis warning dialog).
- [x] User-facing copy reviewed (uses "감지된 태그", "제안", "수정 가능" patterns).
- [x] Error states reviewed (Toast messages, exception handling).
- [x] LICENSE file added (`MIT License`).

## Before Public Release

- [ ] Current Google Play policy checked.
- [ ] Current Android permission behavior checked.
- [ ] Current Gemini/API provider terms checked.
- [x] Privacy policy URL live (`docs/PRIVACY_POLICY.md` on GitHub Pages).
- [ ] Contact email works.
- [x] Release signing configured outside repository (Secrets based automation).
- [x] No debug logs expose private data (verified in `PRIVACY_AND_SECURITY.md`).
- [x] Version name and code set (`versionName "2.0.5"`, `versionCode 205`).

## Current Implementation Status

| Component | Status |
|-----------|--------|
| Version | v2.0.5 (2026-05-05) |
| CameraX Capture | ✅ Implemented |
| Photo Picker Import | ✅ Implemented |
| ML Kit Local Tagging | ✅ Implemented (fallback to mock) |
| Gemini BYOK Analysis | ✅ Implemented (fallback to mock) |
| Room Database | ✅ Implemented |
| Encrypted API Key Storage | ✅ Implemented (EncryptedSharedPreferences) |
| Privacy Policy | ✅ Draft Complete |
| LICENSE | ✅ MIT Added |
| Unit Tests | ⚠️ Basic tests added (CI runs `testDebugUnitTest`) |
| CI Pipeline | ✅ lint + test + assembleDebug + release APK automation |

## Notes

- README.md and docs/index.html updated with project branding.
- GitHub Pages site live at: `https://jeiel85.github.io/markscene-android/`
- APK releases automated via `release-apk.yml` on tag push (`v*.*.*`).
