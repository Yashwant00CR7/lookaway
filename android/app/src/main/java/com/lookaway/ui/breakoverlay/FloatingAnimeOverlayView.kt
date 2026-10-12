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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.lookaway.data.AnimeCharacter
import com.lookaway.ui.image.CoilImageLoader

@Composable
fun FloatingAnimeOverlayView(
    character: AnimeCharacter,
    remainingSeconds: Int,
    totalBreakDuration: Int,
    isCompleted: Boolean,
    onSkip: () -> Unit,
    onSnooze: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val imageLoader = CoilImageLoader.create(context)

    val progress = if (totalBreakDuration > 0) {
        remainingSeconds.toFloat() / totalBreakDuration.toFloat()
    } else 0f

    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        label = "overlayProgress"
    )

    // Semi-transparent soothing dark scrim over whatever app is active
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xD90A0F1D)) // ~85% opacity dark slate
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        // Close button at top right
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopEnd
        ) {
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .padding(12.dp)
                    .background(Color(0x33FFFFFF), shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = Color.White
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Transparent Anime Character Sticker Animation
            Box(
                modifier = Modifier
                    .size(190.dp)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(character.animationUrl)
                        .crossfade(true)
                        .build(),
                    imageLoader = imageLoader,
                    contentDescription = character.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(180.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Character Name Pill
            Surface(
                color = Color(0x3338BDF8),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "${character.name} • ${character.animeName}",
                    color = Color(0xFF38BDF8),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Character Tagline / Advice
            Text(
                text = if (isCompleted) "Awesome job! Eyes rested." else character.tagLine,
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Look at an object 20 feet away (~6 meters) for 20 seconds.",
                color = Color(0xFF94A3B8),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Circular Countdown Timer
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(140.dp)
            ) {
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.size(140.dp),
                    color = Color(0x33FFFFFF),
                    strokeWidth = 8.dp
                )
                CircularProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.size(140.dp),
                    color = if (isCompleted) Color(0xFF10B981) else Color(0xFF38BDF8),
                    strokeWidth = 8.dp
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isCompleted) "Done" else "${remainingSeconds}s",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = if (isCompleted) "Refreshed!" else "Break",
                        fontSize = 11.sp,
                        color = Color(0xFF7DD3FC)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            AnimatedVisibility(
                visible = !isCompleted,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedButton(
                        onClick = onSkip,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFCBD5E1)
                        )
                    ) {
                        Text("Skip")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = onSnooze,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1E3A8A)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Snooze,
                            contentDescription = "Snooze",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Snooze 5m")
                    }
                }
            }
        }
    }
}
