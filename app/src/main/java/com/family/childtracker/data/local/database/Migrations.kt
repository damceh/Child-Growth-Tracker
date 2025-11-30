package com.family.childtracker.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Database migration strategies for future schema changes.
 * 
 * Example migration from version 1 to 2:
 * val MIGRATION_1_2 = object : Migration(1, 2) {
 *     override fun migrate(database: SupportSQLiteDatabase) {
 *         // Add new column to existing table
 *         database.execSQL("ALTER TABLE child_profiles ADD COLUMN photo_uri TEXT")
 *     }
 * }
 * 
 * To use migrations, update DatabaseProvider:
 * Room.databaseBuilder(...)
 *     .addMigrations(MIGRATION_1_2)
 *     .build()
 */

object Migrations {
    // Migration from version 1 to 2: Add model caching fields to app_settings
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE app_settings ADD COLUMN cachedModelsJson TEXT")
            database.execSQL("ALTER TABLE app_settings ADD COLUMN modelsCacheTimestamp INTEGER")
        }
    }
    
    // Migration from version 2 to 3: Encrypt sensitive fields
    // Note: This migration doesn't modify schema, but the TypeConverters will
    // automatically encrypt data on next read/write. Existing unencrypted data
    // will be handled gracefully by the EncryptedTypeConverters which checks
    // if data is already encrypted before attempting decryption.
    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // No schema changes needed - encryption is handled by TypeConverters
            // The EncryptedTypeConverters will:
            // 1. Check if data is encrypted when reading
            // 2. Return unencrypted data as-is if not encrypted (backward compatibility)
            // 3. Encrypt data when writing
            // This allows gradual migration as data is accessed and updated
        }
    }
    
    // Migration from version 3 to 4: Add biometric authentication fields
    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE app_settings ADD COLUMN biometricAuthEnabled INTEGER NOT NULL DEFAULT 0")
            database.execSQL("ALTER TABLE app_settings ADD COLUMN appLockEnabled INTEGER NOT NULL DEFAULT 0")
            database.execSQL("ALTER TABLE app_settings ADD COLUMN pinCode TEXT")
            database.execSQL("ALTER TABLE app_settings ADD COLUMN autoLockTimeoutMinutes INTEGER NOT NULL DEFAULT 5")
        }
    }
}
