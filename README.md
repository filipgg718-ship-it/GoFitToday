# GoFitToday — Android starter build

This repository is a clean Android Studio / GitHub Actions starter for GoFitToday.

## Build an APK on GitHub

1. Download and extract the ZIP.
2. Open your `filipgg718-ship-it/GoFitToday` repository on GitHub.
3. Choose **Add file → Upload files**.
4. Upload the **contents** of the extracted `gofittoday` folder to the repository root (so `app/`, `.github/`, `build.gradle.kts` and `settings.gradle.kts` appear at the top level). Do not upload the ZIP itself.
5. Commit to the `main` branch.
6. Open **Actions → Build GoFitToday Android APK** and wait for the run to finish.
7. Open the successful run and download the `GoFitToday-debug-apk` artifact. Extract it and install the APK on an Android 10+ device.

No GitHub chatbot, Copilot, API key, or repository secret is required for this starter build.

## What is included

- Kotlin + Jetpack Compose Android app, minimum Android 10 (API 29).
- Dark cyberpunk-inspired UI.
- Home dashboard, workout plan generator, workout session set counter, basic muscle activity screen, and settings.
- Planner inputs for weekly training days and equipment (full gym, dumbbells, bodyweight).
- GitHub Actions workflow that runs unit-test task and assembles a debug APK.

## Important scope notes

This is a **starter/MVP build**, not the full feature set previously discussed. It currently uses in-memory demo state; workout history is not persisted. The muscle screen is a clearly labelled demo display, not a true 28-day muscle score or symmetry assessment. Gemini AI, barcode/photo food recognition, nutrition database, step tracking, hydration, reminders, accounts, and cloud sync are not implemented in this starter. The planner is a local rule-based template generator, not an AI-generated plan. The app does not request or store an API key.

The GitHub workflow uses hosted Gradle installation, so a Gradle wrapper JAR is not needed for the GitHub build. The build must pass in Actions before an APK is considered available.
