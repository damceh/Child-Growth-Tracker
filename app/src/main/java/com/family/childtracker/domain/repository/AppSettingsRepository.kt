package com.family.childtracker.domain.repository

import com.family.childtracker.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface AppSettingsRepository {
    fun getSettings(): Flow<AppSettings?>
    suspend fun getSettingsOnce(): AppSettings?
    suspend fun saveSettings(settings: AppSettings)
    suspend fun updateSettings(settings: AppSettings)
    suspend fun deleteSettings()
}
