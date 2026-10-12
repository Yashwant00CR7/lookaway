package com.lookaway.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import com.lookaway.core.SessionState
import com.lookaway.core.TimerSnapshot
import com.lookaway.core.TimerStateMachine
import com.lookaway.data.SharedPrefsKeyValueStore
import com.lookaway.data.StatsRepository
import com.lookaway.ui.sound.SoundManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class LookAwayService : Service() {

    companion object {
        const val ACTION_START = "com.lookaway.action.START"
        const val ACTION_STOP = "com.lookaway.action.STOP"
        const val ACTION_START_BREAK = "com.lookaway.action.START_BREAK"
        const val ACTION_SKIP_BREAK = "com.lookaway.action.SKIP_BREAK"
        const val ACTION_SNOOZE_BREAK = "com.lookaway.action.SNOOZE_BREAK"
        const val ACTION_RELOAD_SETTINGS = "com.lookaway.action.RELOAD_SETTINGS"
        const val ACTION_SHOW_TEST_OVERLAY = "com.lookaway.action.SHOW_TEST_OVERLAY"

        private val _timerState = MutableStateFlow(TimerSnapshot())
        val timerState: StateFlow<TimerSnapshot> = _timerState.asStateFlow()

        fun startService(context: Context) {
            val intent = Intent(context, LookAwayService::class.java).apply {
                action = ACTION_START
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun showTestOverlay(context: Context) {
            val intent = Intent(context, LookAwayService::class.java).apply {
                action = ACTION_SHOW_TEST_OVERLAY
            }
            context.startService(intent)
        }

        fun stopService(context: Context) {
            val intent = Intent(context, LookAwayService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }

        fun skipBreak(context: Context) {
            val intent = Intent(context, LookAwayService::class.java).apply {
                action = ACTION_SKIP_BREAK
            }
            context.startService(intent)
        }

        fun snoozeBreak(context: Context) {
            val intent = Intent(context, LookAwayService::class.java).apply {
                action = ACTION_SNOOZE_BREAK
            }
            context.startService(intent)
        }

        fun startBreak(context: Context) {
            val intent = Intent(context, LookAwayService::class.java).apply {
                action = ACTION_START_BREAK
            }
            context.startService(intent)
        }

        fun reloadSettings(context: Context) {
            val intent = Intent(context, LookAwayService::class.java).apply {
                action = ACTION_RELOAD_SETTINGS
            }
            context.startService(intent)
        }
    }

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var tickerJob: Job? = null

    private lateinit var notificationHelper: NotificationHelper
    private lateinit var statsRepository: StatsRepository
    private lateinit var soundManager: SoundManager
    private lateinit var floatingOverlayManager: FloatingOverlayManager
    private var screenReceiver: ScreenBroadcastReceiver? = null

    private lateinit var stateMachine: TimerStateMachine

    override fun onCreate() {
        super.onCreate()
        notificationHelper = NotificationHelper(this)
        statsRepository = StatsRepository(SharedPrefsKeyValueStore(this))
        soundManager = SoundManager(this)
        floatingOverlayManager = FloatingOverlayManager(this)

        initStateMachine()
        registerScreenReceiver()
    }

    private fun initStateMachine() {
        val prefs = statsRepository.getPreferences()
        val todayStats = statsRepository.getStatsForDate()

        stateMachine = TimerStateMachine(
            workDurationSeconds = prefs.workDurationSeconds,
            breakDurationSeconds = prefs.breakDurationSeconds,
            onBreakCompleted = {
                statsRepository.recordBreakCompleted()
                notificationHelper.dismissBreakAlertNotification()
                floatingOverlayManager.hideOverlay()
                if (prefs.soundEnabled) soundManager.playBreakCompleteTone()
                if (prefs.vibrationEnabled) soundManager.vibrateGentle()
            },
            onBreakSkipped = {
                statsRepository.recordBreakSkipped()
                notificationHelper.dismissBreakAlertNotification()
                floatingOverlayManager.hideOverlay()
            },
            onBreakTriggered = {
                if (prefs.useFloatingOverlay && floatingOverlayManager.canDrawOverlays) {
                    floatingOverlayManager.showOverlay()
                } else {
                    notificationHelper.showBreakAlertNotification()
                }
                if (prefs.vibrationEnabled) soundManager.vibrateGentle()
            }
        )
        _timerState.value = stateMachine.snapshot.copy(
            totalScreenTimeTodaySeconds = todayStats.screenTimeSeconds
        )
    }

    private fun registerScreenReceiver() {
        screenReceiver = ScreenBroadcastReceiver(
            onScreenOn = { now ->
                stateMachine.onScreenOn(now)
                _timerState.value = stateMachine.snapshot
                updateNotification()
            },
            onScreenOff = { now ->
                stateMachine.onScreenOff(now)
                _timerState.value = stateMachine.snapshot
                updateNotification()
            }
        )
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_USER_PRESENT)
        }
        registerReceiver(screenReceiver, filter)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action ?: ACTION_START
        when (action) {
            ACTION_START -> {
                startForeground(
                    NotificationHelper.TRACKING_NOTIFICATION_ID,
                    notificationHelper.buildTrackingNotification(stateMachine.snapshot)
                )
                stateMachine.start()
                startTicker()
                statsRepository.updatePreferences(
                    statsRepository.getPreferences().copy(trackingEnabled = true)
                )
            }
            ACTION_STOP -> {
                stopTicker()
                stateMachine.stop()
                _timerState.value = stateMachine.snapshot
                statsRepository.updatePreferences(
                    statsRepository.getPreferences().copy(trackingEnabled = false)
                )
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
            ACTION_START_BREAK -> {
                stateMachine.startBreak()
                _timerState.value = stateMachine.snapshot
            }
            ACTION_SKIP_BREAK -> {
                stateMachine.skipBreak()
                _timerState.value = stateMachine.snapshot
                updateNotification()
            }
            ACTION_SNOOZE_BREAK -> {
                stateMachine.snoozeBreak()
                _timerState.value = stateMachine.snapshot
                notificationHelper.dismissBreakAlertNotification()
                updateNotification()
            }
            ACTION_RELOAD_SETTINGS -> {
                val prefs = statsRepository.getPreferences()
                // If running, reconfigure intervals
                initStateMachine()
                stateMachine.start()
            }
            ACTION_SHOW_TEST_OVERLAY -> {
                floatingOverlayManager.showOverlay()
            }
        }
        _timerState.value = stateMachine.snapshot
        return START_STICKY
    }

    private fun startTicker() {
        tickerJob?.cancel()
        tickerJob = serviceScope.launch {
            var secondCount = 0
            while (isActive) {
                delay(1000L)
                stateMachine.tickSecond()
                _timerState.value = stateMachine.snapshot

                // Persist screen time increment every 10 seconds to storage
                secondCount++
                if (secondCount >= 10) {
                    secondCount = 0
                    if (stateMachine.snapshot.state == SessionState.RUNNING && stateMachine.snapshot.isScreenOn) {
                        statsRepository.addScreenTime(10L)
                    }
                }

                // Update ongoing notification
                updateNotification()
            }
        }
    }

    private fun stopTicker() {
        tickerJob?.cancel()
        tickerJob = null
    }

    private fun updateNotification() {
        val snapshot = stateMachine.snapshot
        val notification = notificationHelper.buildTrackingNotification(snapshot)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        manager.notify(NotificationHelper.TRACKING_NOTIFICATION_ID, notification)
    }

    override fun onDestroy() {
        stopTicker()
        floatingOverlayManager.hideOverlay()
        screenReceiver?.let {
            try {
                unregisterReceiver(it)
            } catch (_: Exception) {}
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
