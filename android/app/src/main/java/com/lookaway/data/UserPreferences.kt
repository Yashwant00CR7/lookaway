package com.lookaway.data

data class UserPreferences(
    val workDurationMinutes: Int = 20,
    val breakDurationSeconds: Int = 20,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true
) {
    val workDurationSeconds: Int
        get() = workDurationMinutes * 60
}

data class DailyStats(
    val date: String,
    val breaksCompleted: Int = 0,
    val breaksSkipped: Int = 0,
    val screenTimeSeconds: Long = 0L
)
