# Phase Context: Media Pause, Landscape Support, Clean UI & Drive Upload

## Problem & User Intent
The user requested four key enhancements for LookAway:
1. Pause active background/foreground music or video playback (e.g., Spotify, YouTube, browsers, media players) whenever the break overlay triggers.
2. Support horizontal phone orientation (landscape mode) inside the floating break overlay so it renders elegantly without squishing or clipping when holding the device sideways.
3. Overhaul the user interface to remove mostly all emojis, sparkle badges, and AI-generated marketing slop, transitioning to a clean, professional, minimalist dark utility aesthetic.
4. Build the updated APK and upload it directly to the user's `Lookaway` folder in Google Drive.

## Mental Model & Core Abstractions
- **Media Focus Controller (`MediaPauseController`):** Encapsulates Android's `AudioManager` and `AudioFocusRequest` (API 26+) / legacy audio focus API. Requests transient audio focus on break display and abandons audio focus on break exit, allowing audio/video players to pause and seamlessly auto-resume.
- **Adaptive Overlay Layout (`FloatingAnimeOverlayView`):** Detects device orientation (portrait vs landscape) or dimensions via `BoxWithConstraints` / `LocalConfiguration.current.orientation`. In portrait, renders the vertical column; in landscape, renders a balanced side-by-side two-column view (character sticker left, countdown ring & controls right).
- **Minimalist Design System:** Pure technical clarity. Standard Material Symbols, clean slate/zinc color tokens, concise labels ("Start Tracking", "Active", "Paused", "20-20-20 Rule"), elimination of childish emojis across all screens.
- **Drive Uploader:** Node script utilizing the user's authenticated Google Drive credentials from keychain to upload the generated APK into the `Lookaway` folder (`1sDX0yXR4dipOZknZ4HzUujNnZ_GJ3yno`).

## Locked Architectural Decisions
- **Decision 1 (Media Pause):** Transient audio focus (`AUDIOFOCUS_GAIN_TRANSIENT`) with auto-abandon on break completion/dismiss/snooze, ensuring clean auto-resume of playback.
- **Decision 2 (Landscape Overlay):** Two-column split layout for landscape mode, with safety paddings for system insets/notches.
- **Decision 3 (UI Cleanup):** Complete removal of emojis, sparkles, and exaggerated AI copy; streamlined dark utility theme with subtle slate/cyan accents.
- **Decision 4 (Drive Upload):** Direct multipart upload to Google Drive v3 endpoint for the `Lookaway` folder ID.
- **Decision 5 (Git Identity):** Personal account `Yashwant00CR7` / `10a19yashwant@gmail.com` on `github-personal`.

## Verification & Backpressure Seams
- Unit Tests: `./gradlew test` (verifying state machine, repositories, media focus controller logic)
- Build Verification: `./gradlew assembleDebug` (compilation of APK)
- Drive Upload Verification: Querying the Google Drive folder files to confirm uploaded APK.
