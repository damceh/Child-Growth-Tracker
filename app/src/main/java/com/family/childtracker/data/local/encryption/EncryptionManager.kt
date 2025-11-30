package com.family.childtracker.data.local.encryption

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Manages encryption and decryption of sensitive data using Android Keystore and AES-256-GCM
 */
class EncryptionManager {
    
    private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
        load(null)
    }
    
    init {
        // Generate key if it doesn't exist
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            generateKey()
        }
    }
    
    /**
     * Generate AES-256 key in Android Keystore
     */
    private fun generateKey() {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )
        
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .setRandomizedEncryptionRequired(true)
            .build()
        
        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()
    }
    
    /**
     * Get the secret key from keystore
     */
    private fun getSecretKey(): SecretKey {
        return keyStore.getKey(KEY_ALIAS, null) as SecretKey
    }
    
    /**
     * Encrypt a string value
     * @param plaintext The text to encrypt
     * @return Base64 encoded string containing IV and ciphertext separated by ":"
     */
    fun encrypt(plaintext: String?): String? {
        if (plaintext.isNullOrBlank()) return null
        
        try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
            
            val iv = cipher.iv
            val ciphertext = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
            
            // Combine IV and ciphertext, encode as Base64
            val ivBase64 = Base64.encodeToString(iv, Base64.NO_WRAP)
            val ciphertextBase64 = Base64.encodeToString(ciphertext, Base64.NO_WRAP)
            
            return "$ivBase64:$ciphertextBase64"
        } catch (e: Exception) {
            // Log error in production, for now return null
            e.printStackTrace()
            return null
        }
    }
    
    /**
     * Decrypt an encrypted string
     * @param encryptedData Base64 encoded string containing IV and ciphertext separated by ":"
     * @return Decrypted plaintext string
     */
    fun decrypt(encryptedData: String?): String? {
        if (encryptedData.isNullOrBlank()) return null
        
        try {
            val parts = encryptedData.split(":")
            if (parts.size != 2) return null
            
            val iv = Base64.decode(parts[0], Base64.NO_WRAP)
            val ciphertext = Base64.decode(parts[1], Base64.NO_WRAP)
            
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec)
            
            val plaintext = cipher.doFinal(ciphertext)
            return String(plaintext, Charsets.UTF_8)
        } catch (e: Exception) {
            // Log error in production, for now return null
            e.printStackTrace()
            return null
        }
    }
    
    /**
     * Check if data is encrypted (contains IV separator)
     */
    fun isEncrypted(data: String?): Boolean {
        if (data.isNullOrBlank()) return false
        return data.contains(":") && data.split(":").size == 2
    }
    
    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "ChildTrackerEncryptionKey"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val GCM_TAG_LENGTH = 128
    }
}
