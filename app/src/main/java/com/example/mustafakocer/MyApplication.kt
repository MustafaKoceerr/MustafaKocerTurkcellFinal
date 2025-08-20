package com.example.mustafakocer

import android.app.Application
import com.example.mustafakocer.data.preferences.SessionManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * The main Application class, serving as the entry point for Hilt's dependency injection.
 * It is also responsible for initializing app-wide components like the [SessionManager].
 */
@HiltAndroidApp
class MyApplication : Application() {

    @Inject
    lateinit var sessionManager: SessionManager

    /**
     * An application-level CoroutineScope that lives as long as the application process.
     * This follows structured concurrency principles and should be used for app-wide background tasks.
     * A SupervisorJob ensures that the failure of one child coroutine does not cancel the entire scope.
     */
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        initializeSession()
    }

    private fun initializeSession() {
        // Use the dedicated application scope to launch the initialization task on a background thread.
        applicationScope.launch(Dispatchers.IO) {
            sessionManager.initialize()
        }
    }
}