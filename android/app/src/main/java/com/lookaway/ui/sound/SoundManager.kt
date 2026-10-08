package com.lookaway.ui.sound

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class SoundManager(private val context: Context) {

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    /**
     * Plays a pleasant harmonic chime when break is completed.
     */
    fun playBreakCompleteTone() {
        try {
            val toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80)
            toneGenerator.startTone(ToneGenerator.TONE_PROP_PROMPT, 300)
        } catch (_: Exception) {
            // Graceful fallback if audio stream is unavailable
        }
    }

    /**
     * Vibrates the device gently when break starts or finishes.
     */
    fun vibrateGentle() {
        try {
            vibrator?.let { v ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val effect = VibrationEffect.createWaveform(
                        longArrayOf(0, 150, 100, 200),
                        intArrayOf(0, 120, 0, 180),
                        -1
                    )
                    v.vibrate(effect)
                } else {
                    @Suppress("DEPRECATION")
                    v.vibrate(longArrayOf(0, 150, 100, 200), -1)
                }
            }
        } catch (_: Exception) {
            // Graceful fallback
        }
    }
}
