package com.family.childtracker.domain.usecase

import com.family.childtracker.data.remote.model.OpenRouterModel
import com.family.childtracker.data.remote.util.ApiResult
import com.family.childtracker.domain.model.AppSettings
import com.family.childtracker.domain.repository.AppSettingsRepository
import com.family.childtracker.domain.repository.OpenRouterRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.concurrent.TimeUnit

/**
 * Use case for fetching available AI models with daily caching
 */
class GetAvailableModelsUseCase(
    private val openRouterRepository: OpenRouterRepository,
    private val appSettingsRepository: AppSettingsRepository
) {
    private val gson = Gson()
    private val cacheValidityDuration = TimeUnit.DAYS.toMillis(1) // 24 hours
    
    suspend operator fun invoke(forceRefresh: Boolean = false): Result {
        // Check if we have cached models and they're still valid
        if (!forceRefresh) {
            val cachedModels = getCachedModels()
            if (cachedModels != null) {
                return Result.Success(cachedModels)
            }
        }
        
        // Fetch fresh models from API
        return when (val apiResult = openRouterRepository.getAvailableModels()) {
            is ApiResult.Success -> {
                val models = apiResult.data
                cacheModels(models)
                Result.Success(models)
            }
            is ApiResult.Error -> {
                // If API fails, try to return cached models even if expired
                val cachedModels = getCachedModels(ignoreExpiry = true)
                if (cachedModels != null) {
                    Result.Success(cachedModels, isStale = true)
                } else {
                    Result.Error(apiResult.exception.message)
                }
            }
        }
    }
    
    private suspend fun getCachedModels(ignoreExpiry: Boolean = false): List<OpenRouterModel>? {
        val settings = appSettingsRepository.getSettingsOnce() ?: return null
        val cachedJson = settings.cachedModelsJson ?: return null
        val cacheTimestamp = settings.modelsCacheTimestamp ?: return null
        
        // Check if cache is still valid
        if (!ignoreExpiry) {
            val currentTime = System.currentTimeMillis()
            val cacheAge = currentTime - cacheTimestamp
            if (cacheAge > cacheValidityDuration) {
                return null // Cache expired
            }
        }
        
        // Deserialize cached models
        return try {
            val type = object : TypeToken<List<OpenRouterModel>>() {}.type
            gson.fromJson(cachedJson, type)
        } catch (e: Exception) {
            null
        }
    }
    
    private suspend fun cacheModels(models: List<OpenRouterModel>) {
        try {
            val modelsJson = gson.toJson(models)
            val currentTime = System.currentTimeMillis()
            
            val settings = appSettingsRepository.getSettingsOnce() ?: AppSettings(
                id = "default",
                openRouterApiKey = null,
                selectedModel = null,
                autoGenerateWeeklySummary = true,
                cachedModelsJson = modelsJson,
                modelsCacheTimestamp = currentTime
            )
            
            val updatedSettings = settings.copy(
                cachedModelsJson = modelsJson,
                modelsCacheTimestamp = currentTime
            )
            
            appSettingsRepository.saveSettings(updatedSettings)
        } catch (e: Exception) {
            // Silently fail - caching is not critical
        }
    }
    
    sealed class Result {
        data class Success(
            val models: List<OpenRouterModel>,
            val isStale: Boolean = false
        ) : Result()
        
        data class Error(val message: String) : Result()
    }
}
