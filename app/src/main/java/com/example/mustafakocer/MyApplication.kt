package com.example.mustafakocer

import android.app.Application
import com.example.mustafakocer.data.preferences.SessionManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate() {
        super.onCreate()
        // Initialize the session manager on a background thread.
        CoroutineScope(Dispatchers.IO).launch {
            sessionManager.initialize()
        }
    }
}