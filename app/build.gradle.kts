plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.0"
    id("androidx.navigation.safeargs.kotlin") // Si usas Safe Args
}

android {
    namespace = "com.escolavision.testescolavision"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.escolavision.testescolavision"
        minSdk = 30
        targetSdk = 34
        versionCode = 26
        versionName = "26.0"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0") // Si usas material v1
    implementation("androidx.compose.material3:material3:1.0.0") // Asegúrate de agregar esto para material3
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Dependencia principal de Moshi
    implementation("com.squareup.moshi:moshi:1.14.0")

    // Adaptador de Moshi para Kotlin
    implementation("com.squareup.moshi:moshi-kotlin:1.14.0")

    // Si usas Retrofit y Moshi
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation(libs.squareup.converter.moshi)

    // Jetpack Compose
    implementation(libs.androidx.activity.compose) // Para setContent
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material) // Si aún quieres usar material v1
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.navigation.compose)

    debugImplementation(libs.androidx.ui.tooling)

    implementation(libs.converter.gson)

    implementation(libs.accompanist.flowlayout)

    implementation(libs.coil.compose)

    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    implementation(libs.accompanist.insets)

    implementation (libs.accompanist.swiperefresh)

    implementation(libs.compose.charts)

    implementation(libs.logging.interceptor)

    implementation ("org.mindrot:jbcrypt:0.4")

    implementation(libs.androidx.foundation)

    implementation ("androidx.compose.ui:ui:1.3.0")

    implementation ("com.google.maps.android:maps-compose:2.11.4")
    implementation ("com.google.android.gms:play-services-maps:18.2.0")
}