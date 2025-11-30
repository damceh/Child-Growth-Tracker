package com.family.childtracker.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Secure storage for sensitive data using EncryptedSharedPreferences
 */
class SecurePreferences(context: Context) {
    
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    /**
     * Save OpenRouter API key securely
     */
    fun saveApiKey(apiKey: String) {
        sharedPreferences.edit()
            .putString(KEY_API_KEY, apiKey)
            .apply()
    }
    
    /**
     * Retrieve OpenRouter API key
     */
    fun getApiKey(): String? {
        return sharedPreferences.getString(KEY_API_KEY, null)
    }
    
    /**
     * Remove API key from storage
     */
    fun clearApiKey() {
        sharedPreferences.edit()
            .remove(KEY_API_KEY)
            .apply()
    }
    
    /**
     * Check if API key exists
     */
    fun hasApiKey(): Boolean {
        return !getApiKey().isNullOrBlank()
    }
    
    /**
     * Get masked API key for display (shows last 4 characters)
     */
    fun getMaskedApiKey(): String? {
        val apiKey = getApiKey()
        return if (apiKey != null && apiKey.length > 4) {
            "sk-••••${apiKey.takeLast(4)}"
        } else {
            null
        }
    }
    
    companion object {
        private const val PREFS_NAME = "child_tracker_secure_prefs"
        private const val KEY_API_KEY = "openrouter_api_key"
    }
}
