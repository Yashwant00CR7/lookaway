package com.lookaway.service

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.lookaway.core.SessionState
import com.lookaway.data.AnimeCharacter
import com.lookaway.data.SharedPrefsKeyValueStore
import com.lookaway.data.StatsRepository
import com.lookaway.ui.breakoverlay.FloatingAnimeOverlayView
import kotlinx.coroutines.delay

class FloatingOverlayManager(private val context: Context) {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val mediaPauseController = MediaPauseController(context)
    private var overlayView: View? = null
    private var lifecycleOwner: OverlayLifecycleOwner? = null

    val canDrawOverlays: Boolean
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }

    fun showOverlay(
        onDismissCallback: () -> Unit = {}
    ) {
        if (!canDrawOverlays || overlayView != null) return

        val statsRepository = StatsRepository(SharedPrefsKeyValueStore(context))
        val prefs = statsRepository.getPreferences()
        val character = AnimeCharacter.getOrDefault(
            id = prefs.selectedAnimeCharacterId,
            customUrl = prefs.customAnimeUrl
        )

        val owner = OverlayLifecycleOwner().apply {
            performRestore(null)
            handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
            handleLifecycleEvent(Lifecycle.Event.ON_START)
            handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        }
        lifecycleOwner = owner

        val composeView = ComposeView(context).apply {
            setViewTreeLifecycleOwner(owner)
            setViewTreeViewModelStoreOwner(owner)
            setViewTreeSavedStateRegistryOwner(owner)

            setContent {
                val timerSnapshot by LookAwayService.timerState.collectAsStateWithLifecycle()
                var isCompleted by remember { mutableStateOf(false) }

                LaunchedEffect(timerSnapshot.state, timerSnapshot.remainingBreakSeconds) {
                    if (timerSnapshot.state == SessionState.RUNNING &&
                        timerSnapshot.remainingBreakSeconds == timerSnapshot.breakDurationSeconds
                    ) {
                        isCompleted = true
                        delay(1500L)
                        hideOverlay()
                        onDismissCallback()
                    }
                }

                FloatingAnimeOverlayView(
                    character = character,
                    remainingSeconds = timerSnapshot.remainingBreakSeconds,
                    totalBreakDuration = timerSnapshot.breakDurationSeconds,
                    isCompleted = isCompleted,
                    onSkip = {
                        LookAwayService.skipBreak(context)
                        hideOverlay()
                        onDismissCallback()
                    },
                    onSnooze = {
                        LookAwayService.snoozeBreak(context)
                        hideOverlay()
                        onDismissCallback()
                    },
                    onDismiss = {
                        LookAwayService.skipBreak(context)
                        hideOverlay()
                        onDismissCallback()
                    }
                )
            }
        }

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )

        try {
            windowManager.addView(composeView, layoutParams)
            overlayView = composeView
            mediaPauseController.pauseMedia()
            LookAwayService.startBreak(context)
        } catch (_: Exception) {
            hideOverlay()
        }
    }

    fun hideOverlay() {
        overlayView?.let { view ->
            try {
                windowManager.removeView(view)
            } catch (_: Exception) {}
            overlayView = null
        }
        lifecycleOwner?.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        lifecycleOwner = null
        mediaPauseController.resumeMedia()
    }

    private class OverlayLifecycleOwner :
        LifecycleOwner,
        ViewModelStoreOwner,
        SavedStateRegistryOwner {

        private val lifecycleRegistry = LifecycleRegistry(this)
        private val savedStateRegistryController = SavedStateRegistryController.create(this)
        private val store = ViewModelStore()

        override val lifecycle: Lifecycle = lifecycleRegistry
        override val savedStateRegistry: SavedStateRegistry = savedStateRegistryController.savedStateRegistry
        override val viewModelStore: ViewModelStore = store

        fun performRestore(savedState: android.os.Bundle?) {
            savedStateRegistryController.performRestore(savedState)
        }

        fun handleLifecycleEvent(event: Lifecycle.Event) {
            lifecycleRegistry.handleLifecycleEvent(event)
            if (event == Lifecycle.Event.ON_DESTROY) {
                store.clear()
            }
        }
    }
}
