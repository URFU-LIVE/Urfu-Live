plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "live.urfu.frontend"
    compileSdk = 35

    defaultConfig {
        applicationId = "live.urfu.frontend"
        minSdk = 25
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
}
dependencies {
    // Core
    implementation(libs.androidx.core.ktx)
    //implementation(libs.androidx.core.ktx.v1150)

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    //implementation(libs.androidx.lifecycle.runtime.ktx.v287)
    //noinspection UseTomlInstead,GradleDependency
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Compose BOM (одна версия для всех)
    val composeBom = platform("androidx.compose:compose-bom:2023.03.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Compose
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    //noinspection GradleDependency
    //implementation(libs.androidx.ui.v178)
    implementation(libs.androidx.ui.tooling.preview.v178)
    implementation(libs.androidx.material3.android)

    implementation(libs.coil.compose)

    //noinspection GradleDependency
    debugImplementation(libs.androidx.ui.tooling.v178)

    // Navigation
    implementation(libs.androidx.navigation.runtime.android)
    implementation(libs.androidx.navigation.compose)

    // Foundation
    implementation(libs.androidx.foundation)

    // Firebase
    implementation(libs.firebase.crashlytics.buildtools)

    // DataStore
    implementation(libs.androidx.datastore.preferences.v116)
    implementation(libs.androidx.datastore.preferences.core.v100)
    //noinspection UseTomlInstead
    implementation(libs.androidx.datastore.core)

    // Ktor
    implementation(libs.ktor.ktor.client.core)
    implementation(libs.ktor.ktor.client.android)
    implementation(libs.ktor.ktor.client.json)
    implementation(libs.ktor.client.serialization)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    // Serialization
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.serialization.json.v160)

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // System UI
    implementation(libs.accompanist.systemuicontroller)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4) // Ensure this dependency is correct
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.0") // Add the missing dependency here
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
