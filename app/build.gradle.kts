// SPDX-License-Identifier: MIT
// Copyright (c) 2026 Hoàng Thị Kiều Anh, Phạm Văn Dượng, Đặng Quốc Trung

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.faceaccess.v2"

    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.faceaccess.v2"

        minSdk = 26
        targetSdk = 37

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }

    compileOptions {
        sourceCompatibility =
            JavaVersion.VERSION_11

        targetCompatibility =
            JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)

    // CameraX
    implementation("androidx.camera:camera-core:1.6.1")
    implementation("androidx.camera:camera-camera2:1.6.1")
    implementation("androidx.camera:camera-lifecycle:1.6.1")
    implementation("androidx.camera:camera-view:1.6.1")

    // MediaPipe
    implementation("com.google.mediapipe:tasks-vision:1.0.0")

    // Crop ảnh đại diện
    implementation("com.github.yalantis:ucrop:2.2.11")

    // Lottie
    implementation("com.airbnb.android:lottie:6.7.1")

    // ViewPager2

    // Test
    testImplementation(libs.junit)

    androidTestImplementation(
        libs.androidx.espresso.core
    )

    androidTestImplementation(
        libs.androidx.junit
    )
}
