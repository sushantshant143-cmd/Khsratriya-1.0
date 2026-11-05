# KSHATRIYA – Milestone Six: FITQUEST Workout Engine (development build)

This milestone adds the first structured FITQUEST workout engine to the existing Kotlin / Jetpack Compose Android source.

## Added in this milestone
- A 50-entry exercise library, organised into Cardio & Endurance, Strength, Core, Flexibility & Mobility, and Recovery & Conditioning.
- Exercise guidance fields: target/repetition guidance, movement cue, breathing cue, common mistake, and easier alternative.
- A library browser with category filtering.
- A personalised starter session assembled from the saved FITQUEST pathway and training level.
- A workout timer with Start, Pause, Resume, Skip, Next, and Finish behaviour.
- Ustaad text-to-speech prompts when a session or next exercise starts.
- Local session completion count and latest-session summary saved in encrypted preferences.
- Weight Management sessions omit the dedicated flexibility category in this initial pathway selection; Generalised Fitness includes all five categories.

## Important limitations – do not treat as production complete
- The session generator is an initial deterministic routine builder. It is not yet the final adaptive programme engine, and the selected session duration is currently used as a planning input rather than a precisely timed work/rest schedule.
- Timer completion pauses at zero and waits for the user to select Next; it does not auto-advance.
- Repetition targets are instructional labels; the app does not yet use camera-based rep counting or sensor validation.
- Exercise demonstrations are text-only in this milestone. No 3D animation assets are included.
- Session history currently stores the latest session summary and total completed count, not a full dated analytics ledger.
- Fitness scoring, rank promotion, diet planning, hydration, calendar scheduling, notifications, PDF reports, CPT/BPET/PPT standards, and verified official service-test charts are not implemented in this milestone.
- No Android SDK/Gradle installation is available in the authoring environment. This source has not been compiled or device-tested. A successful APK build and physical-device test are still required.

## Build prerequisites
- Android Studio with JDK 17
- Android SDK Platform 35
- Gradle plugin and Kotlin versions pinned in the project

Open the project root in Android Studio, allow Gradle sync, build, then test each control on a physical Android device. Test both pathways, all training levels, all library filters, timer pause/resume, skip, next, finish, and persistence after app restart.
