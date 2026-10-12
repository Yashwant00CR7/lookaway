package com.lookaway.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StatsRepository(private val store: KeyValueStore) {

    companion object {
        private const val KEY_WORK_DURATION_MIN = "pref_work_duration_min"
        private const val KEY_BREAK_DURATION_SEC = "pref_break_duration_sec"
        private const val KEY_SOUND_ENABLED = "pref_sound_enabled"
        private const val KEY_VIBRATION_ENABLED = "pref_vibration_enabled"
        private const val KEY_TRACKING_ENABLED = "pref_tracking_enabled"
        private const val KEY_ANIME_ID = "pref_anime_id"
        private const val KEY_CUSTOM_ANIME_URL = "pref_custom_anime_url"
        private const val KEY_USE_FLOATING_OVERLAY = "pref_use_floating_overlay"

        private const val PREFIX_COMPLETED = "stats_completed_"
        private const val PREFIX_SKIPPED = "stats_skipped_"
        private const val PREFIX_SCREEN_TIME = "stats_screen_time_"
    }

    fun todayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(Date())
    }

    fun getPreferences(): UserPreferences {
        return UserPreferences(
            workDurationMinutes = store.getInt(KEY_WORK_DURATION_MIN, 20),
            breakDurationSeconds = store.getInt(KEY_BREAK_DURATION_SEC, 20),
            soundEnabled = store.getBoolean(KEY_SOUND_ENABLED, true),
            vibrationEnabled = store.getBoolean(KEY_VIBRATION_ENABLED, true),
            trackingEnabled = store.getBoolean(KEY_TRACKING_ENABLED, true),
            selectedAnimeCharacterId = store.getString(KEY_ANIME_ID, AnimeCharacter.DEFAULT_CHARACTER_ID)
                ?: AnimeCharacter.DEFAULT_CHARACTER_ID,
            customAnimeUrl = store.getString(KEY_CUSTOM_ANIME_URL, "") ?: "",
            useFloatingOverlay = store.getBoolean(KEY_USE_FLOATING_OVERLAY, true)
        )
    }

    fun updatePreferences(preferences: UserPreferences) {
        store.putInt(KEY_WORK_DURATION_MIN, preferences.workDurationMinutes)
        store.putInt(KEY_BREAK_DURATION_SEC, preferences.breakDurationSeconds)
        store.putBoolean(KEY_SOUND_ENABLED, preferences.soundEnabled)
        store.putBoolean(KEY_VIBRATION_ENABLED, preferences.vibrationEnabled)
        store.putBoolean(KEY_TRACKING_ENABLED, preferences.trackingEnabled)
        store.putString(KEY_ANIME_ID, preferences.selectedAnimeCharacterId)
        store.putString(KEY_CUSTOM_ANIME_URL, preferences.customAnimeUrl)
        store.putBoolean(KEY_USE_FLOATING_OVERLAY, preferences.useFloatingOverlay)
    }

    fun getStatsForDate(date: String = todayDateString()): DailyStats {
        val completed = store.getInt("$PREFIX_COMPLETED$date", 0)
        val skipped = store.getInt("$PREFIX_SKIPPED$date", 0)
        val screenTime = store.getLong("$PREFIX_SCREEN_TIME$date", 0L)
        return DailyStats(
            date = date,
            breaksCompleted = completed,
            breaksSkipped = skipped,
            screenTimeSeconds = screenTime
        )
    }

    fun recordBreakCompleted(date: String = todayDateString()) {
        val current = store.getInt("$PREFIX_COMPLETED$date", 0)
        store.putInt("$PREFIX_COMPLETED$date", current + 1)
    }

    fun recordBreakSkipped(date: String = todayDateString()) {
        val current = store.getInt("$PREFIX_SKIPPED$date", 0)
        store.putInt("$PREFIX_SKIPPED$date", current + 1)
    }

    fun addScreenTime(seconds: Long, date: String = todayDateString()) {
        val current = store.getLong("$PREFIX_SCREEN_TIME$date", 0L)
        store.putLong("$PREFIX_SCREEN_TIME$date", current + seconds)
    }
}
