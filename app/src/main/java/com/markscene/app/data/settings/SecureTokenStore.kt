package com.markscene.app.data.settings

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SecureTokenStore(context: Context) {
    private val prefs = runCatching {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            "secure_settings",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }.getOrNull()

    fun isAvailable(): Boolean = prefs != null

    fun saveHuggingFaceToken(token: String): Boolean {
        return prefs?.let {
            runCatching {
                it.edit().putString(KEY_HF_TOKEN, token).commit()
            }.getOrDefault(false)
        } ?: false
    }

    fun getHuggingFaceToken(): String? {
        return prefs?.let {
            runCatching { it.getString(KEY_HF_TOKEN, null) }.getOrNull()
        }
    }

    fun clearHuggingFaceToken(): Boolean {
        return prefs?.let {
            runCatching {
                it.edit().remove(KEY_HF_TOKEN).commit()
            }.getOrDefault(false)
        } ?: false
    }

    fun clearLegacyExternalAiCredentials(): Boolean {
        return prefs?.let {
            runCatching {
                it.edit().remove(KEY_LEGACY_GEMINI).commit()
            }.getOrDefault(false)
        } ?: false
    }

    companion object {
        private const val KEY_HF_TOKEN = "huggingface_token"
        private const val KEY_LEGACY_GEMINI = "gemini_api_key"
    }
}
