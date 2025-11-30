package com.family.childtracker.presentation.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.family.childtracker.data.backup.BackupManager
import com.family.childtracker.data.local.database.DatabaseProvider
import com.family.childtracker.data.local.encryption.EncryptionManager
import com.family.childtracker.data.local.preferences.SecurePreferences
import com.family.childtracker.data.remote.api.OpenRouterClient
import com.family.childtracker.data.repository.*
import com.family.childtracker.domain.usecase.*

class SettingsViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            val database = DatabaseProvider.getDatabase(context)
            val securePreferences = SecurePreferences(context)
            
            val appSettingsRepository = AppSettingsRepositoryImpl(
                appSettingsDao = database.appSettingsDao(),
                securePreferences = securePreferences
            )
            
            // Create OpenRouter API client
            val apiService = OpenRouterClient.create(
                apiKeyProvider = { securePreferences.getApiKey() }
            )
            
            val openRouterRepository = OpenRouterRepositoryImpl(apiService)
            
            val validateApiKeyUseCase = ValidateApiKeyUseCase(openRouterRepository)
            val getAvailableModelsUseCase = GetAvailableModelsUseCase(
                openRouterRepository = openRouterRepository,
                appSettingsRepository = appSettingsRepository
            )
            
            // Create backup-related dependencies
            val encryptionManager = EncryptionManager()
            val backupManager = BackupManager(context, encryptionManager)
            
            val childProfileRepository = ChildProfileRepositoryImpl(
                childProfileDao = database.childProfileDao(),
                encryptionManager = encryptionManager
            )
            val growthRecordRepository = GrowthRecordRepositoryImpl(
                growthRecordDao = database.growthRecordDao(),
                encryptionManager = encryptionManager
            )
            val milestoneRepository = MilestoneRepositoryImpl(
                milestoneDao = database.milestoneDao(),
                encryptionManager = encryptionManager
            )
            val behaviorEntryRepository = BehaviorEntryRepositoryImpl(
                behaviorEntryDao = database.behaviorEntryDao(),
                encryptionManager = encryptionManager
            )
            val parentingTipRepository = ParentingTipRepositoryImpl(
                parentingTipDao = database.parentingTipDao()
            )
            val chatMessageRepository = ChatMessageRepositoryImpl(
                chatMessageDao = database.chatMessageDao()
            )
            val weeklySummaryRepository = WeeklySummaryRepositoryImpl(
                weeklySummaryDao = database.weeklySummaryDao()
            )
            
            val exportDataUseCase = ExportDataUseCase(
                childProfileRepository = childProfileRepository,
                growthRecordRepository = growthRecordRepository,
                milestoneRepository = milestoneRepository,
                behaviorEntryRepository = behaviorEntryRepository,
                parentingTipRepository = parentingTipRepository,
                chatMessageRepository = chatMessageRepository,
                weeklySummaryRepository = weeklySummaryRepository,
                appSettingsRepository = appSettingsRepository,
                backupManager = backupManager
            )
            
            val importDataUseCase = ImportDataUseCase(
                childProfileRepository = childProfileRepository,
                growthRecordRepository = growthRecordRepository,
                milestoneRepository = milestoneRepository,
                behaviorEntryRepository = behaviorEntryRepository,
                parentingTipRepository = parentingTipRepository,
                chatMessageRepository = chatMessageRepository,
                weeklySummaryRepository = weeklySummaryRepository,
                appSettingsRepository = appSettingsRepository,
                backupManager = backupManager
            )
            
            val getBackupFilesUseCase = GetBackupFilesUseCase(backupManager)
            
            return SettingsViewModel(
                appSettingsRepository = appSettingsRepository,
                securePreferences = securePreferences,
                validateApiKeyUseCase = validateApiKeyUseCase,
                getAvailableModelsUseCase = getAvailableModelsUseCase,
                exportDataUseCase = exportDataUseCase,
                importDataUseCase = importDataUseCase,
                getBackupFilesUseCase = getBackupFilesUseCase,
                context = context
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
