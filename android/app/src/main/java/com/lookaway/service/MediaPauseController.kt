package com.lookaway.service

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build

interface AudioPlaybackClient {
    fun requestTransientAudioFocus(): Boolean
    fun abandonAudioFocus(): Boolean
}

class AndroidAudioPlaybackClient(private val context: Context) : AudioPlaybackClient {

    private val audioManager: AudioManager? by lazy {
        context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    }

    private var activeFocusRequest: AudioFocusRequest? = null

    override fun requestTransientAudioFocus(): Boolean {
        val am = audioManager ?: return false
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val playbackAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()

                val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                    .setAudioAttributes(playbackAttributes)
                    .setAcceptsDelayedFocusGain(false)
                    .setOnAudioFocusChangeListener { /* no-op listener to hold focus */ }
                    .build()

                activeFocusRequest = focusRequest
                val result = am.requestAudioFocus(focusRequest)
                result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
            } else {
                @Suppress("DEPRECATION")
                val result = am.requestAudioFocus(
                    null,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
                )
                result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
            }
        } catch (_: Exception) {
            false
        }
    }

    override fun abandonAudioFocus(): Boolean {
        val am = audioManager ?: return false
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                activeFocusRequest?.let { request ->
                    am.abandonAudioFocusRequest(request)
                    activeFocusRequest = null
                }
                true
            } else {
                @Suppress("DEPRECATION")
                am.abandonAudioFocus(null)
                true
            }
        } catch (_: Exception) {
            false
        }
    }
}

class MediaPauseController(
    private val client: AudioPlaybackClient
) {
    constructor(context: Context) : this(AndroidAudioPlaybackClient(context))

    var isMediaPaused: Boolean = false
        private set

    @Synchronized
    fun pauseMedia(): Boolean {
        if (isMediaPaused) return true
        val granted = client.requestTransientAudioFocus()
        if (granted) {
            isMediaPaused = true
        }
        return granted
    }

    @Synchronized
    fun resumeMedia(): Boolean {
        if (!isMediaPaused) return true
        val abandoned = client.abandonAudioFocus()
        if (abandoned) {
            isMediaPaused = false
        }
        return abandoned
    }

    @Synchronized
    fun reset() {
        if (isMediaPaused) {
            client.abandonAudioFocus()
            isMediaPaused = false
        }
    }
}
