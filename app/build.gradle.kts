plugins {
    alias(libs.plugins.android.application)
    id("realm-android")
}

android {
    namespace = "salvador.labs"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "salvador.labs"
        minSdk = 24
        targetSdk = 29
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // CAMERA
    implementation(files("libs/Android-Image-Cropper-release.aar"))
    implementation("androidx.exifinterface:exifinterface:1.4.2")

    implementation(libs.picasso)
    implementation("com.karumi:dexter:6.2.3")
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    implementation(libs.recyclerview)
    implementation(files("libs/realmadapter4.0.0.jar"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}