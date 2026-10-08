package com.lookaway.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.lookaway.core.SessionState
import com.lookaway.core.TimerSnapshot
import com.lookaway.ui.MainActivity
import com.lookaway.ui.breakoverlay.BreakActivity

class NotificationHelper(private val context: Context) {

    companion object {
        const val TRACKING_CHANNEL_ID = "lookaway_tracking_channel"
        const val BREAK_CHANNEL_ID = "lookaway_break_channel"
        const val TRACKING_NOTIFICATION_ID = 1001
        const val BREAK_NOTIFICATION_ID = 1002
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val trackingChannel = NotificationChannel(
                TRACKING_CHANNEL_ID,
                "Eye Strain Tracking",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows live screen time tracking and remaining time until next eye rest"
                setShowBadge(false)
            }

            val breakChannel = NotificationChannel(
                BREAK_CHANNEL_ID,
                "Eye Rest Alert",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifies you when it is time to take a 20-second 20-20-20 eye break"
                enableVibration(true)
                setShowBadge(true)
            }

            notificationManager.createNotificationChannel(trackingChannel)
            notificationManager.createNotificationChannel(breakChannel)
        }
    }

    fun buildTrackingNotification(snapshot: TimerSnapshot): Notification {
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val minutes = snapshot.remainingWorkSeconds / 60
        val seconds = snapshot.remainingWorkSeconds % 60
        val timeFormatted = String.format("%02d:%02d", minutes, seconds)

        val contentText = when (snapshot.state) {
            SessionState.RUNNING -> "Next break in $timeFormatted"
            SessionState.PAUSED -> "Paused (Screen resting)"
            SessionState.BREAK_ACTIVE, SessionState.BREAK_PENDING -> "Break time! Look 20 feet away"
            SessionState.STOPPED -> "Tracking stopped"
        }

        return NotificationCompat.Builder(context, TRACKING_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("LookAway Active")
            .setContentText(contentText)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    fun showBreakAlertNotification(): Notification {
        val breakIntent = Intent(context, BreakActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            1,
            breakIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, BREAK_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Time for a 20-20-20 Eye Break!")
            .setContentText("Look at an object at least 20 feet away for 20 seconds.")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setContentIntent(fullScreenPendingIntent)
            .build()

        notificationManager.notify(BREAK_NOTIFICATION_ID, notification)
        return notification
    }

    fun dismissBreakAlertNotification() {
        notificationManager.cancel(BREAK_NOTIFICATION_ID)
    }
}
