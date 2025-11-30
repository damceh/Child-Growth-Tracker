package com.family.childtracker.domain.model

data class AppSettings(
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
