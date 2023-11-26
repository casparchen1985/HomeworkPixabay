// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.2" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    id("org.jetbrains.kotlin.kapt") version "1.9.20" apply false
    id("androidx.navigation.safeargs") version "2.7.5" apply false
    id("com.google.dagger.hilt.android") version "2.48.1" apply false
    id("io.realm.kotlin") version "1.11.0" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false
    id("com.google.firebase.crashlytics") version "2.9.9" apply false
}