plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.urfulive"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.urfulive"
        minSdk = 25
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.runtime.android)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.material)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.androidx.datastore.core.android)
//    implementation(libs.androidx.datastore.preferences.core.jvm)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.compose.ui:ui:1.7.8")
    implementation("androidx.compose.material:material:1.7.8")
    implementation("androidx.compose.ui:ui-tooling-preview:1.7.8")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.activity:activity-compose:1.10.1")
    debugImplementation("androidx.compose.ui:ui-tooling:1.7.8")
    // Бом (BOM) для удобной синхронизации версий
    implementation(platform("androidx.compose:compose-bom:2023.01.00")) // актуальную версию

    // Основные библиотеки Compose
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material:material")
    // Если используешь Material 3, добавь
    implementation("androidx.compose.material3:material3")

    // Инструменты превью
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Иконки расширенные (включая Visibility/VisibilityOff)
    implementation("androidx.compose.material:material-icons-extended")

    // Жизненный цикл и ViewModel-Compose
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")

    // Корутины (часто нужны для Flow)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    // Возможно, уже есть, но на всякий случай
    implementation(libs.androidx.activity.compose.v170)
    implementation(platform("androidx.compose:compose-bom:2023.03.00")) // Или другую версию
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material:material")
    // Если применяешь Material 3, добавь:
    // implementation("androidx.compose.material3:material3")

    // Для превью и инструментария
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Иконки "Visibility", "VisibilityOff" находятся здесь
    implementation("androidx.compose.material:material-icons-extended")

    // ViewModel + Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")

    // Activity для Compose
    implementation("androidx.activity:activity-compose:1.7.0")

    // Для Flow и корутин
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    implementation(libs.androidx.foundation)

    implementation(libs.ktor.ktor.client.core)
    implementation(libs.ktor.ktor.client.android)
    implementation (libs.ktor.ktor.client.json)
    implementation (libs.ktor.client.serialization)
    implementation (libs.ktor.client.content.negotiation)
    implementation (libs.ktor.serialization.kotlinx.json)
    implementation (libs.kotlinx.serialization.json)

    implementation(libs.kotlinx.serialization.json.v160) // Для работы с Json файлами
    implementation("androidx.datastore:datastore-preferences:1.1.5")
}