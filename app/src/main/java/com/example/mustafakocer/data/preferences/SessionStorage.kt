package com.example.mustafakocer.data.preferences

import android.content.Context
import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.mustafakocer.data.security.CryptoManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Creates a singleton instance of DataStore, scoped to the application context.
 * The file name "encrypted_session_storage" indicates its purpose.
 */
private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(name = "encrypted_session_storage")

/**
 * Manages the secure persistence of the authentication token using Jetpack DataStore.
 * It collaborates with a [CryptoManager] to encrypt data before writing to disk and
 * decrypt it after reading.
 */
@Singleton
class SessionStorage @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cryptoManager: CryptoManager,
) {
    private val dataStore = context.sessionDataStore

    private companion object {
        val ENCRYPTED_AUTH_TOKEN = stringPreferencesKey("encrypted_auth_token")
    }

    /**
     * Encrypts the given token and saves it to DataStore.
     * The encrypted byte array is stored as a Base64 string.
     */
    suspend fun saveAuthToken(token: String) {
        val tokenBytes = token.encodeToByteArray()
        val encryptedBytes = cryptoManager.encrypt(tokenBytes)
        val encryptedTokenBase64 = Base64.encodeToString(encryptedBytes, Base64.DEFAULT)

        dataStore.edit { preferences ->
            preferences[ENCRYPTED_AUTH_TOKEN] = encryptedTokenBase64
        }
    }

    /**
     * A flow that reads the encrypted token from DataStore, decrypts it, and emits the result.
     * Emits null if the token is not found or if a decryption error occurs.
     */
    val authTokenFlow: Flow<String?> = dataStore.data
        .map { preferences ->
            val encryptedTokenBase64 = preferences[ENCRYPTED_AUTH_TOKEN] ?: return@map null
            try {
                val encryptedBytes = Base64.decode(encryptedTokenBase64, Base64.DEFAULT)
                val decryptedBytes = cryptoManager.decrypt(encryptedBytes)
                decryptedBytes.decodeToString()
            } catch (e: Exception) {
                // Handles decryption errors (e.g., key changed, data corrupted) gracefully.
                null
            }
        }

    /**
     * Clears all data from this specific DataStore instance.
     */
    suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}