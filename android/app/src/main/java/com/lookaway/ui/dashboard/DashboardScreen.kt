package com.lookaway.ui.dashboard

import android.content.Context
import android.content.Intent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lookaway.core.SessionState
import com.lookaway.core.TimerSnapshot
import com.lookaway.data.DailyStats
import com.lookaway.data.UserPreferences
import com.lookaway.ui.breakoverlay.BreakActivity
import com.lookaway.ui.settings.SettingsDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    timerSnapshot: TimerSnapshot,
    dailyStats: DailyStats,
    preferences: UserPreferences,
    onToggleTracking: () -> Unit,
    onSavePreferences: (UserPreferences) -> Unit
) {
    val context = LocalContext.current
    var showSettingsDialog by remember { mutableStateOf(false) }

    if (showSettingsDialog) {
        SettingsDialog(
            currentPreferences = preferences,
            onDismiss = { showSettingsDialog = false },
            onSave = {
                onSavePreferences(it)
                showSettingsDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.RemoveRedEye,
                            contentDescription = "LookAway",
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "LookAway",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showSettingsDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Main Hero Countdown Card
            HeroTimerCard(
                timerSnapshot = timerSnapshot,
                onToggleTracking = onToggleTracking
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Test Break Screen Preview Button
            OutlinedButton(
                onClick = {
                    val intent = Intent(context, BreakActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.RemoveRedEye,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Test 20-Second Break Overlay")
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Today's Stats Card
            StatsCard(
                dailyStats = dailyStats,
                liveScreenTimeSeconds = timerSnapshot.totalScreenTimeTodaySeconds
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Science / Health Guidance Card
            ScienceInfoCard()

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun HeroTimerCard(
    timerSnapshot: TimerSnapshot,
    onToggleTracking: () -> Unit
) {
    val isRunning = timerSnapshot.state == SessionState.RUNNING
    val isPaused = timerSnapshot.state == SessionState.PAUSED
    val isStopped = timerSnapshot.state == SessionState.STOPPED

    val totalWork = if (timerSnapshot.workDurationSeconds > 0) timerSnapshot.workDurationSeconds else 1200
    val progress = if (isStopped) 0f else (timerSnapshot.remainingWorkSeconds.toFloat() / totalWork.toFloat())
    val animatedProgress by animateFloatAsState(targetValue = progress.coerceIn(0f, 1f), label = "heroProgress")

    val statusColor by animateColorAsState(
        targetValue = when {
            isRunning -> Color(0xFF10B981) // Green
            isPaused -> Color(0xFFF59E0B)  // Amber
            else -> Color(0xFF64748B)      // Slate Gray
        },
        label = "statusColor"
    )

    val statusText = when {
        isRunning -> "Monitoring Screen Time"
        isPaused -> "Paused (Screen Off)"
        timerSnapshot.state == SessionState.BREAK_ACTIVE -> "Break in Progress"
        timerSnapshot.state == SessionState.BREAK_PENDING -> "Break Ready"
        else -> "Tracking Stopped"
    }

    val minutes = timerSnapshot.remainingWorkSeconds / 60
    val seconds = timerSnapshot.remainingWorkSeconds % 60
    val countdownString = String.format("%02d:%02d", minutes, seconds)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Status Pill
            Surface(
                color = statusColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(statusColor, shape = CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = statusText,
                        color = statusColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Circular Countdown Display
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(210.dp)
            ) {
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.size(210.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    strokeWidth = 12.dp
                )
                CircularProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.size(210.dp),
                    color = if (isStopped) Color(0xFF475569) else Color(0xFF38BDF8),
                    strokeWidth = 12.dp
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isStopped) "--:--" else countdownString,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Next Eye Break",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Primary Toggle Button
            Button(
                onClick = onToggleTracking,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isStopped) Color(0xFF0284C7) else Color(0xFFDC2626)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(
                    imageVector = if (isStopped) Icons.Default.PlayArrow else Icons.Default.Stop,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isStopped) "Start Eye Protection" else "Stop Protection",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun StatsCard(
    dailyStats: DailyStats,
    liveScreenTimeSeconds: Long
) {
    val totalScreenSeconds = maxOf(dailyStats.screenTimeSeconds, liveScreenTimeSeconds)
    val screenHours = totalScreenSeconds / 3600
    val screenMinutes = (totalScreenSeconds % 3600) / 60
    val screenTimeFormatted = if (screenHours > 0) "${screenHours}h ${screenMinutes}m" else "${screenMinutes}m"

    val restSeconds = dailyStats.breaksCompleted * 20

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Today's Eye Health",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    icon = Icons.Default.CheckCircle,
                    iconTint = Color(0xFF10B981),
                    value = "${dailyStats.breaksCompleted}",
                    label = "Breaks Done"
                )
                StatItem(
                    icon = Icons.Default.VisibilityOff,
                    iconTint = Color(0xFFF43F5E),
                    value = "${dailyStats.breaksSkipped}",
                    label = "Skipped"
                )
                StatItem(
                    icon = Icons.Default.Timer,
                    iconTint = Color(0xFF38BDF8),
                    value = screenTimeFormatted,
                    label = "Screen Time"
                )
                StatItem(
                    icon = Icons.Default.RemoveRedEye,
                    iconTint = Color(0xFFA855F7),
                    value = "${restSeconds}s",
                    label = "Rest Gained"
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    icon: ImageVector,
    iconTint: Color,
    value: String,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(iconTint.copy(alpha = 0.12f), shape = RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ScienceInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0369A1).copy(alpha = 0.15f)
        )
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "20-20-20 Info",
                tint = Color(0xFF38BDF8),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = "The 20-20-20 Eye Rule",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF38BDF8)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Digital eye strain is caused by decreased blink rate (drops 50%) and prolonged ciliary muscle contraction—not blue light. Every 20 minutes, gaze 20 feet away for 20 seconds to replenish tears and relax focus.",
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
