# Contributing to The Purple Launcher

Thank you for your interest in contributing to **The Purple Launcher**!

## Development Guidelines

1. **Architecture**: Adhere to MVVM and Clean Architecture patterns. Keep business logic inside repositories and state machines within ViewModels/StateFlows.
2. **Jetpack Compose**: Use Material 3 theming tokens (`MaterialTheme.colorScheme`), avoid hardcoded colors.
3. **Monochrome + Purple Palette**: Ensure all UI elements respect the monochrome canvas and purposeful purple accenting.
4. **Android Home Standards**: Ensure no regressions in `HOME` intent handling, package change broadcast receivers, or launcher lifecycle.

## Code Quality & Testing

- Run `./gradlew testDebugUnitTest` before opening a pull request.
- Ensure all CI workflow checks pass in GitHub Actions.
