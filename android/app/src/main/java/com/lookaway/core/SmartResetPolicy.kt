package com.lookaway.core

class SmartResetPolicy {

    /**
     * Determines whether the screen-off period was sufficient to count as an eye rest.
     *
     * @param screenOffTimestampMs System epoch millisecond when screen went off.
     * @param screenOnTimestampMs System epoch millisecond when screen turned on.
     * @param thresholdSeconds The minimum seconds the eyes must rest to reset (default 20 seconds).
     * @return true if screen was off for at least [thresholdSeconds], false otherwise.
     */
    fun shouldReset(
        screenOffTimestampMs: Long?,
        screenOnTimestampMs: Long,
        thresholdSeconds: Long = 20L
    ): Boolean {
        if (screenOffTimestampMs == null) return false
        val elapsedOffSeconds = (screenOnTimestampMs - screenOffTimestampMs) / 1000
        return elapsedOffSeconds >= thresholdSeconds
    }
}
