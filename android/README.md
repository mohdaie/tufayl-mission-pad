# Tufayl's Mission Pad — Android v2.1

Package: `com.mohdaie.tufaylmissionpad`

This Android project packages Mission Pad as an installable Android application while keeping the existing web edition untouched.

## Architecture

- Native Android Activity and lifecycle.
- Mission Pad learning UI bundled inside the APK/AAB under `app/src/main/assets/www/`.
- No dependency on GitHub Pages at runtime.
- Android Text-to-Speech bridge for spelling and trace prompts.
- Native Android back handling for modals and tabs.
- Browser local storage remains persistent inside the app's private WebView data directory.
- No Android runtime permissions are requested.
- No Internet permission is declared.
- No advertising or analytics SDKs.

## Build requirements

- JDK 17
- Gradle 8.13
- Android Gradle Plugin 8.13.2
- Android SDK 36

From the `android` directory:

```bash
gradle :app:assembleDebug
gradle :app:bundleRelease
```

The GitHub Actions workflow in the repository can also build both artifacts without requiring a local Android setup.

## Google Play

Current application metadata:

- Version name: **2.1.0**
- Version code: **20100**
- Minimum Android: **Android 8.0 / API 26**
- Target Android: **Android 16 / API 36**

The release AAB must be signed with your upload key before Google Play accepts it. Android Studio's **Build → Generate Signed App Bundle / APK** flow can create the signed bundle.
