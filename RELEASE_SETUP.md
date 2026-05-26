# MarkScene 릴리즈 서명 설정 가이드

## 개요

MarkScene 앱을 릴리즈용으로 빌드하여 설치 가능하게 하려면 APK/AAB 서명 설정이 필요합니다.

## 방법 1: 로컬에서 릴리즈 빌드 (권장)

### 1. 키스토어 복원

원본 키스토어 백업이 `.keystore/` 디렉터리에 보관되어 있습니다.

- `base64.txt` — 키스토어를 Base64로 인코딩한 값
- `markscene.txt` — alias 및 비밀번호 정보

복원 방법 (PowerShell):
```powershell
$lines = Get-Content ".keystore\base64.txt"
$dataLines = $lines[2..($lines.Count-1)]
$base64 = ($dataLines -join '') -replace '\s',''
$bytes = [Convert]::FromBase64String($base64)
[IO.File]::WriteAllBytes("keystore\release.keystore", $bytes)
```

- 키스토어 위치: `keystore/release.keystore`
- 이 디렉터리는 `.gitignore`에 포함되어 있어 저장소에 커밋되지 않습니다.

### 2. `local.properties` 설정

프로젝트 루트에 `local.properties` 파일을 생성하고 다음 내용을 추가합니다:

```properties
# 릴리즈 서명 설정 (경로는 app 모듈 기준 상대 경로)
RELEASE_STORE_FILE=../keystore/release.keystore
RELEASE_STORE_PASSWORD=<.keystore/markscene.txt 참고>
RELEASE_KEY_ALIAS=markscene
RELEASE_KEY_PASSWORD=<.keystore/markscene.txt 참고>
```

> **주의**: `local.properties`는 `.gitignore`에 포함되어 있어 저장소에 커밋되지 않습니다.
> **경로 주의**: `build.gradle.kts`의 `file()` 함수는 `app` 모듈 기준 상대 경로를 사용하므로 `../keystore/`로 지정합니다.

### 3. 릴리즈 빌드

```bash
# AAB (Play Store 업로드용)
./gradlew :app:bundleRelease

# APK (직접 설치용)
./gradlew :app:assembleRelease
```

생성 위치:
- AAB: `app/build/outputs/bundle/release/app-release.aab`
- APK: `app/build/outputs/apk/release/app-release.apk`

## 방법 2: GitHub Actions에서 자동 서명

### 1. GitHub Secrets 설정

GitHub 저장소 → Settings → Secrets and variables → Actions에서 다음 시크릿을 추가합니다:

| 시크릿 이름 | 값 |
|--------------|-----|
| RELEASE_KEYSTORE | `.keystore/base64.txt` 내용 (Base64 데이터만) |
| RELEASE_STORE_PASSWORD | 키스토어 비밀번호 |
| RELEASE_KEY_ALIAS | markscene |
| RELEASE_KEY_PASSWORD | 키 비밀번호 |

### 2. 워크플로우 동작 방식

`release-apk.yml`은 태그 푸시 시 `RELEASE_KEYSTORE`를 `app/release.jks`로 복원하고,
`RELEASE_STORE_FILE=release.jks` 환경 변수로 Gradle release signingConfig에 전달합니다.

`app/build.gradle.kts`가 `app` 모듈 안에 있으므로, 여기서의 상대 경로 `release.jks`는
저장소 루트 기준 `app/release.jks`를 가리킵니다.

따라서 GitHub Actions에서 사용하는 secret 이름과 경로는 아래 기준으로 맞아야 합니다.

```text
RELEASE_KEYSTORE        -> Base64 인코딩된 keystore 원문
RELEASE_STORE_PASSWORD  -> keystore 비밀번호
RELEASE_KEY_ALIAS       -> markscene
RELEASE_KEY_PASSWORD    -> key 비밀번호
RELEASE_STORE_FILE      -> release.jks (app 모듈 기준 상대 경로)
```

## 로컬 VLM 모델 설정

MarkScene은 기본적으로 Google Gemma 3n E2B INT4 LiteRT-LM 모델(약 3.66GB)을 로컬 고급 AI로 사용합니다.

### 기본 빌드 동작

`app/build.gradle.kts`의 기본값으로 다음 BuildConfig가 생성됩니다.

```text
LOCAL_VLM_MODEL_URL         = https://huggingface.co/google/gemma-3n-E2B-it-litert-lm/resolve/main/gemma-3n-E2B-it-int4.litertlm
LOCAL_VLM_MODEL_NAME        = Gemma 3n E2B (INT4)
LOCAL_VLM_MODEL_FILENAME    = gemma-3n-E2B-it-int4.litertlm
LOCAL_VLM_MODEL_SIZE_MB     = 3660
LOCAL_VLM_MODEL_LICENSE_URL = https://huggingface.co/google/gemma-3n-E2B-it-litert-lm
```

### 다른 MediaPipe 호환 모델로 교체

`local.properties` 또는 환경 변수로 override 할 수 있습니다.

```properties
MARKSCENE_LOCAL_VLM_MODEL_URL=https://...your-mediapipe-compatible-.task-or-.litertlm-url
MARKSCENE_LOCAL_VLM_MODEL_NAME=My Custom VLM
MARKSCENE_LOCAL_VLM_MODEL_FILENAME=my-model.litertlm
MARKSCENE_LOCAL_VLM_MODEL_SIZE_MB=1500
MARKSCENE_LOCAL_VLM_MODEL_LICENSE_URL=https://...license-page (선택)
```

### 사용자(end user) 사전 준비

기본 Gemma 모델은 HuggingFace 라이선스 게이트가 있어 사용자가 직접 다음 단계를 수행해야 합니다.

1. https://huggingface.co/google/gemma-3n-E2B-it-litert-lm 접속 후 라이선스 수락
2. https://huggingface.co/settings/tokens 에서 read 권한 토큰(예: `hf_...`) 발급
3. MarkScene 앱 → 설정 → "HuggingFace 토큰" 필드에 입력 후 저장
4. "모델 다운로드" 클릭 (WiFi 권장, 약 3.66GB)
5. 다운로드 완료 후 사진 상세 화면에서 "로컬 AI로 자세히 분석하기" 실행

토큰은 EncryptedSharedPreferences로 기기 내부에만 암호화 저장됩니다. 모델 다운로드 시 Authorization Bearer 헤더로만 전송되고, 그 외 외부 서버로 전달되지 않습니다.

### 디바이스 요구 사항

- Android 8.0 (API 26) 이상
- 약 4GB 이상의 사용 가능 RAM (Gemma 3n E2B INT4 추론 기준)
- 약 4GB 이상의 빈 저장 공간 (다운로드 + 임시 파일)

저사양 기기에서는 추론 시 OOM 또는 매우 느린 응답이 발생할 수 있습니다. 이 경우 사용자는 모델을 삭제하고 외부 BYOK Gemini 경로를 사용할 수 있습니다.

## 현재 설정 상태

- ✅ `keystore/` 디렉터리 → `.gitignore`에 포함됨
- ✅ `.keystore/` 백업 디렉터리 → `.gitignore`에 포함됨
- ✅ `local.properties` → `.gitignore`에 포함됨
- ✅ `build.gradle.kts`에 서명 설정 추가됨
- ✅ `keystore/release.keystore` 복원 완료
- ✅ `local.properties` 설정 완료

## 문제 해결

### "Keystore file not found" 에러

1. **경로가 틀린 경우**
   - `local.properties`의 `RELEASE_STORE_FILE`이 `../keystore/release.keystore`인지 확인하세요.
   - `build.gradle.kts`의 `file()` 함수는 `app` 모듈 기준 상대 경로를 사용합니다.

2. **비밀번호가 틀린 경우**
   - `.keystore/markscene.txt`의 비밀번호와 일치하는지 확인하세요.

3. **서명되지 않은 APK를 설치하려는 경우**
   - `assembleRelease`로 빌드하고 서명 설정이 적용되었는지 확인하세요.
   - `app/build/outputs/apk/release/`에서 `-unsigned`가 포함된 파일은 설치할 수 없습니다.

### R8 빌드 에러

MediaPipe 관련 protobuf 클래스 누락 에러 발생 시 `proguard-rules.pro`에 아래 규칙이 있는지 확인:
```
-dontwarn com.google.protobuf.Internal$ProtoMethodMayReturnNull
-dontwarn com.google.protobuf.Internal$ProtoNonnullApi
-dontwarn com.google.protobuf.ProtoField
-dontwarn com.google.protobuf.ProtoPresenceBits
-dontwarn com.google.protobuf.ProtoPresenceCheckedField
```

## 참고

- Android 공식 문서: [App Signing](https://developer.android.com/studio/publish/app-signing)
- 키스토어는 안전한 곳에 백업해 두세요!
- 릴리즈 키를 잃어버리면 업데이트용 APK를 만들 수 없습니다.
