package com.example.mustafakocer.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferences @Inject constructor(
    private val context: Context
) {
    private val dataStore = context.dataStore

    private companion object {
        val USER_ID = intPreferencesKey("user_id")
    }

    /**
     * Oturum açan kullanıcının ID'sini DataStore'a kaydeder.
     */
    suspend fun saveUserId(userId: Int) {
        dataStore.edit { preferences ->
            preferences[USER_ID] = userId
        }
    }

    /**
     * Kaydedilmiş kullanıcı ID'sini bir Flow olarak okur.
     */
    val userId: Flow<Int?> = dataStore.data
        .map { preferences ->
            preferences[USER_ID]
        }

    /**
     * Kaydedilmiş kullanıcı ID'sini temizler.
     */
    suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.remove(USER_ID)
        }
    }
}