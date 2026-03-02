# Star Control 2: The Ur-Quan Masters — Android Port

An Android port of [The Ur-Quan Masters](http://sc2.sourceforge.net/), the open-source version of Star Control II, built with **SDL 2.30.10**.

## Features

- Full UQM 0.8.0 gameplay on Android
- **Native 64-bit** ARM build (`arm64-v8a`), with 32-bit (`armeabi-v7a`) support
- SDL2-based rendering and input
- Touch overlay controls for navigation, combat, and menus
- Supports Android 5.0+ (API 21), targeting API 35
- 3DO music and voice pack support

## Building

### Prerequisites

- **Android Studio** (2024.x or later recommended) — or Gradle CLI
- **Android SDK** with:
  - SDK Platform API 35
  - NDK (latest stable, e.g. 27.x)
  - CMake (3.22+)
- **Git** (for cloning submodules)
- **Java 17+** (bundled with Android Studio, or install separately for CLI builds)

### Clone

```bash
git clone --recurse-submodules https://github.com/keylimesoda/sc2-uqm.git
cd sc2-uqm
```

If you already cloned without `--recurse-submodules`:
```bash
git submodule update --init --recursive
```

### Download Content Packs

The game requires UQM content packs to run. Download them from
[UQM downloads](http://sc2.sourceforge.net/downloads.php) and place them as follows:

| File | Destination | Required? |
|---|---|---|
| `uqm-0.8.0-content.uqm` | `app/src/main/assets/content/` | **Yes** |
| `uqm-0.8.0-3domusic.uqm` | `app/src/main/assets/content/addons/` | Optional (3DO music) |
| `uqm-0.8.0-voice.uqm` | `app/src/main/assets/content/addons/` | Optional (voice acting, ~110 MB) |

### Build

Create `local.properties` pointing to your Android SDK:
```bash
echo "sdk.dir=/path/to/Android/Sdk" > local.properties
```

Then build:
```bash
# Linux / macOS
./gradlew assembleDebug

# Windows
gradlew.bat assembleDebug
```

The APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

### Install on Device / Emulator

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Project Structure

```
app/
├── jni/CMakeLists.txt    — Native build (C code via NDK)
├── src/main/java/        — Android Java layer (SDL activity, touch overlay)
├── src/main/assets/      — Game content (graphics, music, data)
deps/                     — Third-party libraries (git submodules)
├── SDL2/                 — SDL 2.30.10
├── libogg/               — libogg 1.3.5
├── libvorbis/            — libvorbis 1.3.7
├── libpng/               — libpng 1.6.43
uqm-src/                  — Patched UQM 0.8.0 game source (SDL2-compatible)
```

## Related

- [sc2-uqm-win64](https://github.com/keylimesoda/sc2-uqm-win64) — Windows 64-bit desktop build
- [The Ur-Quan Masters](http://sc2.sourceforge.net/) — Original open-source project
- [Upstream source](https://github.com/JKtheSlacker/sc2-uqm) — JKtheSlacker's sc2-uqm fork

## License

This project inherits the GPL v2 license from The Ur-Quan Masters.
