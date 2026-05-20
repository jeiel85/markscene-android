> GitHub Issue: [#17](https://github.com/jeiel85/markscene-android/issues/17)

## Security, Privacy & Trust (보안, 프라이버시 및 신뢰) 전략

플레이 스토어 Top 10 진입을 위해 사용자의 민감한 데이터를 보호하고 신뢰를 구축하는 10가지 전략입니다.

- [x] **[Sec] 생체 인증(Biometrics) 잠금 기능**: 민감한 시각적 메모 보호를 위해 앱 실행 시 지문/얼굴 인식을 요구하는 프라이버시 잠금 옵션 제공. (v2.5.0 - 백그라운드 자동 잠금 추가)
- [x] **[Sec] 갤러리 숨김(No Media) 모드**: MarkScene에서 찍은 사진이 시스템 기본 갤러리나 다른 앱에 노출되지 않도록 .nomedia 파일 생성. (v2.5.0)
- [x] **[Sec] API Key 암호화 스토리지 강화**: Android Keystore 기반 EncryptedSharedPreferences + allowBackup=false + auto-backup 방지. (v2.5.0)
- [x] **[Privacy] 권한 요청 시나리오 투명화**: (v2.3.0)
- [x] **[Trust] 로컬 처리 인증 배지 UI**: (v2.3.0)
- [x] **[Privacy] EXIF 메타데이터 제거 옵션**: 내보내기 시 위치정보 등 민감한 EXIF 데이터를 자동 삭제하는 토글. (v2.5.0)
- [ ] **[Trust] 투명한 데이터 내보내기/가져오기 (JSON/ZIP)**: (BackupManager 존재, ZIP/CSV/MD 내보내기 구현됨)
- [x] **[Sec] 클립보드 보호**: API Key 입력 필드 마스킹 + 자동완성 차단 + 표시/숨김 토글. (v2.3.0)
- [x] **[Trust] 프라이버시 영수증(Privacy Dashboard)**: (v2.2.0)
- [x] **[Sec] 스크린샷 방지 옵션**: (v2.2.0)
