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

// DataStore instance'ını Context'e extension olarak tanımlıyoruz.
private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(name = "encrypted_session_storage")

@Singleton
class SessionStorage @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cryptoManager: CryptoManager,
) {
    private val dataStore = context.sessionDataStore

    companion object {
        private val ENCRYPTED_AUTH_TOKEN = stringPreferencesKey("encrypted_auth_token")
    }

    /**
     * Verilen Token'i şifreler ve DataStore'a kaydeder.
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
     * DataStore'dan şifrelenmiş token'ı okur, şifresini çözer ve bir Flow olarak sunar.
     */
    val authTokenFlow: Flow<String?> = dataStore.data
        .map { preferences ->
            val encryptedTokenBase64 = preferences[ENCRYPTED_AUTH_TOKEN] ?: return@map null
            try {
                val encryptedBytes = Base64.decode(encryptedTokenBase64, Base64.DEFAULT)
                val decryptedBytes = cryptoManager.decrypt(encryptedBytes)
                decryptedBytes.decodeToString()
            } catch (e: Exception) {
                // Deşifreleme hatası (örn: anahtar değişti, veri bozuk)
                null
            }
        }

    /**
     * Kaydedilmiş tüm oturum bilgilerini temizler.
     */
    suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

}