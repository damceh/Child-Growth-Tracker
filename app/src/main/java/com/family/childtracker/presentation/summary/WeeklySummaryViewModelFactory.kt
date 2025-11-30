package com.family.childtracker.presentation.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.family.childtracker.data.local.database.DatabaseProvider
import com.family.childtracker.data.local.preferences.SecurePreferences
import com.family.childtracker.data.remote.api.OpenRouterClient
import com.family.childtracker.data.repository.*
import com.family.childtracker.domain.usecase.GenerateWeeklySummaryUseCase

class WeeklySummaryViewModelFactory(
    private val databaseProvider: DatabaseProvider,
    private val securePreferences: SecurePreferences
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeeklySummaryViewModel::class.java)) {
            val database = databaseProvider.getDatabase()
            
            // Create repositories
            val weeklySummaryRepository = WeeklySummaryRepositoryImpl(database.weeklySummaryDao())
            val appSettingsRepository = AppSettingsRepositoryImpl(
                database.appSettingsDao(),
                securePreferences
            )
            val childProfileRepository = ChildProfileRepositoryImpl(database.childProfileDao())
            val growthRecordRepository = GrowthRecordRepositoryImpl(database.growthRecordDao())
            val milestoneRepository = MilestoneRepositoryImpl(database.milestoneDao())
            val behaviorEntryRepository = BehaviorEntryRepositoryImpl(database.behaviorEntryDao())
            
            // Create OpenRouter client and repository
            val openRouterClient = OpenRouterClient(securePreferences)
            val openRouterRepository = OpenRouterRepositoryImpl(
                openRouterClient.apiService,
                securePreferences
            )
            
            // Create use case
            val generateWeeklySummaryUseCase = GenerateWeeklySummaryUseCase(
                weeklySummaryRepository,
                openRouterRepository,
                appSettingsRepository,
                childProfileRepository,
                growthRecordRepository,
                milestoneRepository,
                behaviorEntryRepository
            )
            
            return WeeklySummaryViewModel(
                weeklySummaryRepository,
                generateWeeklySummaryUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
