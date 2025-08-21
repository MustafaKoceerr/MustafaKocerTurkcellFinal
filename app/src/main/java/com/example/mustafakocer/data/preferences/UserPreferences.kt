package com.example.mustafakocer.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Creates a singleton instance of DataStore for user-related preferences.
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

/**
 * Manages the persistence of non-sensitive, user-specific data using Jetpack DataStore.
 * This class is designed to be a singleton, managed by Hilt.
 *
 * @param context The application context, provided by Hilt.
 */
@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    private companion object {
        val USER_ID = intPreferencesKey("user_id")
    }

    /**
     * Persists the user's ID to DataStore.
     */
    suspend fun saveUserId(userId: Int) {
        dataStore.edit { preferences ->
            preferences[USER_ID] = userId
        }
    }

    /**
     * A flow that emits the stored user ID, or null if it's not set.
     */
    val userId: Flow<Int?> = dataStore.data
        .map { preferences ->
            preferences[USER_ID]
        }

    /**
     * Removes the stored user ID from DataStore.
     */
    suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.remove(USER_ID)
        }
    }
}