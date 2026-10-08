package com.lookaway.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ScreenBroadcastReceiver(
    private val onScreenOn: (timestampMs: Long) -> Unit,
    private val onScreenOff: (timestampMs: Long) -> Unit
) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val now = System.currentTimeMillis()
        when (intent?.action) {
            Intent.ACTION_SCREEN_ON, Intent.ACTION_USER_PRESENT -> {
                onScreenOn(now)
            }
            Intent.ACTION_SCREEN_OFF -> {
                onScreenOff(now)
            }
        }
    }
}
