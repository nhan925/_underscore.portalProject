plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.login_portal"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.login_portal"
        minSdk = 26
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    implementation(fileTree(mapOf(
        "dir" to "./libs",
        "include" to listOf("*.aar", "*.jar"),
        "exclude" to listOf("")
    )))
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    ksp("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.ui.desktop)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    
    implementation(libs.material.v190) // Update the version as needed
    implementation(libs.androidx.fragment.ktx) // For Fragment transactions
    implementation(libs.androidx.security.crypto)

    implementation(libs.okhttp.v460)
    implementation(libs.commons.codec)

    implementation(libs.mpandroidchart)

    implementation(libs.fuel)
    implementation(libs.fuel.gson)

    implementation(libs.lottie)

    implementation(libs.gson)

    implementation(libs.msal.v510)
    implementation(libs.volley)



    implementation(libs.okhttp)

    implementation(libs.opentelemetry.api)
    implementation(libs.opentelemetry.context)

    // Core library cho Coroutines
    implementation(libs.kotlinx.coroutines.core)

    // Android-specific library cho Coroutines (Dispatchers.Main)
    implementation(libs.kotlinx.coroutines.android)
    // Thư viện Lifecycle (bao gồm lifecycleScope)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    //Thư viện Action Button
    implementation(libs.speed.dial.v330)
    implementation(libs.glide)
    implementation(libs.androidx.gridlayout)

    implementation (libs.androidx.work.runtime.ktx)
    implementation ("com.facebook.shimmer:shimmer:0.5.0")

    // Thư viện cho GeminiService
    implementation(libs.generativeai)
    implementation(libs.markwon.core)

    // Thư viện decode jwt Token
    implementation(libs.jwtdecode)
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1") // Check for the latest version
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("org.apache.poi:poi-ooxml:5.2.3")
    implementation("org.apache.commons:commons-math3:3.6.1")
}