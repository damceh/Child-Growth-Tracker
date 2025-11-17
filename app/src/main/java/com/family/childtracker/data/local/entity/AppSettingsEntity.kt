package com.family.childtracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey
    val id: String,
    val openRouterApiKey: String?,
    val selectedModel: String?,
    val autoGenerateWeeklySummary: Boolean = true
)
