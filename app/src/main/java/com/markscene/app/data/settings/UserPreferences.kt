package com.markscene.app.data.settings

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages user preferences including onboarding state
 */
class UserPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * Returns true if onboarding has been completed
     */
    fun isOnboardingCompleted(): Boolean {
        return prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }

    /**
     * Mark onboarding as completed
     */
    fun setOnboardingCompleted(completed: Boolean) {
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, completed).apply()
    }

    /**
     * Check if user prefers OLED black dark mode
     */
    fun useTrueBlackDarkMode(): Boolean {
        return prefs.getBoolean(KEY_TRUE_BLACK_MODE, false)
    }

    /**
     * Set OLED black dark mode preference
     */
    fun setTrueBlackDarkMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_TRUE_BLACK_MODE, enabled).apply()
    }

    /**
     * Check if Material You dynamic colors are enabled
     */
    fun useDynamicColors(): Boolean {
        return prefs.getBoolean(KEY_DYNAMIC_COLORS, false)
    }

    /**
     * Set Material You dynamic colors preference
     */
    fun setDynamicColors(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DYNAMIC_COLORS, enabled).apply()
    }

    /**
     * Get recent search queries (stored as comma-separated string)
     */
    fun getRecentSearches(): List<String> {
        val saved = prefs.getString(KEY_RECENT_SEARCHES, "") ?: ""
        return if (saved.isBlank()) emptyList()
        else saved.split("\n").filter { it.isNotBlank() }
    }

    /**
     * Add a search query to recent searches (max 10)
     */
    fun addRecentSearch(query: String) {
        if (query.isBlank()) return
        val current = getRecentSearches().toMutableList()
        current.remove(query) // Remove if exists to move to top
        current.add(0, query)
        val limited = current.take(10) // Keep only 10
        prefs.edit().putString(KEY_RECENT_SEARCHES, limited.joinToString("\n")).apply()
    }

    /**
     * Clear all recent searches
     */
    fun clearRecentSearches() {
        prefs.edit().remove(KEY_RECENT_SEARCHES).apply()
    }

    companion object {
        private const val PREFS_NAME = "markscene_prefs"
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        private const val KEY_TRUE_BLACK_MODE = "true_black_mode"
        private const val KEY_DYNAMIC_COLORS = "dynamic_colors"
        private const val KEY_RECENT_SEARCHES = "recent_searches"
    }
}