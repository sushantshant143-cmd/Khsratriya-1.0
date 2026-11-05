plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "in.kshatriya.app"
    compileSdk = 35
    defaultConfig {
        applicationId = "in.kshatriya.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures { compose = true; buildConfig = true }
    val developerVerifier = (project.findProperty("developerPinVerifier") as String?) ?: ""
    buildTypes {
        getByName("debug") { buildConfigField("String", "DEVELOPER_PIN_VERIFIER", "\"$developerVerifier\"") }
        getByName("release") { buildConfigField("String", "DEVELOPER_PIN_VERIFIER", "\"$developerVerifier\"") }
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.activity:activity-compose:1.10.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.biometric:biometric:1.1.0")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation("androidx.fragment:fragment-ktx:1.8.5")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    debugImplementation("androidx.compose.ui:ui-tooling")
}
