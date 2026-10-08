package com.lookaway.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TimerStateMachineTest {

    private lateinit var stateMachine: TimerStateMachine
    private var completedBreaks = 0
    private var skippedBreaks = 0

    @Before
    fun setUp() {
        completedBreaks = 0
        skippedBreaks = 0
        stateMachine = TimerStateMachine(
            workDurationSeconds = 20 * 60,
            breakDurationSeconds = 20,
            onBreakCompleted = { completedBreaks++ },
            onBreakSkipped = { skippedBreaks++ }
        )
    }

    @Test
    fun `initial state is STOPPED with full work interval`() {
        val snapshot = stateMachine.snapshot
        assertEquals(SessionState.STOPPED, snapshot.state)
        assertEquals(1200, snapshot.remainingWorkSeconds)
        assertEquals(20, snapshot.remainingBreakSeconds)
    }

    @Test
    fun `start transitions to RUNNING and ticks decrement remaining work`() {
        stateMachine.start()
        assertEquals(SessionState.RUNNING, stateMachine.snapshot.state)

        stateMachine.tickSecond()
        assertEquals(1199, stateMachine.snapshot.remainingWorkSeconds)
        assertEquals(1L, stateMachine.snapshot.totalScreenTimeTodaySeconds)
    }

    @Test
    fun `screen off pauses timer and records screen off time`() {
        stateMachine.start()
        stateMachine.tickSecond() // 1199 left

        stateMachine.onScreenOff(100_000L)
        assertEquals(SessionState.PAUSED, stateMachine.snapshot.state)
        assertFalse(stateMachine.snapshot.isScreenOn)

        // Ticks while paused should NOT decrement remaining work
        stateMachine.tickSecond()
        assertEquals(1199, stateMachine.snapshot.remainingWorkSeconds)
    }

    @Test
    fun `screen on after brief off preserves remaining work`() {
        stateMachine.start()
        repeat(100) { stateMachine.tickSecond() } // 1100 left

        stateMachine.onScreenOff(100_000L)
        // Screen on after 10 seconds (under 20s threshold)
        stateMachine.onScreenOn(110_000L)

        assertEquals(SessionState.RUNNING, stateMachine.snapshot.state)
        assertTrue(stateMachine.snapshot.isScreenOn)
        assertEquals(1100, stateMachine.snapshot.remainingWorkSeconds)
    }

    @Test
    fun `screen on after 20 or more seconds off resets work timer`() {
        stateMachine.start()
        repeat(100) { stateMachine.tickSecond() } // 1100 left

        stateMachine.onScreenOff(100_000L)
        // Screen on after 25 seconds (>= 20s threshold)
        stateMachine.onScreenOn(125_000L)

        assertEquals(SessionState.RUNNING, stateMachine.snapshot.state)
        assertTrue(stateMachine.snapshot.isScreenOn)
        // Resets back to full 1200s (20 mins)
        assertEquals(1200, stateMachine.snapshot.remainingWorkSeconds)
    }

    @Test
    fun `work timer expiration triggers BREAK_PENDING`() {
        val shortMachine = TimerStateMachine(
            workDurationSeconds = 2,
            breakDurationSeconds = 20
        )
        shortMachine.start()
        shortMachine.tickSecond() // 1 left
        assertEquals(SessionState.RUNNING, shortMachine.snapshot.state)

        shortMachine.tickSecond() // 0 left -> triggers break
        assertEquals(SessionState.BREAK_PENDING, shortMachine.snapshot.state)
    }

    @Test
    fun `startBreak transitions to BREAK_ACTIVE and completes after breakDuration`() {
        val shortMachine = TimerStateMachine(
            workDurationSeconds = 1,
            breakDurationSeconds = 2,
            onBreakCompleted = { completedBreaks++ }
        )
        shortMachine.start()
        shortMachine.tickSecond() // BREAK_PENDING
        assertEquals(SessionState.BREAK_PENDING, shortMachine.snapshot.state)

        shortMachine.startBreak()
        assertEquals(SessionState.BREAK_ACTIVE, shortMachine.snapshot.state)
        assertEquals(2, shortMachine.snapshot.remainingBreakSeconds)

        shortMachine.tickSecond()
        assertEquals(1, shortMachine.snapshot.remainingBreakSeconds)
        assertEquals(0, completedBreaks)

        shortMachine.tickSecond() // Reaches 0
        assertEquals(SessionState.RUNNING, shortMachine.snapshot.state)
        assertEquals(1, completedBreaks)
        assertEquals(1, shortMachine.snapshot.remainingWorkSeconds)
    }

    @Test
    fun `skipBreak increments skipped count and returns to RUNNING`() {
        val shortMachine = TimerStateMachine(
            workDurationSeconds = 5,
            breakDurationSeconds = 20,
            onBreakSkipped = { skippedBreaks++ }
        )
        shortMachine.start()
        repeat(5) { shortMachine.tickSecond() }
        assertEquals(SessionState.BREAK_PENDING, shortMachine.snapshot.state)

        shortMachine.skipBreak()
        assertEquals(SessionState.RUNNING, shortMachine.snapshot.state)
        assertEquals(1, skippedBreaks)
        assertEquals(5, shortMachine.snapshot.remainingWorkSeconds)
    }

    @Test
    fun `snoozeBreak sets work countdown to snooze duration`() {
        val shortMachine = TimerStateMachine(
            workDurationSeconds = 5,
            breakDurationSeconds = 20
        )
        shortMachine.start()
        repeat(5) { shortMachine.tickSecond() }
        assertEquals(SessionState.BREAK_PENDING, shortMachine.snapshot.state)

        shortMachine.snoozeBreak(snoozeSeconds = 300)
        assertEquals(SessionState.RUNNING, shortMachine.snapshot.state)
        assertEquals(300, shortMachine.snapshot.remainingWorkSeconds)
    }
}
