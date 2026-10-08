package com.lookaway.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class StatsRepositoryTest {

    private class InMemoryStore : KeyValueStore {
        private val data = mutableMapOf<String, Any>()

        override fun getInt(key: String, defValue: Int): Int =
            (data[key] as? Int) ?: defValue

        override fun putInt(key: String, value: Int) {
            data[key] = value
        }

        override fun getLong(key: String, defValue: Long): Long =
            (data[key] as? Long) ?: defValue

        override fun putLong(key: String, value: Long) {
            data[key] = value
        }

        override fun getBoolean(key: String, defValue: Boolean): Boolean =
            (data[key] as? Boolean) ?: defValue

        override fun putBoolean(key: String, value: Boolean) {
            data[key] = value
        }

        override fun getString(key: String, defValue: String?): String? =
            (data[key] as? String) ?: defValue

        override fun putString(key: String, value: String) {
            data[key] = value
        }
    }

    private lateinit var store: InMemoryStore
    private lateinit var repository: StatsRepository

    @Before
    fun setUp() {
        store = InMemoryStore()
        repository = StatsRepository(store)
    }

    @Test
    fun `default preferences are 20 minutes work, 20 seconds break, audio and vibration on, tracking enabled`() {
        val prefs = repository.getPreferences()
        assertEquals(20, prefs.workDurationMinutes)
        assertEquals(20, prefs.breakDurationSeconds)
        assertTrue(prefs.soundEnabled)
        assertTrue(prefs.vibrationEnabled)
        assertTrue(prefs.trackingEnabled)
    }

    @Test
    fun `updating preferences persists and updates values`() {
        val updated = UserPreferences(
            workDurationMinutes = 25,
            breakDurationSeconds = 30,
            soundEnabled = false,
            vibrationEnabled = true,
            trackingEnabled = false
        )
        repository.updatePreferences(updated)

        val retrieved = repository.getPreferences()
        assertEquals(25, retrieved.workDurationMinutes)
        assertEquals(30, retrieved.breakDurationSeconds)
        assertEquals(false, retrieved.soundEnabled)
        assertEquals(true, retrieved.vibrationEnabled)
        assertEquals(false, retrieved.trackingEnabled)
    }

    @Test
    fun `records break completed and updates count for target date`() {
        val date = "2026-10-08"
        assertEquals(0, repository.getStatsForDate(date).breaksCompleted)

        repository.recordBreakCompleted(date)
        repository.recordBreakCompleted(date)

        val stats = repository.getStatsForDate(date)
        assertEquals(2, stats.breaksCompleted)
    }

    @Test
    fun `records break skipped and updates count for target date`() {
        val date = "2026-10-08"
        assertEquals(0, repository.getStatsForDate(date).breaksSkipped)

        repository.recordBreakSkipped(date)

        val stats = repository.getStatsForDate(date)
        assertEquals(1, stats.breaksSkipped)
    }

    @Test
    fun `tracks screen time per date`() {
        val date = "2026-10-08"
        repository.addScreenTime(120L, date)
        repository.addScreenTime(60L, date)

        val stats = repository.getStatsForDate(date)
        assertEquals(180L, stats.screenTimeSeconds)
    }

    @Test
    fun `stats are isolated across distinct dates`() {
        repository.recordBreakCompleted("2026-10-08")
        repository.recordBreakCompleted("2026-10-09")
        repository.recordBreakCompleted("2026-10-09")

        assertEquals(1, repository.getStatsForDate("2026-10-08").breaksCompleted)
        assertEquals(2, repository.getStatsForDate("2026-10-09").breaksCompleted)
    }
}
