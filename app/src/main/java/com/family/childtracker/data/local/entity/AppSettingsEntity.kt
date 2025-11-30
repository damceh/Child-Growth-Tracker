package com.family.childtracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey
    val id: String,
    val openRouterApiKey: String?,
    val selectedModel: String?,
    val autoGenerateWeeklySummary: Boolean = true,
    val cachedModelsJson: String? = null,
    val modelsCacheTimestamp: Long? = null,
    val biometricAuthEnabled: Boolean = false,
    val appLockEnabled: Boolean = false,
    val pinCode: String? = null,
    val autoLockTimeoutMinutes: Int = 5
)
