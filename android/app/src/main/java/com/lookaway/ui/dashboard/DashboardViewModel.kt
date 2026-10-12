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

import android.os.Build
import android.provider.Settings
import com.lookaway.data.AnimeCharacter

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

    private val _selectedCharacter = MutableStateFlow(
        AnimeCharacter.getOrDefault(
            id = _preferences.value.selectedAnimeCharacterId,
            customUrl = _preferences.value.customAnimeUrl
        )
    )
    val selectedCharacter: StateFlow<AnimeCharacter> = _selectedCharacter.asStateFlow()

    private val _canDrawOverlays = MutableStateFlow(checkOverlayPermission())
    val canDrawOverlays: StateFlow<Boolean> = _canDrawOverlays.asStateFlow()

    fun checkOverlayPermission(): Boolean {
        val app = getApplication<Application>()
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(app)
        } else {
            true
        }
    }

    fun refreshStats() {
        viewModelScope.launch {
            _dailyStats.value = statsRepository.getStatsForDate()
            val prefs = statsRepository.getPreferences()
            _preferences.value = prefs
            _selectedCharacter.value = AnimeCharacter.getOrDefault(
                id = prefs.selectedAnimeCharacterId,
                customUrl = prefs.customAnimeUrl
            )
            _canDrawOverlays.value = checkOverlayPermission()
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
        _selectedCharacter.value = AnimeCharacter.getOrDefault(
            id = newPreferences.selectedAnimeCharacterId,
            customUrl = newPreferences.customAnimeUrl
        )
        LookAwayService.reloadSettings(app)
    }

    fun selectAnimeCharacter(id: String, customUrl: String) {
        val current = preferences.value
        val updated = current.copy(
            selectedAnimeCharacterId = id,
            customAnimeUrl = customUrl
        )
        savePreferences(updated)
    }

    fun testFloatingOverlay() {
        val app = getApplication<Application>()
        LookAwayService.showTestOverlay(app)
    }
}
