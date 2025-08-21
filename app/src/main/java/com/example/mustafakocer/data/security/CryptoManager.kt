package com.example.mustafakocer.data.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A singleton class responsible for all symmetric encryption and decryption operations.
 * It uses the Android KeyStore system to securely generate, store, and retrieve the
 * cryptographic key, ensuring it never leaves the device's secure hardware.
 */
@Singleton
class CryptoManager @Inject constructor() {

    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    private val encryptCipher
        get() = Cipher.getInstance(TRANSFORMATION)

    private fun getDecryptCipherForIv(iv: ByteArray): Cipher {
        return Cipher.getInstance(TRANSFORMATION).apply {
            val spec = GCMParameterSpec(128, iv)
            init(Cipher.DECRYPT_MODE, getKey(), spec)
        }
    }

    private fun getKey(): SecretKey {
        val existingKey = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createKey()
    }

    private fun createKey(): SecretKey {
        return KeyGenerator.getInstance(ALGORITHM).apply {
            init(
                KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(BLOCK_MODE)
                    .setEncryptionPaddings(PADDING)
                    .setKeySize(256)
                    .setUserAuthenticationRequired(false)
                    .setRandomizedEncryptionRequired(true)
                    .build()
            )
        }.generateKey()
    }

    /**
     * Encrypts the given byte array.
     * The 12-byte Initialization Vector (IV) is prepended to the resulting ciphertext,
     * which is essential for decryption.
     * @param data The raw data to be encrypted.
     * @return A byte array containing the IV followed by the encrypted data.
     */
    fun encrypt(data: ByteArray): ByteArray {
        val cipher = encryptCipher.apply {
            init(Cipher.ENCRYPT_MODE, getKey())
        }
        return cipher.iv + cipher.doFinal(data)
    }

    /**
     * Decrypts the given byte array.
     * It assumes the first 12 bytes of the array are the Initialization Vector (IV).
     * @param encryptedData A byte array containing the IV and the encrypted data.
     * @return The original, decrypted raw data.
     */
    fun decrypt(encryptedData: ByteArray): ByteArray {
        val iv = encryptedData.copyOfRange(0, 12)
        val data = encryptedData.copyOfRange(12, encryptedData.size)
        return getDecryptCipherForIv(iv).doFinal(data)
    }

    private companion object {
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_NONE
        private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
        private const val KEY_ALIAS = "auth_token_master_key"
    }
}