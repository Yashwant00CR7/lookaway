# Roadmap: LookAway

## Milestone 1: LookAway Android (v1.0)
Focused on solving digital eye strain and dry eyes with battery-efficient screen time tracking and 20-20-20 rule enforcement on Android.

### Phase 1: Core Engine & State Machine (Tracer)
- [ ] Task 1.1: Project scaffolding & Gradle setup (Kotlin 2.0, Compose, AGP 8.7).
- [ ] Task 1.2: TimerStateMachine & SmartResetPolicy logic with TDD unit tests.
- [ ] Task 1.3: LookAwayPreferences & StatsRepository for persistent settings and daily counts.

### Phase 2: Foreground Service & Full-Screen Break Intent
- [ ] Task 2.1: ScreenReceiver (`ACTION_SCREEN_ON` / `ACTION_SCREEN_OFF`) & LookAwayService foreground lifecycle.
- [ ] Task 2.2: Full-Screen Intent notification channel & high-priority break trigger.
- [ ] Task 2.3: BreakActivity Jetpack Compose fullscreen UI with animated countdown, sound/haptic feedback, Skip & Snooze actions.

### Phase 3: Dashboard UI & Settings
- [ ] Task 3.1: Material 3 Dashboard screen with live countdown ring, status toggle, and today's statistics card.
- [ ] Task 3.2: Settings dialog/sheet for custom work/break intervals and sound/vibration toggles.
- [ ] Task 3.3: Integration verification & E2E smoke tests.

### Phase 4: Verification & Release Packaging
- [ ] Task 4.1: Final test suite execution & verification report.
- [ ] Task 4.2: APK build & artifact verification.

---

## Milestone 2: LookAway macOS Desktop (v2.0)
Native macOS Menu Bar application (Swift) featuring system idle detection, menu bar countdown, and gentle break overlay.
