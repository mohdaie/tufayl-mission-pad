# Google Play release checklist

## App identity
- App name: Tufayl's Mission Pad
- Package ID: com.mohdaie.tufaylmissionpad
- Version: 2.1.0
- Version code: 20100
- Target SDK: 36

## Current privacy posture
This Android build:
- requests no dangerous Android permissions;
- has no location access;
- has no microphone or camera access;
- has no login requirement;
- contains no ads;
- contains no analytics SDK;
- stores learner profiles, stars, settings and progress locally on the device;
- uses the device Text-to-Speech engine for English learning prompts.

Verify these statements again before each Play submission if features are added later.

## Before first upload
1. Create or select the app in Google Play Console.
2. Enrol in Play App Signing.
3. Create and securely retain an upload key.
4. Generate a signed Android App Bundle (AAB).
5. Complete Target audience and content, Data safety, App access, Content rating and Families-related declarations accurately.
6. Add a privacy policy page suitable for a children's educational app.
7. Prepare Play Store assets: 512×512 icon, feature graphic, phone screenshots and tablet screenshots.
8. Use Internal testing first, then Closed/Open testing if needed.
9. Verify TTS, tracing, spelling, Art Studio, profile persistence and back navigation on at least one phone and one tablet.
10. Never commit the upload keystore or its passwords to GitHub.

## Data Safety starting point
Based on the current source code, learner progress remains on-device and the Android application itself does not transmit it. Google Play declarations must still reflect the final shipping build and any future SDKs or services you add.
