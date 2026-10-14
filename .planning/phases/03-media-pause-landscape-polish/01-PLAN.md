---
phase: 03-media-pause-landscape-polish
plan: 01
type: execute
tracer_first: true
---

# Plan: Media Pause, Landscape Overlay, Clean UI & Drive Upload

## Objective
Implement automatic media/video pause-and-resume via audio focus, responsive side-by-side landscape overlay support, a stripped-back minimalist dark UI (zero emojis and zero AI slop), and deploy the updated APK to Google Drive and GitHub.

## Requirements Addressed
- REQ-01, REQ-02, REQ-03, REQ-04, REQ-05, REQ-06, REQ-07, REQ-08, REQ-09, REQ-10, REQ-11

## Execution Waves

### Wave 1: Tracer Slice (Media Pause & Audio Focus Engine)
#### Task 1.1: Build `MediaPauseController` & Audio Focus Management
- **Type:** tdd
- **Objective:** Create `MediaPauseController` wrapping `AudioManager` with transient audio focus request/abandon, and write unit tests for state tracking.
- **Target Files:**
  - `android/app/src/main/java/com/lookaway/service/MediaPauseController.kt`
  - `android/app/src/test/java/com/lookaway/service/MediaPauseControllerTest.kt`
- **Acceptance Criteria:** `requestPause()` requests `AUDIOFOCUS_GAIN_TRANSIENT` with audio attributes usage `USAGE_MEDIA`; `resume()` abandons audio focus cleanly; state transitions verified via unit tests.
- **Verification Command:** `cd android && ./gradlew test`

#### Task 1.2: Connect Media Pause into Overlay Lifecycle
- **Type:** auto
- **Objective:** Connect `MediaPauseController` into `FloatingOverlayManager`, `LookAwayService`, and `BreakActivity`.
- **Target Files:**
  - `android/app/src/main/java/com/lookaway/service/FloatingOverlayManager.kt`
  - `android/app/src/main/java/com/lookaway/service/LookAwayService.kt`
  - `android/app/src/main/java/com/lookaway/ui/breakoverlay/BreakActivity.kt`
- **Acceptance Criteria:** Showing the overlay or break screen triggers pause; dismissing, completing, or snoozing resumes audio focus.
- **Verification Command:** `cd android && ./gradlew assembleDebug`

### Wave 2: Responsive Landscape Overlay & Clean Minimalist UI
#### Task 2.1: Implement Landscape Two-Column Support in Overlays
- **Type:** auto
- **Objective:** Adapt `FloatingAnimeOverlayView` and `BreakScreen` to detect landscape orientation and render a side-by-side two-column layout (left: sticker, right: countdown ring and actions).
- **Target Files:**
  - `android/app/src/main/java/com/lookaway/ui/breakoverlay/FloatingAnimeOverlayView.kt`
  - `android/app/src/main/java/com/lookaway/ui/breakoverlay/BreakScreen.kt`
- **Acceptance Criteria:** In landscape (`orientation == ORIENTATION_LANDSCAPE`), overlay displays two side-by-side columns with no clipping, properly sized elements, and centered gravity.
- **Verification Command:** `cd android && ./gradlew test assembleDebug`

#### Task 2.2: Purge Emojis and AI Slop across Dashboard and Modals
- **Type:** auto
- **Objective:** Strip all emojis, sparkle badges, and AI promotional copy across `DashboardScreen`, `SettingsDialog`, `AnimeCharacterPickerModal`, and `BreakScreen`, updating them to a clean minimalist dark utility theme with concise technical microcopy.
- **Target Files:**
  - `android/app/src/main/java/com/lookaway/ui/dashboard/DashboardScreen.kt`
  - `android/app/src/main/java/com/lookaway/ui/settings/SettingsDialog.kt`
  - `android/app/src/main/java/com/lookaway/ui/settings/AnimeCharacterPickerModal.kt`
  - `android/app/src/main/java/com/lookaway/data/AnimeCharacter.kt`
- **Acceptance Criteria:** Zero emojis in code strings; no `AutoAwesome` sparkle icon; crisp Material Symbols and technical typography; all tests passing.
- **Verification Command:** `cd android && ./gradlew test assembleDebug`

### Wave 3: Compilation, Google Drive Upload & GitHub Push
#### Task 3.1: Build Final APK & Run Verification Suite
- **Type:** auto
- **Objective:** Run full test suite and build output APK (`lookaway-debug.apk` and `lookaway-v1.2.0.apk`).
- **Target Files:**
  - `lookaway-debug.apk`
  - `lookaway-v1.2.0.apk`
- **Acceptance Criteria:** Tests pass 100%, APK compiles cleanly.
- **Verification Command:** `cd android && ./gradlew test assembleDebug`

#### Task 3.2: Upload APK to Google Drive
- **Type:** auto
- **Objective:** Upload the newly built APK to the user's `Lookaway` folder in Google Drive (`1sDX0yXR4dipOZknZ4HzUujNnZ_GJ3yno`) using Drive API.
- **Target Files:**
  - Upload script / direct API call
- **Acceptance Criteria:** File appears in Drive folder listing with valid ID and size.
- **Verification Command:** Verification via Drive API files list.

#### Task 3.3: Git Commit & Push
- **Type:** auto
- **Objective:** Commit all changes and push to `origin main` using personal SSH host `github-personal`.
- **Target Files:**
  - All modified files
- **Acceptance Criteria:** `git status` clean, pushed to `Yashwant00CR7/lookaway`.
- **Verification Command:** `git status && git log -n 1`
