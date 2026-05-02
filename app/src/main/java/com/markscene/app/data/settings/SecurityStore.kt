package com.markscene.app.data.settings

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class SecurityStore(context: Context) {
    private val prefs = runCatching {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        EncryptedSharedPreferences.create(
            "security_prefs",
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }.getOrNull()

    fun isAvailable(): Boolean = prefs != null

    fun isBiometricLockEnabled(): Boolean {
        return prefs?.let {
            runCatching { it.getBoolean(KEY_BIOMETRIC_LOCK, false) }.getOrDefault(false)
        } ?: false
    }

    fun setBiometricLockEnabled(enabled: Boolean): Boolean {
        return prefs?.let {
            runCatching {
                it.edit().putBoolean(KEY_BIOMETRIC_LOCK, enabled).commit()
            }.getOrDefault(false)
        } ?: false
    }

    companion object {
        private const val KEY_BIOMETRIC_LOCK = "biometric_lock"
    }
}
