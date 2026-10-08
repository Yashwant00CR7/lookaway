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

class MainActivity : ComponentActivity() {

    private val viewModel: DashboardViewModel by viewModels()

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            // Permission result handled; notifications will display if granted
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkAndRequestPermissions()

        setContent {
            LookAwayTheme {
                val timerSnapshot by viewModel.timerState.collectAsStateWithLifecycle()
                val dailyStats by viewModel.dailyStats.collectAsStateWithLifecycle()
                val preferences by viewModel.preferences.collectAsStateWithLifecycle()

                DashboardScreen(
                    timerSnapshot = timerSnapshot,
                    dailyStats = dailyStats,
                    preferences = preferences,
                    onToggleTracking = { viewModel.toggleTracking() },
                    onSavePreferences = { viewModel.savePreferences(it) }
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
}
