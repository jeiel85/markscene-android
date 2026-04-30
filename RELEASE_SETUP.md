# MarkScene 릴리즈 서명 설정 가이드

## 개요

MarkScene 앱을 릴리즈용으로 빌드하여 설치 가능하게 하려면 APK 서명 설정이 필요합니다.

## 방법 1: 로컬에서 릴리즈 빌드 (권장)

### 1. 키스토어 생성

다음 명령어로 키스토어를 생성합니다:

```bash
keytool -genkey -valiity 10000 -keystore keystore/release-key.jks -alias markscene-release -keyalg RSA -keysize 2048
```

- 키스토어 위치: `D:\Project\markscene-android\keystore\release-key.jks`
- 이 디렉터리는 `.gitignore`에 포함되어 있어 저장소에 커밋되지 않습니다.

### 2. `local.properties` 설정

프로젝트 루트에 `local.properties` 파일을 생성하고 다음 내용을 추가합니다:

```properties
# 릴리즈 서명 설정
RELEASE_STORE_FILE=keystore/release-key.jks
RELEASE_STORE_PASSWORD=본인이_설정한_키스토어_비밀번호
RELEASE_KEY_ALIAS=markscene-release
RELEASE_KEY_PASSWORD=본인이_설정한_키_비밀번호
```

> **주의**: `local.properties`는 `.gitignore`에 포함되어 있어 저장소에 커밋되지 않습니다.

### 3. 릴리즈 빌드

```bash
./gradlew :app:assembleRelease
```

생성된 APK 위치: `app/build/outputs/apk/release/app-release.apk`

## 방법 2: GitHub Actions에서 자동 서명

### 1. GitHub Secrets 설정

GitHub 저장소 → Settings → Secrets and variables → Actions에서 다음 시크릿을 추가합니다:

| 시크릿 이름 | 값 |
|--------------|-----|
| RELEASE_KEYSTORE_BASE64 | 키스토어 파일을 Base64로 인코딩한 값 |
| RELEASE_KEYSTORE_PASSWORD | 키스토어 비밀번호 |
| RELEASE_KEY_ALIAS | markscene-release |
| RELEASE_KEY_PASSWORD | 키 비밀번호 |

Base64 인코딩 방법 (Windows PowerShell):
```powershell
[Convert]::ToBase64String([IO.File]::ReadBytes("keystore\release-key.jks")) | Set-Content "keystore_base64.txt"
```

### 2. 워크플로우 업데이트

`.github/workflows/release-apk.yml`에 서명 단계를 추가해야 합니다.

## 현재 설정 상태

- ✅ `keystore/` 디렉터리 → `.gitignore`에 포함됨
- ✅ `local.properties` → `.gitignore`에 포함됨  
- ✅ `build.gradle.kts`에 서명 설정 추가됨
- ⚠️ `local.properties` 파일 생성 필요 (로컬 빌드 시)
- ⚠️ 키스토어 생성 필요

## 문제 해결

### "invalid" 에러가 발생하는 경우

1. **키스토어가 없거나 경로가 틀린 경우**
   - `local.properties`의 `RELEASE_STORE_FILE` 경로를 확인하세요.
   - 상대 경로는 프로젝트 루트 기준입니다.

2. **비밀번호가 틀린 경우**
   - `RELEASE_STORE_PASSWORD`와 `RELEASE_KEY_PASSWORD`가 정확한지 확인하세요.

3. **서명되지 않은 APK를 설치하려는 경우**
   - `assembleRelease`로 빌드하고 서명 설정이 적용되었는지 확인하세요.
   - `app/build/outputs/apk/release/`에서 `-unsigned`가 포함된 파일은 설치할 수 없습니다.

## 참고

- Android 공식 문서: [App Signing](https://developer.android.com/studio/publish/app-signing)
- 키스토어는 안전한 곳에 백업해 두세요!
- 릴리즈 키를 잃어버리면 업데이트용 APK를 만들 수 없습니다.
