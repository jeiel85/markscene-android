package com.markscene.app.data.settings

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class ApiKeyStore(context: Context) {
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

    fun saveGeminiApiKey(apiKey: String): Boolean {
        return prefs?.let {
            runCatching {
                it.edit().putString(KEY_GEMINI, apiKey).commit()
            }.getOrDefault(false)
        } ?: false
    }

    fun getGeminiApiKey(): String? {
        return prefs?.let {
            runCatching { it.getString(KEY_GEMINI, null) }.getOrNull()
        }
    }

    fun clearGeminiApiKey(): Boolean {
        return prefs?.let {
            runCatching {
                it.edit().remove(KEY_GEMINI).commit()
            }.getOrDefault(false)
        } ?: false
    }

    companion object {
        private const val KEY_GEMINI = "gemini_api_key"
    }
}
