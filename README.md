# The Purple Launcher

> **"Android, in your own frequency."**  
> *Version v0.1*

The Purple Launcher is a modern, privacy-first, engineering-grade Android **HOME** application crafted in **Kotlin** and **Jetpack Compose**. Built with a deep monochrome canvas and surgical purple accents, it delivers a minimal yet expressive home screen experience.

---

## ✦ Core Features

- **Real Android HOME Launcher**: Registers as a full `android.intent.category.HOME` default launcher.
- **Monochrome & Purple Dynamic Theming**:
  - Pure Monochrome base with customizable high-contrast purple accents.
  - Automatic luminance detection from wallpaper to select optimal foreground contrast.
  - Themes: *Dynamic*, *Dark*, *Light*, *AMOLED Pure Black*, *Pure White*, *Pure Monochrome*, *Custom Purple*.
- **Wallpaper Studio & Processing Pipeline**:
  - Non-destructive image pipeline: Grayscale conversion, Luminance analysis, Contrast adjustment, Box/Stack blur, Film grain noise, Vignette framing, Darkening scrim.
  - Presets: *Pure*, *Soft*, *Noir*, *Film*, *Matte*, *High Contrast*.
- **Profiles & Context Switching**:
  - Multi-profile support (*HOME*, *WORK*, *DEV*, *STUDY*, *RELAX*, *TRAVEL*, *GAMING* + custom profiles).
  - Isolated dock configurations, favorite apps, custom wallpapers, and developer spaces per profile.
- **Developer Hub & Telemetry**:
  - **Package Inspector**: Real-time inspection of APK target SDK, min SDK, APK size, install/update timestamps, declared permissions, signatures (SHA-256), and component counts.
  - **Developer Tools**: Fast offline JSON Formatter & Validator, Base64 Encoder/Decoder, SHA-256 / MD5 Hash calculator, Regex tester.
  - **System Telemetry**: Device model, RAM usage, display DPI, resolution, and battery state.
  - **Developer Space**: Git repository monitor, branch badge, sprint task checklist, and pinned tools.
- **Universal Spatial Search**:
  - Fast fuzzy search matching across applications, Android system settings, developer utilities, spaces, profiles, contacts, and web shortcuts.
- **Glass Surfaces & Motion Design**:
  - Translucent glass containers with subtle border strokes, fluid gestures, and spring animations.
- **Privacy First & Offline**:
  - No ads, no analytics tracking, local Room database persistence.

---

## ✦ Architecture & Technology Stack

- **UI**: Jetpack Compose, Material Design 3 (M3)
- **Language**: Kotlin 2.0+ (100% Coroutines & Flow)
- **Architecture**: Clean MVVM (Model-View-ViewModel + Repository pattern)
- **Persistence**: Room Database (SQLite) + Jetpack DataStore Preferences
- **Image Processing**: Canvas, ColorMatrix, Stack Blur & Grain synthesis
- **System Integration**: `LauncherApps`, `PackageManager`, `AppWidgetHost`, `ContactsContract`
- **CI/CD**: GitHub Actions workflow for automated Release & Debug APK builds

---

## ✦ Building from Source

### Prerequisites
- Android Studio Ladybug / Meerkat or later
- JDK 17 or JDK 21
- Android SDK (API 34+)

### Build Commands

```bash
# Clone the repository
git clone https://github.com/purple-launcher/purple-launcher.git
cd purple-launcher

# Run unit & robolectric tests
./gradlew testDebugUnitTest

# Assemble Debug APK
./gradlew assembleDebug

# Assemble Release APK (uses signing config if environment secrets are present)
./gradlew assembleRelease
```

---

## ✦ GitHub Actions CI/CD & Automated Releases

The repository includes `.github/workflows/build.yml` configured to:
1. Run local JVM unit and Robolectric tests on every PR and commit to `main`.
2. Generate debug and release APK artifacts on every push.
3. Automatically publish a **GitHub Release with signed APKs** whenever a tag (`v*`) is pushed.

---

## ✦ License

Distributed under the Apache License 2.0. See `LICENSE` for more information.
