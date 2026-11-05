# Build KSHATRIYA APK from an Android phone (GitHub Actions)

This project includes `.github/workflows/android-build.yml` so GitHub can compile the Android source on a cloud runner. No Android Studio is needed on the phone.

1. Create a private repository on GitHub in the mobile browser.
2. Upload the contents of this project folder to the repository root (the `.github/workflows` folder must also be uploaded).
3. Open the repository's **Actions** tab and select **Build KSHATRIYA Android APK**.
4. If the workflow has not run automatically after upload, select **Run workflow**.
5. Open the completed workflow run, scroll to **Artifacts**, and download `KSHATRIYA-debug-apk`.
6. Extract the ZIP and install `app-debug.apk` on the Android phone. Android may ask permission to install unknown apps for the browser/file manager used.

This produces a DEBUG test APK, not a production-signed release. The Developer Activation screen will remain unconfigured until the developer verifier is supplied through a secure build property. Do not put the actual PIN or its verifier in a public repository.

The workflow is a build route, not proof that the source compiles. The first cloud run will reveal any compile/dependency issues that must be fixed before the APK can be installed.
