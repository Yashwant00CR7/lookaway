package com.lookaway.core

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SmartResetPolicyTest {

    private val policy = SmartResetPolicy()

    @Test
    fun `should not reset when screen off timestamp is null`() {
        val result = policy.shouldReset(
            screenOffTimestampMs = null,
            screenOnTimestampMs = 100_000L,
            thresholdSeconds = 20L
        )
        assertFalse(result)
    }

    @Test
    fun `should not reset when screen was off for less than threshold`() {
        val offTime = 100_000L
        val onTime = 115_000L // 15 seconds off (< 20s)
        val result = policy.shouldReset(
            screenOffTimestampMs = offTime,
            screenOnTimestampMs = onTime,
            thresholdSeconds = 20L
        )
        assertFalse(result)
    }

    @Test
    fun `should reset when screen was off for exactly threshold`() {
        val offTime = 100_000L
        val onTime = 120_000L // exactly 20 seconds
        val result = policy.shouldReset(
            screenOffTimestampMs = offTime,
            screenOnTimestampMs = onTime,
            thresholdSeconds = 20L
        )
        assertTrue(result)
    }

    @Test
    fun `should reset when screen was off for longer than threshold`() {
        val offTime = 100_000L
        val onTime = 300_000L // 200 seconds off (> 20s)
        val result = policy.shouldReset(
            screenOffTimestampMs = offTime,
            screenOnTimestampMs = onTime,
            thresholdSeconds = 20L
        )
        assertTrue(result)
    }
}
