# Requirements: LookAway (v1.0 Android)

- [ ] **REQ-01**: Background Foreground Service with persistent low-priority status notification tracking active screen-on time.
- [ ] **REQ-02**: Smart Reset logic: Screen off >= 20 seconds resets the work timer; screen off < 20 seconds pauses and resumes on screen on.
- [ ] **REQ-03**: Full-Screen Intent notification triggering `BreakActivity` when work duration (default 20 minutes) is reached.
- [ ] **REQ-04**: Calming 20-second break countdown screen in Jetpack Compose with animated circular progress indicator and 20-20-20 rule guidance.
- [ ] **REQ-05**: Gentle sound chime and haptic feedback upon break completion, with functional Skip and Snooze (5 min) buttons.
- [ ] **REQ-06**: Dashboard Home Screen with live tracking toggle, remaining countdown display, and today's stats card (breaks taken, breaks skipped, total screen time).
- [ ] **REQ-07**: Preferences sheet/dialog allowing configuration of work interval (e.g. 20m, 30m), break duration (e.g. 20s, 30s), and sound/vibration toggles.
- [ ] **REQ-08**: Comprehensive unit tests covering TimerStateMachine, SmartResetPolicy, and statistics repository.
- [ ] **REQ-09**: Clean Gradle build producing `lookaway-debug.apk`.
