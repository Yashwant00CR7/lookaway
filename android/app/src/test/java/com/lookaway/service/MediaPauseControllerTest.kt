package com.lookaway.service

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MediaPauseControllerTest {

    private class FakeAudioPlaybackClient : AudioPlaybackClient {
        var isFocusRequested = false
        var isFocusAbandoned = false
        var shouldGrantFocus = true

        override fun requestTransientAudioFocus(): Boolean {
            return if (shouldGrantFocus) {
                isFocusRequested = true
                isFocusAbandoned = false
                true
            } else {
                false
            }
        }

        override fun abandonAudioFocus(): Boolean {
            isFocusAbandoned = true
            isFocusRequested = false
            return true
        }
    }

    private lateinit var fakeClient: FakeAudioPlaybackClient
    private lateinit var controller: MediaPauseController

    @Before
    fun setUp() {
        fakeClient = FakeAudioPlaybackClient()
        controller = MediaPauseController(fakeClient)
    }

    @Test
    fun `pauseMedia requests transient focus and updates state`() {
        val result = controller.pauseMedia()

        assertTrue(result)
        assertTrue(controller.isMediaPaused)
        assertTrue(fakeClient.isFocusRequested)
        assertFalse(fakeClient.isFocusAbandoned)
    }

    @Test
    fun `pauseMedia is idempotent when already paused`() {
        controller.pauseMedia()
        val secondResult = controller.pauseMedia()

        assertTrue(secondResult)
        assertTrue(controller.isMediaPaused)
    }

    @Test
    fun `resumeMedia abandons audio focus and updates state`() {
        controller.pauseMedia()
        val result = controller.resumeMedia()

        assertTrue(result)
        assertFalse(controller.isMediaPaused)
        assertTrue(fakeClient.isFocusAbandoned)
    }

    @Test
    fun `resumeMedia is idempotent when not paused`() {
        val result = controller.resumeMedia()

        assertTrue(result)
        assertFalse(controller.isMediaPaused)
        assertFalse(fakeClient.isFocusAbandoned)
    }

    @Test
    fun `pauseMedia handles failure to grant focus`() {
        fakeClient.shouldGrantFocus = false
        val result = controller.pauseMedia()

        assertFalse(result)
        assertFalse(controller.isMediaPaused)
        assertFalse(fakeClient.isFocusRequested)
    }
}
