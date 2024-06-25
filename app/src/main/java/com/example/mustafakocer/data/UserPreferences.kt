package com.example.mustafakocer.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "my_data_store")

class UserPreferences @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val applicationContext = context.applicationContext

    // even when we will pass a context from activity or fragment we will get the application context here
    private val KEY_AUTH = stringPreferencesKey("key_auth")
    //private val ID = stringPreferencesKey("user_id")

    suspend fun savePreference(preferenceKey: PreferenceKeys, value: String) {
        applicationContext.dataStore.edit { preferences ->
            preferences[preferenceKey.key] = value
        }
    }

    // Kullanımı:
    suspend fun saveAuthToken(authToken: String) {
        savePreference(PreferenceKeys.KEY_AUTH, authToken)
    }

   /*
    suspend fun saveUserId(userId: String) {
        savePreference(PreferenceKeys.USER_ID, userId)
    }
    */


    fun getPreferenceFlow(preferenceKey: PreferenceKeys): Flow<String?> {
        return applicationContext.dataStore.data.map { preferences ->
            preferences[preferenceKey.key]
        }
    }
    /*
    // Kullanımı:
    val authToken: Flow<String?> = getPreferenceFlow(PreferenceKeys.KEY_AUTH)
    val userId: Flow<String?> = getPreferenceFlow(PreferenceKeys.USER_ID)
     */

    // it will give us the saved auth token back
    // in case no value is saved it will return null


    suspend fun clearDataStore() {
        applicationContext.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

}


enum class PreferenceKeys(val key: Preferences.Key<String>) {
    KEY_AUTH(stringPreferencesKey("key_auth")),
    //USER_ID(stringPreferencesKey("user_id"))
}