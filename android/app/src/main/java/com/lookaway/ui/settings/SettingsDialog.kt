package com.lookaway.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lookaway.data.UserPreferences

@Composable
fun SettingsDialog(
    currentPreferences: UserPreferences,
    onDismiss: () -> Unit,
    onSave: (UserPreferences) -> Unit
) {
    var workMinutes by remember { mutableFloatStateOf(currentPreferences.workDurationMinutes.toFloat()) }
    var breakSeconds by remember { mutableFloatStateOf(currentPreferences.breakDurationSeconds.toFloat()) }
    var soundEnabled by remember { mutableStateOf(currentPreferences.soundEnabled) }
    var vibrationEnabled by remember { mutableStateOf(currentPreferences.vibrationEnabled) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Timer Settings",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Work Interval Slider
                Text(
                    text = "Work Interval: ${workMinutes.toInt()} min",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Slider(
                    value = workMinutes,
                    onValueChange = { workMinutes = it },
                    valueRange = 5f..60f,
                    steps = 10,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf(15, 20, 25, 30).forEach { mins ->
                        FilterChip(
                            selected = workMinutes.toInt() == mins,
                            onClick = { workMinutes = mins.toFloat() },
                            label = { Text("${mins}m", fontSize = 12.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Break Duration Slider
                Text(
                    text = "Break Duration: ${breakSeconds.toInt()} sec",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Slider(
                    value = breakSeconds,
                    onValueChange = { breakSeconds = it },
                    valueRange = 10f..60f,
                    steps = 9,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf(15, 20, 30, 45).forEach { secs ->
                        FilterChip(
                            selected = breakSeconds.toInt() == secs,
                            onClick = { breakSeconds = secs.toFloat() },
                            label = { Text("${secs}s", fontSize = 12.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Sound Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Audio Chime", fontSize = 15.sp)
                    Switch(
                        checked = soundEnabled,
                        onCheckedChange = { soundEnabled = it }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Vibration Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Haptic Feedback", fontSize = 15.sp)
                    Switch(
                        checked = vibrationEnabled,
                        onCheckedChange = { vibrationEnabled = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        UserPreferences(
                            workDurationMinutes = workMinutes.toInt(),
                            breakDurationSeconds = breakSeconds.toInt(),
                            soundEnabled = soundEnabled,
                            vibrationEnabled = vibrationEnabled
                        )
                    )
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
