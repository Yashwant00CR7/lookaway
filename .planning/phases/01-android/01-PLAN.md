---
phase: 01-android
plan: 01
type: execute
tracer_first: true
---

# Plan: LookAway Android Complete Implementation

## Objective
Build a lightweight, battery-friendly, privacy-focused Android app to enforce the 20-20-20 eye strain rule by tracking continuous screen-on time, pausing/resetting intelligently on screen lock, launching a soothing 20-second break overlay, and displaying today's eye health statistics.

## Requirements Addressed
- REQ-01, REQ-02, REQ-03, REQ-04, REQ-05, REQ-06, REQ-07, REQ-08, REQ-09

## Execution Waves

### Wave 1: Tracer Slice (Scaffolding & Core State Machine with TDD)
<!-- The tracer slice is the thinnest production-quality foundation connecting core logic to verified backpressure -->
#### Task 1.1: Android Project Scaffolding
- **Type:** auto
- **Objective:** Initialize Gradle project structure for LookAway Android with Kotlin 2.0, Jetpack Compose, Material 3, and SDK 35.
- **Target Files:**
  - `android/settings.gradle.kts`
  - `android/build.gradle.kts`
  - `android/gradle.properties`
  - `android/local.properties`
  - `android/app/build.gradle.kts`
  - `android/app/src/main/AndroidManifest.xml`
- **Acceptance Criteria:** `./gradlew --version` and project builds cleanly.
- **Verification Command:** `./gradlew projects`

#### Task 1.2: Timer State Machine & Smart Reset Policy (TDD)
- **Type:** tdd
- **Objective:** Implement `TimerStateMachine` and `SmartResetPolicy` handling screen on/off events, countdown ticks, expiration triggers, and the 20s screen-off reset rule.
- **Target Files:**
  - `android/app/src/main/java/com/lookaway/core/TimerStateMachine.kt`
  - `android/app/src/main/java/com/lookaway/core/SmartResetPolicy.kt`
  - `android/app/src/test/java/com/lookaway/core/TimerStateMachineTest.kt`
  - `android/app/src/test/java/com/lookaway/core/SmartResetPolicyTest.kt`
- **Acceptance Criteria:** 100% of unit tests pass verifying state transitions and smart reset logic.
- **Verification Command:** `./gradlew test`

#### Task 1.3: LookAway Preferences & Daily Stats Repository (TDD)
- **Type:** tdd
- **Objective:** Implement repository for persistent settings (work duration, break duration, audio/vibration) and daily metrics (breaks completed, breaks skipped, screen time).
- **Target Files:**
  - `android/app/src/main/java/com/lookaway/data/LookAwayPreferences.kt`
  - `android/app/src/main/java/com/lookaway/data/StatsRepository.kt`
  - `android/app/src/test/java/com/lookaway/data/StatsRepositoryTest.kt`
- **Acceptance Criteria:** Tests verify today's date rollover and accurate metric increments.
- **Verification Command:** `./gradlew test`

---

### Wave 2: Foreground Service & Full-Screen Break Overlay
#### Task 2.1: Screen Receiver & Foreground Service
- **Type:** auto
- **Objective:** Create `ScreenBroadcastReceiver` and `LookAwayService` with low-priority notification showing live countdown, handling lifecycle and screen state changes.
- **Target Files:**
  - `android/app/src/main/java/com/lookaway/service/ScreenBroadcastReceiver.kt`
  - `android/app/src/main/java/com/lookaway/service/LookAwayService.kt`
  - `android/app/src/main/java/com/lookaway/service/NotificationHelper.kt`
- **Acceptance Criteria:** Service manages notification channels, registers screen receivers, and updates notification content.
- **Verification Command:** `./gradlew compileDebugKotlin`

#### Task 2.2: Full-Screen Intent Break Trigger & Audio/Haptic Manager
- **Type:** auto
- **Objective:** Implement full-screen intent dispatch for incoming breaks, along with gentle audio chime synthesizer/tone generator and haptic buzzer.
- **Target Files:**
  - `android/app/src/main/java/com/lookaway/service/BreakAlertManager.kt`
  - `android/app/src/main/java/com/lookaway/ui/sound/SoundManager.kt`
- **Acceptance Criteria:** Break alerts trigger high-priority full-screen intent and sound/haptic cues are cleanly handled.
- **Verification Command:** `./gradlew compileDebugKotlin`

#### Task 2.3: Break Screen UI (Jetpack Compose)
- **Type:** auto
- **Objective:** Implement `BreakActivity` with a peaceful 20-second circular countdown timer, 20-20-20 distance guidance, completion chime, Skip button, and Snooze (5m) button.
- **Target Files:**
  - `android/app/src/main/java/com/lookaway/ui/breakoverlay/BreakActivity.kt`
  - `android/app/src/main/java/com/lookaway/ui/breakoverlay/BreakScreen.kt`
- **Acceptance Criteria:** Fullscreen activity displays 20s animated countdown and handles Skip/Snooze/Complete callbacks.
- **Verification Command:** `./gradlew compileDebugKotlin`

---

### Wave 3: Compose Dashboard UI, Polish & Verification
#### Task 3.1: Material 3 Dashboard & Live Status Screen
- **Type:** auto
- **Objective:** Implement `MainActivity` with Material 3 theme, hero countdown circular progress, Start/Stop service toggle, today's eye health statistics card, and 20-20-20 educational banner.
- **Target Files:**
  - `android/app/src/main/java/com/lookaway/ui/MainActivity.kt`
  - `android/app/src/main/java/com/lookaway/ui/dashboard/DashboardScreen.kt`
  - `android/app/src/main/java/com/lookaway/ui/dashboard/DashboardViewModel.kt`
  - `android/app/src/main/java/com/lookaway/ui/theme/Theme.kt`
- **Acceptance Criteria:** Dashboard reflects service state, lets user toggle tracking, and shows live stats.
- **Verification Command:** `./gradlew compileDebugKotlin`

#### Task 3.2: Settings & Customization Modal
- **Type:** auto
- **Objective:** Add bottom sheet or settings dialog to adjust work interval (e.g. 20m), break interval (e.g. 20s), and sound/vibration toggles.
- **Target Files:**
  - `android/app/src/main/java/com/lookaway/ui/settings/SettingsDialog.kt`
- **Acceptance Criteria:** User can change preferences and changes take effect immediately in the service.
- **Verification Command:** `./gradlew compileDebugKotlin`

#### Task 3.3: Full Build, Verification & APK Generation
- **Type:** auto
- **Objective:** Run full test suite, verify all requirements, build debug APK, and generate verification report.
- **Target Files:**
  - `.planning/phases/01-android/VERIFICATION.md`
  - `.planning/phases/01-android/SUMMARY.md`
- **Acceptance Criteria:** `./gradlew test` passes, `./gradlew assembleDebug` produces `lookaway-debug.apk`.
- **Verification Command:** `./gradlew assembleDebug test`
