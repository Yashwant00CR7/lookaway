package com.lookaway.core

enum class SessionState {
    STOPPED,
    RUNNING,
    PAUSED,
    BREAK_PENDING,
    BREAK_ACTIVE
}

data class TimerSnapshot(
    val state: SessionState = SessionState.STOPPED,
    val workDurationSeconds: Int = 20 * 60,
    val breakDurationSeconds: Int = 20,
    val remainingWorkSeconds: Int = 20 * 60,
    val remainingBreakSeconds: Int = 20,
    val totalScreenTimeTodaySeconds: Long = 0L,
    val isScreenOn: Boolean = true
)
