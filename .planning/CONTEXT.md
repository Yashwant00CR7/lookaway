# Phase Context: 01-android (LookAway Android)

## Problem & User Intent
Staring at screens continuously causes digital eye strain and dry eyes due to a 50% drop in blink rate and continuous ciliary muscle accommodation. The medically proven solution (endorsed by the American Academy of Ophthalmology) is the **20-20-20 rule**: every 20 minutes, look at an object 20 feet away for at least 20 seconds.
Camera-based iris/blink tracking was evaluated and rejected as battery-draining, error-prone, and privacy-invasive overkill. Instead, LookAway tracks continuous screen-on time via lightweight OS broadcast receivers and triggers a calming full-screen 20-second break prompt.

## Mental Model & Core Abstractions
1. **TimerStateMachine:**
   - States: `STOPPED`, `RUNNING`, `PAUSED`, `BREAK_ACTIVE`.
   - Transitions driven by:
     - User Start/Stop toggles.
     - Screen on/off broadcasts.
     - Second-by-second coroutine ticks.
     - Timer expiration (triggers break).
     - Break completion or skip/snooze actions.
2. **SmartResetPolicy:**
   - Detects `screen_off_timestamp`.
   - When screen turns on again: `elapsedScreenOff = now - screen_off_timestamp`.
   - If `elapsedScreenOff >= breakDurationSeconds` (default 20s): resets timer to full interval (default 20 min).
   - If `elapsedScreenOff < breakDurationSeconds`: preserves accumulated screen time and resumes countdown.
3. **LookAwayForegroundService:**
   - Maintains continuous operation in the background using an ongoing notification with current countdown status.
   - Registers dynamic receiver for `Intent.ACTION_SCREEN_ON` and `Intent.ACTION_SCREEN_OFF`.
   - Fires a high-priority Full-Screen Intent notification launching `BreakActivity` when interval expires.
4. **BreakOverlayActivity:**
   - Calm, non-jarring fullscreen Compose UI.
   - Smooth animated circular progress ring counting down 20 seconds.
   - Soothing instruction: "Look at an object at least 20 feet (6m) away".
   - Plays gentle completion sound chime and haptic buzz.
   - "Skip" and "Snooze (5m)" actions.
5. **LookAwayRepository & Preferences:**
   - Stores settings: work duration (default 20 min), break duration (default 20 sec), sound enabled, vibration enabled.
   - Stores daily statistics: today's date, breaks completed count, breaks skipped count, total tracked screen-on seconds.

## Verification & Backpressure Seams
- Unit Tests: `./gradlew test` (Robolectric / JUnit 4 tests validating StateMachine transitions, SmartResetPolicy elapsed times, and statistics calculations)
- Compilation & Lint: `./gradlew compileDebugKotlin`
- Build & Packaging: `./gradlew assembleDebug` (generates installable `app-debug.apk`)
