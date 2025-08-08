plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
}

android {
  namespace = "com.example.aifiletransfer"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.example.aifiletransfer"
    minSdk = 24
    targetSdk = 34
    versionCode = 1
    versionName = "1.0-offline"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }

  buildFeatures {
    viewBinding = true
  }
}

dependencies {
  implementation("androidx.core:core-ktx:1.13.1")
  implementation("androidx.appcompat:appcompat:1.7.0")
  implementation("com.google.android.material:material:1.12.0")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
  implementation("androidx.activity:activity-ktx:1.9.1")

  val camerax_version = "1.3.4"
  implementation("androidx.camera:camera-core:$camerax_version")
  implementation("androidx.camera:camera-camera2:$camerax_version")
  implementation("androidx.camera:camera-lifecycle:$camerax_version")
  implementation("androidx.camera:camera-view:1.3.4")
}