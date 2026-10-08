package com.lookaway.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lookaway.core.SessionState
import com.lookaway.core.TimerSnapshot
import com.lookaway.data.DailyStats
import com.lookaway.data.SharedPrefsKeyValueStore
import com.lookaway.data.StatsRepository
import com.lookaway.data.UserPreferences
import com.lookaway.service.LookAwayService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val statsRepository = StatsRepository(SharedPrefsKeyValueStore(application))

    val timerState: StateFlow<TimerSnapshot> = LookAwayService.timerState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TimerSnapshot()
        )

    private val _dailyStats = MutableStateFlow(statsRepository.getStatsForDate())
    val dailyStats: StateFlow<DailyStats> = _dailyStats.asStateFlow()

    private val _preferences = MutableStateFlow(statsRepository.getPreferences())
    val preferences: StateFlow<UserPreferences> = _preferences.asStateFlow()

    fun refreshStats() {
        viewModelScope.launch {
            _dailyStats.value = statsRepository.getStatsForDate()
            _preferences.value = statsRepository.getPreferences()
        }
    }

    fun toggleTracking() {
        val app = getApplication<Application>()
        if (timerState.value.state == SessionState.STOPPED) {
            LookAwayService.startService(app)
        } else {
            LookAwayService.stopService(app)
        }
    }

    fun autoStartIfEnabled() {
        val app = getApplication<Application>()
        if (preferences.value.trackingEnabled && timerState.value.state == SessionState.STOPPED) {
            LookAwayService.startService(app)
        }
    }

    fun savePreferences(newPreferences: UserPreferences) {
        val app = getApplication<Application>()
        statsRepository.updatePreferences(newPreferences)
        _preferences.value = newPreferences
        LookAwayService.reloadSettings(app)
    }
}
