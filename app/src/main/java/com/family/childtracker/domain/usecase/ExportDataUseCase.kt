package com.family.childtracker.domain.usecase

import com.family.childtracker.data.backup.BackupManager
import com.family.childtracker.domain.repository.*
import kotlinx.coroutines.flow.first
import java.io.File

/**
 * Use case for exporting all app data to a backup file
 */
class ExportDataUseCase(
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
     * Export all app data to a backup file
     * 
     * @param encrypt Whether to encrypt the backup file
     * @return Result containing the created backup file
     */
    suspend operator fun invoke(encrypt: Boolean = false): Result<File> {
        return try {
            // Collect all data from repositories
            val childProfiles = childProfileRepository.getAllChildProfiles().first()
            val growthRecords = mutableListOf<com.family.childtracker.domain.model.GrowthRecord>()
            val milestones = mutableListOf<com.family.childtracker.domain.model.Milestone>()
            val behaviorEntries = mutableListOf<com.family.childtracker.domain.model.BehaviorEntry>()
            val weeklySummaries = mutableListOf<com.family.childtracker.domain.model.WeeklySummary>()
            
            // Collect data for each child
            childProfiles.forEach { child ->
                growthRecords.addAll(growthRecordRepository.getGrowthRecords(child.id).first())
                milestones.addAll(milestoneRepository.getMilestones(child.id).first())
                behaviorEntries.addAll(behaviorEntryRepository.getBehaviorEntries(child.id).first())
                weeklySummaries.addAll(weeklySummaryRepository.getSummariesByChildId(child.id).first())
            }
            
            val parentingTips = parentingTipRepository.getAllTips().first()
            val chatMessages = chatMessageRepository.getAllMessages().first()
            val appSettings = appSettingsRepository.getSettings().first()
            
            // Export using backup manager
            backupManager.exportData(
                childProfiles = childProfiles,
                growthRecords = growthRecords,
                milestones = milestones,
                behaviorEntries = behaviorEntries,
                parentingTips = parentingTips,
                chatMessages = chatMessages,
                weeklySummaries = weeklySummaries,
                appSettings = appSettings,
                encrypt = encrypt
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
