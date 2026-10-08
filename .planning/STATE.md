# GSD Project State

**Active Milestone:** v1.0 - LookAway Android
**Current Phase:** Phase 1 - LookAway Android App
**Status:** In Progress
**Last Updated:** 2026-10-08 15:48:00

## Active Decisions & Constraints
- **Platform Strategy:** Android app first, followed by macOS desktop app in Milestone 2.
- **Repository Structure:** Monorepo at `~/Downloads/Personal Projects/lookaway/` containing `android/` and later `macos/`.
- **Tracking Mechanism:** Screen-on time tracking via Android Foreground Service (`ACTION_SCREEN_ON` / `ACTION_SCREEN_OFF`). Camera iris tracking rejected as battery-draining and privacy-invasive.
- **Smart Reset Policy:** If screen is off for >= 20 seconds, eyes are considered rested and the 20-minute timer automatically resets. If off for < 20 seconds, the timer pauses and resumes.
- **Break Experience:** Full-screen intent launching a calm 20-second countdown Activity over any open app without needing special draw-over-apps permission. Audio chime and haptics on completion, plus Skip/Snooze buttons.
- **Dashboard & Features:** Material 3 Jetpack Compose UI showing live status, today's break stats (completed vs skipped), and configurable work/break intervals and audio/vibration toggles.
- **Git Identity:** Yashwant00CR7 / 10a19yashwant@gmail.com, personal SSH host `github-personal`.

## Current Position
- Phase: 01-android
- Total Plans: 1
- Completed Plans: 0
- Next Action: Execute Plan 01 (Wave 1: Tracer Engine)
