// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    id("com.google.devtools.ksp") version "1.9.0-1.0.13" apply false
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
    alias(libs.plugins.google.gms.google.services) apply false
}


buildscript {

    repositories {
        google()
        mavenCentral()

    }
    dependencies {
        val nav_version = "2.7.7"
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$nav_version")
    }
}

/*
dependencies {
    implementation(kotlin("script-runtime"))
}
 */