package com.lookaway.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.lookaway.data.SharedPrefsKeyValueStore
import com.lookaway.data.StatsRepository

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return

        when (intent?.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            "android.intent.action.QUICKBOOT_POWERON",
            "com.htc.intent.action.QUICKBOOT_POWERON" -> {
                val repository = StatsRepository(SharedPrefsKeyValueStore(context))
                if (repository.getPreferences().trackingEnabled) {
                    LookAwayService.startService(context)
                }
            }
        }
    }
}
