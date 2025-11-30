package com.family.childtracker.data.local.encryption

import androidx.room.TypeConverter

/**
 * Room type converters for automatic encryption/decryption of sensitive fields
 */
class EncryptedTypeConverters {
    
    private val encryptionManager = EncryptionManager()
    
    /**
     * Convert encrypted string from database to plaintext
     */
    @TypeConverter
    fun fromEncryptedString(encryptedValue: String?): String? {
        if (encryptedValue == null) return null
        
        // Check if data is already encrypted
        return if (encryptionManager.isEncrypted(encryptedValue)) {
            encryptionManager.decrypt(encryptedValue)
        } else {
            // Data is not encrypted (legacy data), return as-is
            encryptedValue
        }
    }
    
    /**
     * Convert plaintext to encrypted string for database storage
     */
    @TypeConverter
    fun toEncryptedString(plainValue: String?): String? {
        if (plainValue == null) return null
        
        // Only encrypt if not already encrypted
        return if (encryptionManager.isEncrypted(plainValue)) {
            plainValue
        } else {
            encryptionManager.encrypt(plainValue) ?: plainValue
        }
    }
}
