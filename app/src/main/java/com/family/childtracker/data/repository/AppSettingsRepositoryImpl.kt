package com.family.childtracker.data.repository

import com.family.childtracker.data.local.dao.AppSettingsDao
import com.family.childtracker.data.local.mapper.toDomain
import com.family.childtracker.data.local.mapper.toEntity
import com.family.childtracker.data.local.preferences.SecurePreferences
import com.family.childtracker.domain.model.AppSettings
import com.family.childtracker.domain.repository.AppSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppSettingsRepositoryImpl(
    private val appSettingsDao: AppSettingsDao,
    private val securePreferences: SecurePreferences
) : AppSettingsRepository {

    override fun getSettings(): Flow<AppSettings?> {
        return appSettingsDao.getSettings().map { entity ->
            entity?.toDomain()?.copy(
                // Get API key from secure storage instead of database
                openRouterApiKey = securePreferences.getApiKey()
            )
        }
    }

    override suspend fun getSettingsOnce(): AppSettings? {
        return appSettingsDao.getSettingsOnce()?.toDomain()?.copy(
            // Get API key from secure storage instead of database
            openRouterApiKey = securePreferences.getApiKey()
        )
    }

    override suspend fun saveSettings(settings: AppSettings) {
        // Save API key to secure storage
        settings.openRouterApiKey?.let { securePreferences.saveApiKey(it) }
        
        // Save other settings to database (without API key)
        appSettingsDao.insertSettings(settings.copy(openRouterApiKey = null).toEntity())
    }

    override suspend fun updateSettings(settings: AppSettings) {
        // Update API key in secure storage
        settings.openRouterApiKey?.let { securePreferences.saveApiKey(it) }
        
        // Update other settings in database (without API key)
        appSettingsDao.updateSettings(settings.copy(openRouterApiKey = null).toEntity())
    }

    override suspend fun deleteSettings() {
        securePreferences.clearApiKey()
        appSettingsDao.deleteSettings()
    }
}
