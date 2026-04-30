# Play Store Checklist

This checklist is not legal advice. Verify current Google Play, Android, and AI provider policies before release.

## Permissions

- [ ] No `MANAGE_EXTERNAL_STORAGE`.
- [ ] No broad media permissions unless explicitly justified and approved.
- [ ] Photo import uses Android Photo Picker.
- [ ] Camera permission is requested only when needed.
- [ ] No background location.
- [ ] No unnecessary permissions.

## Data Safety

- [ ] Document what data is stored on-device.
- [ ] Document when selected images are sent to an AI provider.
- [ ] Document that API keys are stored on-device.
- [ ] Document that the developer does not run a backend in MVP.
- [ ] Ensure Play Console Data Safety answers match actual implementation.

## Privacy Policy

- [ ] Publish privacy policy URL.
- [ ] Link privacy policy inside app.
- [ ] Explain photo handling.
- [ ] Explain optional AI provider transmission.
- [ ] Explain API key storage.
- [ ] Explain deletion behavior.
- [ ] Provide contact email.

## AI Disclosure

- [ ] The app explains that AI output may be inaccurate.
- [ ] The app says suggestions are editable.
- [ ] The app warns before sending images externally.
- [ ] The app avoids claiming exact object detection or guaranteed counts.

## Open Source Safety

- [ ] No real API keys in repository.
- [ ] No signing keys in repository.
- [ ] No release keystore in repository.
- [ ] `.gitignore` covers local secret files.
- [ ] Sample config files use placeholders only.

## Release Quality

- [ ] App starts without API key.
- [ ] Capture/import works.
- [ ] Save/search/delete works.
- [ ] Local tagging failure is recoverable.
- [ ] AI failure is recoverable.
- [ ] Dark mode checked.
- [ ] Accessibility basics checked.
- [ ] No private data in logs.

