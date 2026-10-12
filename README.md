# LookAway 👁️
> **Relieve Digital Eye Strain & Dry Eyes with the 20-20-20 Rule**

LookAway is an intelligent, privacy-first, battery-friendly Android application designed to prevent digital eye strain and ocular dryness by enforcing the clinically proven **20-20-20 eye care rule**.

---

## 🔬 The Science: Blue Light Glasses vs The 20-20-20 Rule

Many people assume "blue light blocking" (blu-ray) glasses relieve dry, irritated eyes after working at a computer or phone. However, clinical ophthalmology studies (including the American Academy of Ophthalmology) have confirmed that **blue light is not what causes dry eyes or eye fatigue**:

1. **Decreased Blink Rate**: Under normal conditions, humans blink ~15 times per minute. When staring intently at screens, that rate drops by over 50% to ~5–7 blinks per minute. Tears evaporate, leaving the cornea exposed and dry.
2. **Ciliary Muscle Accommodation Spasm**: Focusing at a near distance (30–50 cm) keeps the eye’s focusing muscles (ciliary muscles) contracted for hours without relief.

### The Solution: The 20-20-20 Rule
- **Every 20 minutes** of screen time,
- Look at a stationary object **at least 20 feet away (6 meters)** or out a window,
- For **at least 20 seconds**.

Looking into the distance completely relaxes the ciliary muscle spasm and prompts natural blinking to restore the eye's tear film.

---

## ⚡ Why Screen-Time Tracking Instead of Camera Eye Tracking?

LookAway rejects battery-draining, camera-based iris tracking in favor of event-driven system screen-time tracking:

| Feature | Camera Eye Tracking | LookAway Screen-Time Tracking |
| :--- | :--- | :--- |
| **Battery Life** | Drains 15–30% battery/hr (camera + NPU/GPU) | **< 0.1% battery/day** (event-driven timer) |
| **Privacy** | Camera sensor active constantly (green LED on) | **100% Private** (zero camera/mic permissions) |
| **Reliability** | Fails in low light, tilted angles, or with glasses | **100% Deterministic** across all conditions |
| **Passive Breaks** | Doesn't know when phone is locked in your pocket | **Smart Reset**: Automatically detects when screen is locked |

---

## 🧠 Smart Reset Logic

LookAway doesn't naively fire alarms if you haven't even been using your screen:
- **Screen On**: Active timer decrements towards the break interval (default: 20 minutes).
- **Screen Off < 20 seconds**: If you put your phone in your pocket briefly or answer a quick glance, the timer pauses and resumes.
- **Screen Off ≥ 20 seconds**: If your phone screen is off for 20+ seconds, your eyes enjoyed a natural rest! LookAway automatically resets the 20-minute countdown back to the beginning.

---

## 📱 Features

- **Floating Anime Animation Overlay over other apps**: When 20 minutes of screen time elapse, a soothing semi-transparent overlay appears directly on top of whatever app you are using (WhatsApp, YouTube, games, browser) featuring an animated anime character with a **transparent background** (isolated character sticker, no solid box) and a 20-second circular countdown ring!
- **Curated Famous Anime Roster + Custom Web URL**:
  - **Goku** (Dragon Ball Z)
  - **Naruto** (Naruto Shippuden)
  - **Luffy** (One Piece)
  - **Anya Forger** (Spy x Family)
  - **Pikachu** (Pokémon)
  - **Totoro** (My Neighbor Totoro)
  - **Custom Web URL**: Paste any transparent GIF or animated WebP link from the web to display your own favorite character!
- **Zero-Friction Fallback**: If "Display over other apps" permission hasn't been granted yet, LookAway gracefully falls back to the Full-Screen alarm intent so you never miss a break.
- **Harmonic Chime & Haptic Pulse**: Plays a subtle completion tone and gentle vibration when the 20 seconds conclude so you don't even have to look at the screen during your break.
- **Today's Eye Health Dashboard**:
  - Breaks Taken Today
  - Breaks Skipped
  - Total Active Screen Time
  - Total Seconds of Eye Rest Gained
- **Customizable Intervals**: Configure work intervals (15m, 20m, 25m, 30m, etc.) and break durations (15s, 20s, 30s) to fit your schedule.
- **Eye-Comfort Dark Theme**: Material 3 dark color scheme tuned to minimize screen glare.

---

## 🛠️ Architecture

- **Language:** Kotlin 2.0.21
- **UI Framework:** Jetpack Compose (Material 3)
- **Background Engine:** Android Foreground Service with low-priority notification
- **Screen Detection:** Dynamic `BroadcastReceiver` for `ACTION_SCREEN_ON` and `ACTION_SCREEN_OFF`
- **Minimum SDK:** API 26 (Android 8.0)
- **Target SDK:** API 35 (Android 15)

---

## 🚀 Installation & Build

### Install Pre-built Debug APK
If you have an Android device connected via USB or wireless debugging:
```bash
adb install lookaway-debug.apk
```

### Build from Source
```bash
cd android
./gradlew assembleDebug
```
The APK will be generated at: `android/app/build/outputs/apk/debug/app-debug.apk`.

### Run Unit Tests
```bash
cd android
./gradlew test
```

---

## 🗺️ Roadmap
- [x] **Milestone 1**: LookAway Android v1.0 (Screen time monitoring, smart reset, full-screen break overlay, stats dashboard).
- [ ] **Milestone 2**: LookAway macOS Desktop v2.0 (Native Swift Menu Bar app with system idle detection and menu bar countdown).
