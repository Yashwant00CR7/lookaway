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
            vibrationEnabled = store.getBoolean(KEY_VIBRATION_ENABLED, true)
        )
    }

    fun updatePreferences(preferences: UserPreferences) {
        store.putInt(KEY_WORK_DURATION_MIN, preferences.workDurationMinutes)
        store.putInt(KEY_BREAK_DURATION_SEC, preferences.breakDurationSeconds)
        store.putBoolean(KEY_SOUND_ENABLED, preferences.soundEnabled)
        store.putBoolean(KEY_VIBRATION_ENABLED, preferences.vibrationEnabled)
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
