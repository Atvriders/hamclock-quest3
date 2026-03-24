# HamClock Reborn — Meta Quest 3 APK

Android WebView wrapper for [HamClock Reborn](https://hamclock-reborn.org/) on Meta Quest 3.

## What This Does

Wraps the HamClock Reborn web app in a fullscreen Android WebView, optimized for Quest 3:
- Launches in 2D panel mode on Quest 3
- Fullscreen immersive (no Android UI chrome)
- Landscape orientation
- Hardware accelerated WebView
- Offline error handling
- Keeps screen awake

## Install on Quest 3

1. Download the APK from [GitHub Actions](../../actions) (click latest run → Artifacts → hamclock-quest3-debug.zip)
2. Enable Developer Mode on your Quest 3 (Settings → System → Developer)
3. Sideload using [SideQuest](https://sidequestvr.com/) or ADB:
   ```bash
   adb install hamclock-quest3-debug.apk
   ```
4. Find "HamClock Reborn" in your Quest app library (Unknown Sources)

## Build Locally

```bash
# Requires Android SDK + JDK 17
./gradlew assembleDebug
# APK at: app/build/outputs/apk/debug/app-debug.apk
```

## Requirements

- Meta Quest 3 (or any Android 10+ device)
- WiFi connection (loads hamclock-reborn.org)
- Developer Mode enabled for sideloading

---

*Built with [Claude Code](https://claude.ai/claude-code)*
