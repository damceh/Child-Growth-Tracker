package com.family.childtracker.domain.model

data class AppSettings(
    val id: String,
    val openRouterApiKey: String?,
    val selectedModel: String?,
    val autoGenerateWeeklySummary: Boolean = true
)
