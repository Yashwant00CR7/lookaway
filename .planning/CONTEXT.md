# Phase Context: 02-anime-overlay (Anime Floating Animation Overlay)

## Problem & User Intent
When 20 minutes of continuous screen time expires, rather than only relying on a full-screen app switch, the user wants a delightful, non-jarring animated overlay directly on top of *whichever app they are currently using* (YouTube, games, messaging, browser).
The animation must feature a famous anime character (e.g. Goku, Naruto, Luffy, Anya, Pikachu, Totoro) with a **transparent background** (isolated character sticker, no solid box) so only the character appears on the screen alongside the 20-second countdown and 20-20-20 guidance.
Users must also be able to choose from a curated web gallery or paste any custom web URL of their own choice.

## Mental Model & Core Abstractions
1. **AnimeCharacter Model & Presets:**
   - Pre-configured transparent anime character sticker animations from reliable web CDNs (Giphy/Tenor transparent stickers or SVG/WebP).
   - Custom URL mode allowing any transparent GIF or animated WebP URL.
   - Stored in `StatsRepository` / `UserPreferences`.
2. **FloatingOverlayManager:**
   - Checks `Settings.canDrawOverlays(context)`.
   - Uses `WindowManager` to inflate a `ComposeView` with `TYPE_APPLICATION_OVERLAY`, `FLAG_NOT_TOUCH_MODAL` / `FLAG_WATCH_OUTSIDE_TOUCH`.
   - Safely adds and removes overlay views from the window hierarchy without memory leaks or window token crashes.
3. **FloatingBreakOverlayView (Compose):**
   - Translucent soothing background scrim (`Color(0xCC0B132B)`).
   - Animated anime character rendered using Coil's `AsyncImage` with `ImageDecoderDecoder` / `GifDecoder` for transparent animation.
   - Circular animated 20-second countdown timer.
   - Action buttons: "Skip" and "Snooze 5m".
   - Plays chime sound and haptic pulse on finish, then auto-removes overlay.
4. **Overlay Permission Flow:**
   - In `MainActivity`: Detects `Settings.canDrawOverlays(context)`.
   - Shows a dedicated "Enable Anime Overlay over Apps" card with 1-tap intent to `Settings.ACTION_MANAGE_OVERLAY_PERMISSION`.
   - If permission is absent when break triggers, gracefully falls back to `BreakActivity` (full screen intent).

## Verification & Backpressure Seams
- Unit Tests: `./gradlew test` (Robolectric / JUnit tests for AnimeCharacter data layer and storage).
- Compilation: `./gradlew compileDebugKotlin`
- Assembly: `./gradlew assembleDebug`
