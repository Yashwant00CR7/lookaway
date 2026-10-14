package com.lookaway.ui.breakoverlay

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lookaway.core.SessionState
import com.lookaway.service.LookAwayService
import com.lookaway.service.MediaPauseController
import kotlinx.coroutines.delay

class BreakActivity : ComponentActivity() {

    private val mediaPauseController by lazy { MediaPauseController(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureWindowFlags()

        mediaPauseController.pauseMedia()

        // Tell service to enter BREAK_ACTIVE state
        LookAwayService.startBreak(this)

        setContent {
            val timerSnapshot by LookAwayService.timerState.collectAsStateWithLifecycle()
            var isFinished by remember { mutableStateOf(false) }

            LaunchedEffect(timerSnapshot.state) {
                if (timerSnapshot.state == SessionState.RUNNING && timerSnapshot.remainingBreakSeconds == timerSnapshot.breakDurationSeconds) {
                    isFinished = true
                    delay(1500L)
                    finish()
                }
            }

            BreakScreen(
                remainingSeconds = timerSnapshot.remainingBreakSeconds,
                totalBreakDuration = timerSnapshot.breakDurationSeconds,
                isCompleted = isFinished,
                onSkip = {
                    LookAwayService.skipBreak(this@BreakActivity)
                    finish()
                },
                onSnooze = {
                    LookAwayService.snoozeBreak(this@BreakActivity)
                    finish()
                },
                onFinishNow = {
                    finish()
                }
            )
        }
    }

    private fun configureWindowFlags() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            )
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onDestroy() {
        mediaPauseController.resumeMedia()
        super.onDestroy()
    }
}
