#!/usr/bin/env bash
set -euo pipefail

wait_for_emulator() {
  adb wait-for-device

  local boot_completed=""
  local sdk_level=""
  for _ in $(seq 1 90); do
    boot_completed="$(adb shell getprop sys.boot_completed 2>/dev/null | tr -d '\r' || true)"
    sdk_level="$(adb shell getprop ro.build.version.sdk 2>/dev/null | tr -d '\r' || true)"
    if [[ "$boot_completed" == "1" && "$sdk_level" =~ ^[0-9]+$ && "$sdk_level" -ge 21 ]]; then
      adb shell cmd package list packages >/dev/null
      return 0
    fi
    sleep 5
  done

  echo "Emulator is not ready: boot=$boot_completed sdk=$sdk_level"
  adb devices -l || true
  return 1
}

retry_adb_install() {
  local apk_path="$1"

  for attempt in $(seq 1 5); do
    echo "Installing $apk_path (attempt $attempt/5)"
    if adb install -r "$apk_path"; then
      return 0
    fi
    adb shell cmd package list packages >/dev/null || true
    sleep 10
  done

  echo "Failed to install $apk_path after retries"
  return 1
}

wait_for_emulator
./gradlew :app:assembleDebugAndroidTest --stacktrace
retry_adb_install app/build/outputs/apk/debug/app-debug.apk
retry_adb_install app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
adb shell am instrument -w com.markscene.app.test/androidx.test.runner.AndroidJUnitRunner
adb shell am start -W -n com.markscene.app/.MainActivity
adb shell pidof com.markscene.app
