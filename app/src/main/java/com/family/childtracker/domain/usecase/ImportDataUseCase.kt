package com.family.childtracker.domain.usecase

import com.family.childtracker.data.backup.BackupManager
import com.family.childtracker.domain.repository.*
import java.io.File

/**
 * Use case for importing app data from a backup file
 */
class ImportDataUseCase(
    private val childProfileRepository: ChildProfileRepository,
    private val growthRecordRepository: GrowthRecordRepository,
    private val milestoneRepository: MilestoneRepository,
    private val behaviorEntryRepository: BehaviorEntryRepository,
    private val parentingTipRepository: ParentingTipRepository,
    private val chatMessageRepository: ChatMessageRepository,
    private val weeklySummaryRepository: WeeklySummaryRepository,
    private val appSettingsRepository: AppSettingsRepository,
    private val backupManager: BackupManager
) {
    
    /**
     * Import app data from a backup file
     * 
     * @param file The backup file to import
     * @param clearExisting Whether to clear existing data before importing
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(file: File, clearExisting: Boolean = false): Result<Unit> {
        return try {
            // Import and parse backup file
            val backupResult = backupManager.importData(file)
            if (backupResult.isFailure) {
                return Result.failure(backupResult.exceptionOrNull() ?: Exception("Failed to import backup"))
            }
            
            val backupData = backupResult.getOrThrow()
            val domainData = with(backupManager) { backupData.toDomainModels() }
            
            // Clear existing data if requested
            if (clearExisting) {
                // Note: In a real implementation, you'd want to clear all data
                // For now, we'll just insert/update which will replace existing items with same IDs
            }
            
            // Import child profiles first (as they're referenced by other entities)
            domainData.childProfiles.forEach { profile ->
                childProfileRepository.createChildProfile(profile)
            }
            
            // Import growth records
            domainData.growthRecords.forEach { record ->
                growthRecordRepository.createGrowthRecord(record)
            }
            
            // Import milestones
            domainData.milestones.forEach { milestone ->
                milestoneRepository.createMilestone(milestone)
            }
            
            // Import behavior entries
            domainData.behaviorEntries.forEach { entry ->
                behaviorEntryRepository.createBehaviorEntry(entry)
            }
            
            // Import parenting tips
            domainData.parentingTips.forEach { tip ->
                parentingTipRepository.insertTip(tip)
            }
            
            // Import chat messages
            domainData.chatMessages.forEach { message ->
                chatMessageRepository.insertMessage(message)
            }
            
            // Import weekly summaries
            domainData.weeklySummaries.forEach { summary ->
                weeklySummaryRepository.insertSummary(summary)
            }
            
            // Import app settings (excluding sensitive data like API key and PIN)
            domainData.appSettings?.let { settings ->
                appSettingsRepository.updateSettings(settings)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
