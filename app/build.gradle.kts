plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    id("androidx.navigation.safeargs.kotlin")
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    id("org.jetbrains.kotlin.plugin.parcelize")
}

android {
    namespace = "com.example.mustafakocer"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mustafakocer"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true
        compose = true
        buildConfig = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // https://mvnrepository.com/artifact/androidx.swiperefreshlayout/swiperefreshlayout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    // https://mvnrepository.com/artifact/com.google.android.material/material
    implementation("com.google.android.material:material:1.12.0")
    // Kotlinx Serialization JSON parser
    implementation(libs.kotlinx.serialization.json)

    // Retrofit için Kotlinx Serialization Converter
    implementation(libs.retrofit.kotlinx.serialization.converter)
    implementation(libs.retrofit)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    implementation(libs.room.paging)

    implementation(libs.paging.runtime)
    implementation(libs.paging.compose)

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.security.crypto.ktx)


    // BOM'lar
    implementation(platform(libs.androidx.compose.bom))
    implementation(platform(libs.firebase.bom))

    // Temel
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)
    implementation("androidx.fragment:fragment-ktx:1.8.0")
    implementation(libs.material)
    implementation(libs.androidx.activity)

    // UI - Compose
    implementation(libs.androidx.material3)
    implementation("androidx.compose.material:material-icons-extended")
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)

    // Lifecycle
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.viewmodel.ktx)

    // Coroutines
    implementation(libs.coroutines.android)

    // Navigation
    implementation(libs.navigation.compose)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.hilt.navigation.fragment)

    // Firebase
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.database)
    implementation(libs.firebase.config)

    // Coil & Glide
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)

    // OKHTTP
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
}


