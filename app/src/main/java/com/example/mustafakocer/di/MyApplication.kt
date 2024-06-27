package com.example.mustafakocer.di

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    // Hilt kullanımı için bu sınıf HiltAndroidApp ile anotasyonlanmıştır.
}