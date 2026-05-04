package com.markscene.app.ui.util

import android.app.Activity
import android.content.Context
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * In-App Review helper for Google Play Store
 * Shows review dialog when users have positive engagement
 */
object ReviewHelper {

    private const val PREFS_NAME = "review_prefs"
    private const val KEY_LAST_REVIEW_REQUEST = "last_review_request"
    private const val KEY_LAUNCH_COUNT = "launch_count"
    private const val KEY_RECORD_COUNT_AT_LAST_REVIEW = "record_count_at_review"
    private const val MIN_DAYS_BETWEEN_REVIEWS = 30
    private const val MIN_LAUNCH_COUNT = 5
    private const val MIN_RECORDS_SINCE_LAST_REVIEW = 10

    /**
     * Check if review should be requested
     */
    fun shouldRequestReview(context: Context, currentRecordCount: Int): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lastRequestTime = prefs.getLong(KEY_LAST_REVIEW_REQUEST, 0)
        val launchCount = prefs.getInt(KEY_LAUNCH_COUNT, 0)
        val recordCountAtLastReview = prefs.getInt(KEY_RECORD_COUNT_AT_LAST_REVIEW, 0)

        // Increment launch count
        prefs.edit().putInt(KEY_LAUNCH_COUNT, launchCount + 1).apply()

        // Check minimum requirements
        if (launchCount < MIN_LAUNCH_COUNT) return false
        if (currentRecordCount - recordCountAtLastReview < MIN_RECORDS_SINCE_LAST_REVIEW) return false

        // Check time since last review
        val daysSinceLastReview = (System.currentTimeMillis() - lastRequestTime) / (1000 * 60 * 60 * 24)
        if (daysSinceLastReview < MIN_DAYS_BETWEEN_REVIEWS) return false

        return true
    }

    /**
     * Request in-app review
     */
    suspend fun requestReview(activity: Activity, currentRecordCount: Int): Boolean = withContext(Dispatchers.Main) {
        try {
            val reviewManager = ReviewManagerFactory.create(activity)
            val reviewInfo = reviewManager.requestReviewFlow().await()

            reviewManager.launchReviewFlow(activity, reviewInfo).await()

            // Update preferences
            val prefs = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit()
                .putLong(KEY_LAST_REVIEW_REQUEST, System.currentTimeMillis())
                .putInt(KEY_RECORD_COUNT_AT_LAST_REVIEW, currentRecordCount)
                .apply()

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}