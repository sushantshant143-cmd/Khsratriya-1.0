# KSHATRIYA Android — Milestone 4: Activation & Personal PIN

This milestone extends the native Kotlin/Jetpack Compose source from Milestone 3. It preserves the approved olive/charcoal/gold design and adds the agreed activation flow:

1. Common Developer Activation screen for all test installations.
2. Separate personal six-digit PIN setup after activation.
3. Personal PIN verification when the app is launched again.
4. Ustaad welcome screen after initial PIN setup.
5. Personal PIN is stored as a salted PBKDF2-HMAC-SHA256 verifier (120,000 iterations), not as plain text.

## Important: configure the common Developer PIN securely

The actual Developer PIN has deliberately NOT been invented or embedded in this source. Until a verifier is configured, the activation screen will say that activation is not configured.

To configure it, run `python3 tools/generate_developer_verifier.py` on the development computer. It prompts for the six-digit PIN without echoing it and prints a `developerPinVerifier=...` property. Put that property in the developer's user-level Gradle properties file (`~/.gradle/gradle.properties`), not in source control. The app build reads that Gradle property and injects only the salted verifier into BuildConfig.

A PIN verifier embedded in an offline APK can still be extracted or attacked by a determined person. This is a test-build activation gate, not enterprise-grade licensing or tamper-proof protection. For controlled distribution, use signed activation tokens or managed device deployment in a later release.

## Current security scope and limitations

- The personal PIN is salted and PBKDF2-hashed. The PIN itself is not stored.
- The current app data/profile/session storage still uses SharedPreferences and is NOT encrypted. Encrypted database/preferences remain a required next security task.
- Biometric unlock is not implemented in this milestone.
- Automatic inactivity timeout is not implemented yet; the personal PIN is requested when the app process is launched again.
- There is no password/PIN recovery flow yet. A forgotten personal PIN currently requires clearing app data, which also deletes local profile and workout history.
- This source project is not a compiled APK and has not been built or device-tested in this workspace because an Android SDK/Gradle build environment is unavailable here.

## Open the project

Open the extracted project folder in Android Studio with JDK 17 and Android SDK Platform 35. Allow Gradle sync. Configure the developer verifier as described above before running a test build.

## Existing features retained

Registration and local profile capture, BMI preview, service and FITQUEST selections, dashboard, sample workout timer and controls, Android text-to-speech prompts, and local session count/history remain in the project. The exercise list is still a small starter set; official CPT/BPET/PPT standards, full fitness scoring, encrypted storage, biometrics, inactivity locking, background workout service, final exercise library/3D animation and PDF reports remain future milestones.

No service test marks or standards are fabricated.
