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

@Singleton
class CryptoManager @Inject constructor() {

    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    private val encryptCipher
        get() = Cipher.getInstance(TRANSFORMATION)

    private fun getDecryptCipherForIv(iv: ByteArray): Cipher {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, getKey(), spec)
        return cipher
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
                    .setUserAuthenticationRequired(false) // İsteğe bağlı
                    .setRandomizedEncryptionRequired(true) // GCM için önerilir
                    .build()
            )
        }.generateKey()
    }

    fun encrypt(data: ByteArray): ByteArray {
        val cipher = encryptCipher
        cipher.init(Cipher.ENCRYPT_MODE, getKey())
        // IV'yi (Initialization Vector) şifrelenmiş verinin başına ekliyoruz.
        // Bu, her şifrelemede farklı bir IV kullanılmasını sağlar ve deşifreleme için gereklidir.
        return cipher.iv + cipher.doFinal(data)
    }

    fun decrypt(encryptedData: ByteArray): ByteArray {
        // Verinin başındaki IV'yi ayırıyoruz. AES/GCM için IV 12 byte'tır.
        val iv = encryptedData.copyOfRange(0, 12)
        val data = encryptedData.copyOfRange(12, encryptedData.size)
        val cipher = getDecryptCipherForIv(iv)
        return cipher.doFinal(data)
    }

    private companion object {
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_NONE
        private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
        private const val KEY_ALIAS = "auth_token_master_key"
    }
}