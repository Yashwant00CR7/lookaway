---
phase: 02-anime-overlay
plan: 01
type: execute
tracer_first: true
---

# Plan: Floating Anime Animation Overlay Over Other Apps

## Objective
Enable a delightful, non-intrusive floating anime animation overlay that displays on top of whatever app the user is in (WhatsApp, YouTube, browser, games) when the 20-minute screen timer expires. Features transparent background anime characters (Goku, Naruto, Luffy, Anya, Pikachu, Totoro) chosen from the web or via custom URL, a 20-second circular countdown ring, and audio/haptic feedback.

## Requirements Addressed
- REQ-10, REQ-11, REQ-12, REQ-13, REQ-14, REQ-15, REQ-16, REQ-17, REQ-18

## Execution Waves

### Wave 1: Dependencies & Anime Character Data Layer (TDD)
#### Task 1.1: Dependencies & Coil Animated Decoder Setup
- **Type:** auto
- **Objective:** Add Coil 2.7.0 and Coil GIF/WebP decoders to `build.gradle.kts`, plus internet permission in `AndroidManifest.xml`.
- **Target Files:**
  - `android/app/build.gradle.kts`
  - `android/app/src/main/AndroidManifest.xml`
- **Acceptance Criteria:** Coil with GIF decoder resolves and compiles cleanly.
- **Verification Command:** `./gradlew compileDebugKotlin`

#### Task 1.2: Anime Character Model & Presets (TDD)
- **Type:** tdd
- **Objective:** Implement `AnimeCharacter` with transparent background sticker animation presets (Goku, Naruto, Luffy, Anya, Pikachu, Totoro) and custom URL support, integrated into `StatsRepository`.
- **Target Files:**
  - `android/app/src/main/java/com/lookaway/data/AnimeCharacter.kt`
  - `android/app/src/main/java/com/lookaway/data/UserPreferences.kt`
  - `android/app/src/main/java/com/lookaway/data/StatsRepository.kt`
  - `android/app/src/test/java/com/lookaway/data/AnimeCharacterTest.kt`
- **Acceptance Criteria:** Unit tests verify character preset lookup, custom URL selection, and persistence.
- **Verification Command:** `./gradlew test`

---

### Wave 2: Floating WindowManager Overlay Engine
#### Task 2.1: Floating Overlay Window Manager
- **Type:** auto
- **Objective:** Implement `FloatingOverlayManager` using `WindowManager` (`TYPE_APPLICATION_OVERLAY`) with proper lifecycle (adding ComposeView, handling back/home, removing cleanly on dismiss or completion).
- **Target Files:**
  - `android/app/src/main/java/com/lookaway/service/FloatingOverlayManager.kt`
  - `android/app/src/main/java/com/lookaway/service/LookAwayService.kt`
- **Acceptance Criteria:** Overlay can be displayed and dismissed cleanly over other apps without crashing or leaking window tokens.
- **Verification Command:** `./gradlew compileDebugKotlin`

#### Task 2.2: Floating Anime Break Overlay Compose UI
- **Type:** auto
- **Objective:** Implement `FloatingAnimeOverlayView` in Jetpack Compose: translucent midnight backdrop, Coil transparent anime sticker animation, 20s countdown ring, distance guidance, and Skip/Snooze buttons.
- **Target Files:**
  - `android/app/src/main/java/com/lookaway/ui/breakoverlay/FloatingAnimeOverlayView.kt`
- **Acceptance Criteria:** Renders transparent animated GIF/WebP with countdown and actions.
- **Verification Command:** `./gradlew compileDebugKotlin`

---

### Wave 3: Anime Gallery UI, Permission Guidance & Release APK
#### Task 3.1: Character Picker Modal & Custom URL Input
- **Type:** auto
- **Objective:** Add an anime character selector sheet/dialog with cards for Goku, Naruto, Luffy, Anya, Pikachu, Totoro, live animation preview, and custom URL text input.
- **Target Files:**
  - `android/app/src/main/java/com/lookaway/ui/settings/AnimeCharacterPickerModal.kt`
- **Acceptance Criteria:** Users can select presets or input a custom URL and preview the animation immediately.
- **Verification Command:** `./gradlew compileDebugKotlin`

#### Task 3.2: Dashboard Integration & Permission Card
- **Type:** auto
- **Objective:** Update `DashboardScreen` with a "Display Over Other Apps" permission setup card (when permission is missing) and a "Test Anime Overlay" button.
- **Target Files:**
  - `android/app/src/main/java/com/lookaway/ui/dashboard/DashboardScreen.kt`
  - `android/app/src/main/java/com/lookaway/ui/dashboard/DashboardViewModel.kt`
- **Acceptance Criteria:** Dashboard shows overlay permission status and provides immediate test trigger.
- **Verification Command:** `./gradlew compileDebugKotlin`

#### Task 3.3: Full Build, Verification & Updated APK
- **Type:** auto
- **Objective:** Run full test suite, verify all requirements, build debug APK, and generate verification report.
- **Target Files:**
  - `.planning/phases/02-anime-overlay/VERIFICATION.md`
  - `.planning/phases/02-anime-overlay/SUMMARY.md`
- **Acceptance Criteria:** `./gradlew test` passes, `./gradlew assembleDebug` produces updated `lookaway-debug.apk`.
- **Verification Command:** `./gradlew test assembleDebug`
