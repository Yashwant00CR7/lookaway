package com.lookaway.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lookaway.ui.dashboard.DashboardScreen
import com.lookaway.ui.dashboard.DashboardViewModel
import com.lookaway.ui.theme.LookAwayTheme

import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings

class MainActivity : ComponentActivity() {

    private val viewModel: DashboardViewModel by viewModels()

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            // Permission result handled
            viewModel.autoStartIfEnabled()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkAndRequestPermissions()
        checkBatteryOptimizations()
        viewModel.autoStartIfEnabled()

        setContent {
            LookAwayTheme {
                val timerSnapshot by viewModel.timerState.collectAsStateWithLifecycle()
                val dailyStats by viewModel.dailyStats.collectAsStateWithLifecycle()
                val preferences by viewModel.preferences.collectAsStateWithLifecycle()
                val selectedCharacter by viewModel.selectedCharacter.collectAsStateWithLifecycle()
                val canDrawOverlays by viewModel.canDrawOverlays.collectAsStateWithLifecycle()

                DashboardScreen(
                    timerSnapshot = timerSnapshot,
                    dailyStats = dailyStats,
                    preferences = preferences,
                    selectedCharacter = selectedCharacter,
                    canDrawOverlays = canDrawOverlays,
                    onToggleTracking = { viewModel.toggleTracking() },
                    onSavePreferences = { viewModel.savePreferences(it) },
                    onSelectAnimeCharacter = { id, customUrl ->
                        viewModel.selectAnimeCharacter(id, customUrl)
                    },
                    onTestFloatingOverlay = {
                        viewModel.testFloatingOverlay()
                    },
                    onRequestOverlayPermission = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            try {
                                val intent = Intent(
                                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:$packageName")
                                )
                                startActivity(intent)
                            } catch (_: Exception) {}
                        }
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshStats()
    }

    private fun checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasNotificationPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasNotificationPermission) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun checkBatteryOptimizations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = getSystemService(POWER_SERVICE) as? PowerManager
            if (powerManager != null && !powerManager.isIgnoringBatteryOptimizations(packageName)) {
                try {
                    val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                        data = Uri.parse("package:$packageName")
                    }
                    startActivity(intent)
                } catch (_: Exception) {
                    // Fallback if OEM custom ROM doesn't support the direct intent
                }
            }
        }
    }
}
