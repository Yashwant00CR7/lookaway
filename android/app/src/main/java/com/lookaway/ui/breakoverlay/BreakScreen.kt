package com.lookaway.ui.breakoverlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BreakScreen(
    remainingSeconds: Int,
    totalBreakDuration: Int,
    isCompleted: Boolean,
    onSkip: () -> Unit,
    onSnooze: () -> Unit,
    onFinishNow: () -> Unit
) {
    val progress = if (totalBreakDuration > 0) {
        remainingSeconds.toFloat() / totalBreakDuration.toFloat()
    } else 0f

    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        label = "breakProgress"
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF0B132B) // Deep serene midnight navy
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Eye Icon Badge
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color(0xFF1C2541), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RemoveRedEye,
                        contentDescription = "Eye Rest",
                        tint = if (isCompleted) Color(0xFF4EBA6F) else Color(0xFF48CAE4),
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = if (isCompleted) "Eyes Refreshed!" else "Look 20 Feet Away",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isCompleted)
                        "Great job giving your ciliary muscles a break."
                    else
                        "Gaze at a distant object (~6 meters away) or out a window to relax eye strain.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFFB0C4DE),
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(36.dp))

                // Circular Countdown Ring
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(200.dp)
                ) {
                    // Background track
                    CircularProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier.size(200.dp),
                        color = Color(0xFF1C2541),
                        strokeWidth = 10.dp
                    )
                    // Animated progress
                    CircularProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.size(200.dp),
                        color = if (isCompleted) Color(0xFF4EBA6F) else Color(0xFF00B4D8),
                        strokeWidth = 10.dp
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isCompleted) "Done" else "${remainingSeconds}s",
                            fontSize = 44.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = if (isCompleted) "Completed" else "Remaining",
                            fontSize = 13.sp,
                            color = Color(0xFF90E0EF)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Action Controls
                AnimatedVisibility(
                    visible = !isCompleted,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        OutlinedButton(
                            onClick = onSkip,
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFFE2E8F0)
                            )
                        ) {
                            Text("Skip")
                        }

                        Button(
                            onClick = onSnooze,
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1E3A8A)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Snooze,
                                contentDescription = "Snooze",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Snooze 5m")
                        }
                    }
                }

                AnimatedVisibility(
                    visible = isCompleted,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Button(
                        onClick = onFinishNow,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF059669)
                        ),
                        modifier = Modifier.fillMaxWidth(0.6f)
                    ) {
                        Text("Resume Screen", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
