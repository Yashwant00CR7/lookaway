# Requirements: Phase 3 (Media Pause, Landscape Overlay, Clean UI & Drive Upload)

- [ ] **REQ-01**: Implement `MediaPauseController` using Android `AudioManager` to request transient audio focus (`AUDIOFOCUS_GAIN_TRANSIENT`) upon break trigger, causing running songs and videos to pause.
- [ ] **REQ-02**: Ensure `MediaPauseController` abandons audio focus upon break completion, dismissal, snooze, or skip, allowing paused media to automatically resume.
- [ ] **REQ-03**: Integrate `MediaPauseController` into `FloatingOverlayManager` and `LookAwayService` (as well as `BreakActivity` fallback).
- [ ] **REQ-04**: Redesign `FloatingAnimeOverlayView` to adapt dynamically to landscape mode (`Configuration.ORIENTATION_LANDSCAPE`) with a balanced two-column layout (anime sticker left, countdown dial & controls right).
- [ ] **REQ-05**: Redesign `BreakScreen` to similarly adapt cleanly to landscape mode.
- [ ] **REQ-06**: Cleanse all UI screens of emojis (`👁️`, `🔬`, `⚡`, `✨`, `🌟`, etc.), sparkles (`AutoAwesome`), and fluffy AI marketing copy.
- [ ] **REQ-07**: Modernize typography, button hierarchy, and cards in `DashboardScreen`, `SettingsDialog`, `AnimeCharacterPickerModal`, and overlay screens to a sleek minimalist dark utility aesthetic.
- [ ] **REQ-08**: Pass all existing and new unit tests (`./gradlew test`).
- [ ] **REQ-09**: Compile the new debug/release APK (`./gradlew assembleDebug`).
- [ ] **REQ-10**: Upload the built APK to Google Drive folder `Lookaway` (`1sDX0yXR4dipOZknZ4HzUujNnZ_GJ3yno`) and verify successful upload.
- [ ] **REQ-11**: Commit and push changes to GitHub (`origin main`) using personal SSH credentials.
