# Requirements: LookAway

## Milestone 1: Core Engine & Screen Time (v1.0)
- [x] **REQ-01**: Background Foreground Service with persistent low-priority status notification tracking active screen-on time.
- [x] **REQ-02**: Smart Reset logic: Screen off >= 20 seconds resets the work timer; screen off < 20 seconds pauses and resumes on screen on.
- [x] **REQ-03**: Full-Screen Intent notification triggering `BreakActivity` when work duration (default 20 minutes) is reached.
- [x] **REQ-04**: Calming 20-second break countdown screen in Jetpack Compose with animated circular progress indicator and 20-20-20 rule guidance.
- [x] **REQ-05**: Gentle sound chime and haptic feedback upon break completion, with functional Skip and Snooze (5 min) buttons.
- [x] **REQ-06**: Dashboard Home Screen with live tracking toggle, remaining countdown display, and today's stats card.
- [x] **REQ-07**: Preferences sheet/dialog allowing configuration of work interval, break duration, and audio/vibration toggles.
- [x] **REQ-08**: Comprehensive unit tests covering TimerStateMachine, SmartResetPolicy, and statistics repository.
- [x] **REQ-09**: Clean Gradle build producing `lookaway-debug.apk`.

## Milestone 1.1: Anime Floating Animation Overlay (v1.1)
- [x] **REQ-10**: Support `SYSTEM_ALERT_WINDOW` permission with a 1-tap guided permission card on the dashboard and fallback to full-screen activity when permission is missing.
- [x] **REQ-11**: Integrate Coil 2.7.0 with animated GIF and animated WebP decoders (`ImageDecoderDecoder` / `GifDecoder`) for hardware-accelerated transparent animation playback.
- [x] **REQ-12**: `AnimeCharacter` model with transparent background animation presets (Goku, Naruto, Luffy, Anya, Pikachu, Totoro) + custom Web URL input support.
- [x] **REQ-13**: `FloatingOverlayManager` to display and remove a Compose-based floating overlay via Android `WindowManager` (`TYPE_APPLICATION_OVERLAY`) directly over any running app.
- [x] **REQ-14**: Floating overlay Compose UI displaying the transparent animated anime character, 20s countdown ring, eye relaxation tips, and Skip/Snooze buttons.
- [x] **REQ-15**: Anime Character Picker modal on the dashboard with live preview and custom GIF/WebP URL input.
- [x] **REQ-16**: Instant "Test Anime Overlay" button on the dashboard to test the floating overlay over other apps immediately.
- [x] **REQ-17**: Automated unit test suite verifying `AnimeCharacter` storage and preference persistence.
- [x] **REQ-18**: Build updated debug APK with all anime floating overlay features.
