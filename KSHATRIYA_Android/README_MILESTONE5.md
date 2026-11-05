# KSHATRIYA – Milestone Five: Security Enhancement

This milestone extends the existing Kotlin/Jetpack Compose Android source.

## Added
- EncryptedSharedPreferences using an Android Keystore-backed AES-256 master key for local profile, PIN verifier, workout history and app settings.
- One-time migration of values from the earlier prototype preferences into encrypted storage.
- Biometric or device-screen-lock unlock using AndroidX BiometricPrompt, with personal six-digit PIN retained as fallback.
- Optional biometric enable/disable control in Profile & Settings; the user must authenticate before changing the setting.
- Automatic lock after the app has remained in the background for two minutes. Short interruptions do not trigger the lock.
- Existing Developer Activation and separate personal PIN flow retained.

## Important implementation notes
- Biometric unlock requires the device to have a supported biometric or secure device credential configured.
- The two-minute timeout is checked when the app returns to the foreground. Android process termination may restart the app; the persisted background timestamp allows the timeout check to run on the next launch.
- The developer PIN verifier remains a build-time configuration (`developerPinVerifier` Gradle property). No actual PIN is embedded in source. A common verifier distributed in an APK is still extractable by a determined reverse engineer; production activation should use a signed, revocable activation token or managed offline provisioning process.
- AndroidX Security Crypto 1.1.0-alpha06 is used for encrypted preferences. Review dependency lifecycle and migrate to a maintained Keystore/DataStore approach before production if required by the target Android release.
- This source has not been compiled or device-tested in this environment because an Android SDK/Gradle build environment is not available here. Do not treat it as a verified APK.

## Build prerequisites
- Android Studio with JDK 17
- Android SDK Platform 35
- Gradle plugin and Kotlin versions pinned in the project

Open the project root in Android Studio, allow Gradle sync, then build and test on a physical device. Test PIN fallback, biometric/device credential, background timeout, and data retention after app restart.
