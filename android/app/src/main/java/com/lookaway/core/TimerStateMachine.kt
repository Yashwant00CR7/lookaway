package com.lookaway.core

class TimerStateMachine(
    val workDurationSeconds: Int = 20 * 60,
    val breakDurationSeconds: Int = 20,
    private val smartResetPolicy: SmartResetPolicy = SmartResetPolicy(),
    private val onBreakCompleted: () -> Unit = {},
    private val onBreakSkipped: () -> Unit = {},
    private val onBreakTriggered: () -> Unit = {}
) {

    private var state: SessionState = SessionState.STOPPED
    private var remainingWorkSeconds: Int = workDurationSeconds
    private var remainingBreakSeconds: Int = breakDurationSeconds
    private var totalScreenTimeTodaySeconds: Long = 0L
    private var isScreenOn: Boolean = true
    private var screenOffTimestampMs: Long? = null

    val snapshot: TimerSnapshot
        get() = TimerSnapshot(
            state = state,
            workDurationSeconds = workDurationSeconds,
            breakDurationSeconds = breakDurationSeconds,
            remainingWorkSeconds = remainingWorkSeconds,
            remainingBreakSeconds = remainingBreakSeconds,
            totalScreenTimeTodaySeconds = totalScreenTimeTodaySeconds,
            isScreenOn = isScreenOn
        )

    fun start() {
        if (state == SessionState.STOPPED || state == SessionState.PAUSED) {
            state = SessionState.RUNNING
        }
    }

    fun stop() {
        state = SessionState.STOPPED
        remainingWorkSeconds = workDurationSeconds
        remainingBreakSeconds = breakDurationSeconds
        screenOffTimestampMs = null
    }

    fun tickSecond() {
        when (state) {
            SessionState.RUNNING -> {
                if (isScreenOn) {
                    totalScreenTimeTodaySeconds++
                    remainingWorkSeconds--
                    if (remainingWorkSeconds <= 0) {
                        state = SessionState.BREAK_PENDING
                        onBreakTriggered()
                    }
                }
            }
            SessionState.BREAK_ACTIVE -> {
                remainingBreakSeconds--
                if (remainingBreakSeconds <= 0) {
                    // Break completed successfully!
                    state = SessionState.RUNNING
                    remainingWorkSeconds = workDurationSeconds
                    remainingBreakSeconds = breakDurationSeconds
                    onBreakCompleted()
                }
            }
            SessionState.STOPPED,
            SessionState.PAUSED,
            SessionState.BREAK_PENDING -> {
                // No tick updates while stopped, paused, or awaiting break response
            }
        }
    }

    fun onScreenOff(timestampMs: Long) {
        isScreenOn = false
        screenOffTimestampMs = timestampMs
        if (state == SessionState.RUNNING) {
            state = SessionState.PAUSED
        }
    }

    fun onScreenOn(timestampMs: Long) {
        isScreenOn = true
        if (state == SessionState.PAUSED) {
            val shouldReset = smartResetPolicy.shouldReset(
                screenOffTimestampMs = screenOffTimestampMs,
                screenOnTimestampMs = timestampMs,
                thresholdSeconds = breakDurationSeconds.toLong()
            )
            if (shouldReset) {
                remainingWorkSeconds = workDurationSeconds
            }
            state = SessionState.RUNNING
        }
        screenOffTimestampMs = null
    }

    fun startBreak() {
        if (state == SessionState.BREAK_PENDING || state == SessionState.RUNNING) {
            state = SessionState.BREAK_ACTIVE
            remainingBreakSeconds = breakDurationSeconds
        }
    }

    fun skipBreak() {
        state = SessionState.RUNNING
        remainingWorkSeconds = workDurationSeconds
        remainingBreakSeconds = breakDurationSeconds
        onBreakSkipped()
    }

    fun snoozeBreak(snoozeSeconds: Int = 5 * 60) {
        state = SessionState.RUNNING
        remainingWorkSeconds = snoozeSeconds
        remainingBreakSeconds = breakDurationSeconds
    }
}
