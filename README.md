# LookAway
> Relieve Digital Eye Strain & Dry Eyes with the 20-20-20 Rule

[![GitHub Release](https://img.shields.io/github/v/release/Yashwant00CR7/lookaway?color=38BDF8&label=Release&style=flat-square)](https://github.com/Yashwant00CR7/lookaway/releases/latest)
[![Platform](https://img.shields.io/badge/Platform-Android%208.0%2B%20(API%2026--35)-10B981?style=flat-square)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?style=flat-square)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-4285F4?style=flat-square)](https://developer.android.com/jetpack/compose)
[![Privacy](https://img.shields.io/badge/Privacy-100%25%20Local%20(Zero%20Camera)-success?style=flat-square)](#why-screen-time-tracking-instead-of-camera-iris-tracking)
[![License](https://img.shields.io/badge/License-MIT-gray?style=flat-square)](LICENSE)

---

## Download

[Download Latest APK](https://github.com/Yashwant00CR7/lookaway/releases/latest/download/lookaway-v1.1.0.apk) *(Direct install on Android 8.0+)*

---

## Screenshots

| Dashboard Screen | Floating Break Overlay |
| :---: | :---: |
| <img src="docs/screenshots/dashboard.png" width="360" alt="LookAway Dashboard Screen" /> | <img src="docs/screenshots/overlay.png" width="360" alt="LookAway Floating Break Overlay" /> |
| *Real-time screen-time countdown, service status, today's metrics, and companion selection.* | *Floating transparent character sticker with 20s countdown ring appearing directly over any active app.* |

---

## Core Purpose & The Problem

### The Blue-Ray / UV Glasses Misconception
Many people experience scratchy, burning, dry eyes and headaches after hours of staring at phones and computers. A widespread misconception suggests that "blue light blocking" (blu-ray) glasses will solve eye dryness and screen fatigue.

Clinical research and the **American Academy of Ophthalmology (AAO)** have repeatedly confirmed:
> **Blue light emitted by computer and phone screens does not cause ocular dryness or digital eye strain.**

The real biological causes of digital eye strain are:
1. **Decreased Blink Rate (Tear Film Evaporation):**
   - Under normal conditions, humans blink approximately 15 times per minute.
   - When concentrating intently on digital displays, our spontaneous blink rate drops by over 60% down to 5–7 blinks per minute.
   - Without regular blinking, the eye's tear film evaporates, exposing the sensitive corneal epithelium to room air and causing dryness and redness.
2. **Ciliary Muscle Accommodation Spasm (Near-Focus Fatigue):**
   - Human eyes evolved to rest at optical infinity (> 20 feet away).
   - Staring at a near screen (30–50 cm) forces the intraocular ciliary muscles to stay continuously contracted in an accommodation spasm for hours without relief.

---

### The Clinically Proven Solution: The 20-20-20 Rule
Developed by optometrist Dr. Jeffrey Anshel and endorsed by ophthalmologists worldwide:
- **Every 20 minutes** of continuous screen viewing,
- Shift your gaze to a stationary object **at least 20 feet away (6 meters)** (e.g., out a window or down a hallway),
- Keep looking into the distance for **at least 20 seconds**.

Looking into the distance completely releases tension on the ciliary muscles, resets focal accommodation, and triggers natural blinks that replenish the corneal tear film.

---

### Why Screen-Time Tracking Instead of Camera Iris Tracking?
Using the front-facing camera with computer vision to track pupils or blink rate introduces severe drawbacks:

| Evaluation Vector | Front Camera Computer Vision | LookAway Screen-Time Engine |
| :--- | :--- | :--- |
| **Battery Consumption** | High: 15%–30% battery drain per hour (camera sensor + neural inference) | Minimal: < 0.1% battery drain per day (event-driven hardware broadcasts) |
| **Privacy & Security** | Camera stays powered on continuously | 100% Private: Zero camera, microphone, or biometric permissions requested |
| **Reliability** | Degrades in low light, tilted angles, or when wearing spectacles | Deterministic: Screen status reported directly by the Android OS |
| **Device Thermals** | Causes device to run warm | Device remains cool |
| **Passive Natural Breaks** | Cannot reliably tell if user left phone on desk or put in pocket | Smart Screen-Off Reset: Detects lock state accurately |

---

## Smart Reset Architecture

- **Screen On:** The foreground service monitors active display time and decrements the timer towards the break interval (default: 20 minutes).
- **Brief Glance (Screen Off < 20s):** If you lock your phone briefly, the timer pauses and resumes when unlocked.
- **Natural Rest (Screen Off ≥ 20s):** If your phone remains locked for 20 seconds or longer, your eyes already received rest. LookAway automatically resets the 20-minute countdown back to the full 20:00 mark.

---

## Features

### Floating Break Overlay
When 20 minutes of screen time elapse, LookAway renders an isolated floating character sticker with a transparent background directly over your active app:
- **Transparent Character Animation:** Curated animated character sticker without solid opaque borders.
- **20-Second Progress Dial:** Circular countdown indicator displaying remaining rest time.
- **Media Pause & Resume:** Automatically pauses background music, podcasts, or videos via transient audio focus, resuming playback upon break completion.
- **Horizontal / Landscape Support:** Adapts dynamically to landscape orientation with a balanced two-column layout.
- **Quick Controls:** `Snooze 5m` if you are in the middle of a critical task, or `Skip` when needed.

### Companion Presets & Custom URLs
- **Goku** (*Dragon Ball Z*)
- **Naruto Uzumaki** (*Naruto Shippuden*)
- **Monkey D. Luffy** (*One Piece*)
- **Anya Forger** (*Spy x Family*)
- **Pikachu** (*Pokémon*)
- **Totoro** (*My Neighbor Totoro*)
- **Custom Web URL:** Support for direct transparent GIF or animated WebP links from Giphy, Tenor, or personal storage.

### Daily Analytics Dashboard
- **Completed Breaks:** Successful 20-20-20 breaks completed today.
- **Skipped Breaks:** Deferred break instances.
- **Screen Time Monitored:** Total cumulative screen time today.
- **Rest Earned:** Total seconds of ciliary muscle relaxation gained.

### Settings & Controls
- **Adjustable Work Intervals:** Configure intervals (15m, 20m, 25m, 30m).
- **Adjustable Break Duration:** 15s, 20s, 30s rest periods.
- **Fallback Mode:** Full-screen activity fallback if overlay permission is pending.
- **Audio Chime & Haptic Feedback:** Subtle completion chime and vibration feedback.
- **Auto-Start on Boot:** Restarts tracking automatically when device reboots.

---

## Tech Stack & Architecture

- **Operating System:** Android 8.0+ (API Level 26–35)
- **UI Toolkit:** Jetpack Compose with Material 3 Dark Theme
- **Image & Animation Engine:** Coil 2.7.0 (`coil-gif`) for animated GIF/WebP decoding
- **Audio Focus:** `AudioManager` with transient audio focus management (`AUDIOFOCUS_GAIN_TRANSIENT`)
- **Overlay Engine:** Android `WindowManager` with `TYPE_APPLICATION_OVERLAY`
- **Background Execution:** Android Foreground Service with low-priority notification
- **Screen Detection:** BroadcastReceiver for `ACTION_SCREEN_ON`, `ACTION_SCREEN_OFF`, and `ACTION_USER_PRESENT`
- **Testing:** JUnit 4 + Kotlin test suite

---

## Getting Started

### Method 1: Install Pre-built APK
1. Download `lookaway-v1.1.0.apk` from the [GitHub Release](https://github.com/Yashwant00CR7/lookaway/releases/latest).
2. Tap the file to install on your Android device (enable "Install unknown apps" if prompted).
3. Open **LookAway**, grant **Display over other apps**, and tap **Start Tracking**.

### Method 2: Install via ADB
```bash
adb install lookaway-debug.apk
```

### Method 3: Build from Source
```bash
git clone git@github-personal:Yashwant00CR7/lookaway.git
cd lookaway/android

# Build Debug APK
./gradlew assembleDebug

# Run Unit Tests
./gradlew testDebugUnitTest
```

---

## License
This project is open-source and available under the [MIT License](LICENSE).
