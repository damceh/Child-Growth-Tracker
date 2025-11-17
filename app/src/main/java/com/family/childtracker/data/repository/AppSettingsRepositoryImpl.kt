package com.family.childtracker.data.repository

import com.family.childtracker.data.local.dao.AppSettingsDao
import com.family.childtracker.data.local.mapper.toDomain
import com.family.childtracker.data.local.mapper.toEntity
import com.family.childtracker.domain.model.AppSettings
import com.family.childtracker.domain.repository.AppSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppSettingsRepositoryImpl(
    private val appSettingsDao: AppSettingsDao
) : AppSettingsRepository {

    override fun getSettings(): Flow<AppSettings?> {
        return appSettingsDao.getSettings().map { it?.toDomain() }
    }

    override suspend fun getSettingsOnce(): AppSettings? {
        return appSettingsDao.getSettingsOnce()?.toDomain()
    }

    override suspend fun saveSettings(settings: AppSettings) {
        appSettingsDao.insertSettings(settings.toEntity())
    }

    override suspend fun updateSettings(settings: AppSettings) {
        appSettingsDao.updateSettings(settings.toEntity())
    }

    override suspend fun deleteSettings() {
        appSettingsDao.deleteSettings()
    }
}
