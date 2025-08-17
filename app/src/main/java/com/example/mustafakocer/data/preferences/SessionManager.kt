package com.example.mustafakocer.data.preferences

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Manages the user session data (token and user ID) in memory for fast access,
 * while persisting it to secure storage. This class acts as the single source
 * of truth for the current session state throughout the app's lifecycle.
 */
@Singleton
class SessionManager @Inject constructor(
    private val sessionStorage: SessionStorage, // Encrypted DataStore for token
    private val userPreferences: UserPreferences, // Standard DataStore for user ID
) {
    // In-memory cache for the auth token.
    private val _authToken = MutableStateFlow<String?>(null)
    val authToken = _authToken.asStateFlow()

    // In-memory cache for the user ID.
    private val _userId = MutableStateFlow<Int?>(null)
    val userId = _userId.asStateFlow()

    /**
     * Initializes the session by loading data from persistent storage into memory.
     * This should be called once when the application starts.
     */
    suspend fun initialize() {
        _authToken.value = sessionStorage.authTokenFlow.first()
        _userId.value = userPreferences.userId.first()
    }

    /**
     * Updates the session with new data, persisting it and updating the in-memory cache.
     */
    suspend fun updateSession(token: String, id: Int) {
        sessionStorage.saveAuthToken(token)
        userPreferences.saveUserId(id)
        _authToken.value = token
        _userId.value = id
    }

    /**
     * Clears the session data from both persistent storage and memory.
     */
    suspend fun clearSession() {
        sessionStorage.clear()
        userPreferences.clear()
        _authToken.value = null
        _userId.value = null
    }
}