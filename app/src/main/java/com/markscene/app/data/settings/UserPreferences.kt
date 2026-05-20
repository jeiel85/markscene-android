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

    /**
     * Whether the app should block screenshots globally (FLAG_SECURE on every screen).
     */
    fun isScreenshotBlockEnabled(): Boolean {
        return prefs.getBoolean(KEY_SCREENSHOT_BLOCK, false)
    }

    /**
     * Toggle global screenshot blocking. Default off so users opt in.
     */
    fun setScreenshotBlockEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SCREENSHOT_BLOCK, enabled).apply()
    }

    /**
     * Whether to strip EXIF metadata on export.
     */
    fun isExifStrippingEnabled(): Boolean {
        return prefs.getBoolean(KEY_EXIF_STRIPPING, true)
    }

    fun setExifStrippingEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_EXIF_STRIPPING, enabled).apply()
    }

    /**
     * Whether to hide MarkScene photos from system gallery.
     */
    fun isGalleryHidden(): Boolean {
        return prefs.getBoolean(KEY_GALLERY_HIDDEN, true)
    }

    fun setGalleryHidden(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_GALLERY_HIDDEN, enabled).apply()
    }

    /**
     * Whether to auto-lock the app when going to background.
     */
    fun isAutoLockEnabled(): Boolean {
        return prefs.getBoolean(KEY_AUTO_LOCK, false)
    }

    fun setAutoLockEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_LOCK, enabled).apply()
    }

    /**
     * Get preferred gallery layout type.
     */
    fun getPreferredLayout(): String {
        return prefs.getString(KEY_PREFERRED_LAYOUT, "GRID_2") ?: "GRID_2"
    }

    fun setPreferredLayout(layout: String) {
        prefs.edit().putString(KEY_PREFERRED_LAYOUT, layout).apply()
    }

    companion object {
        private const val PREFS_NAME = "markscene_prefs"
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        private const val KEY_TRUE_BLACK_MODE = "true_black_mode"
        private const val KEY_DYNAMIC_COLORS = "dynamic_colors"
        private const val KEY_RECENT_SEARCHES = "recent_searches"
        private const val KEY_SCREENSHOT_BLOCK = "screenshot_block"
        private const val KEY_EXIF_STRIPPING = "exif_stripping"
        private const val KEY_GALLERY_HIDDEN = "gallery_hidden"
        private const val KEY_AUTO_LOCK = "auto_lock"
        private const val KEY_PREFERRED_LAYOUT = "preferred_layout"
    }
}