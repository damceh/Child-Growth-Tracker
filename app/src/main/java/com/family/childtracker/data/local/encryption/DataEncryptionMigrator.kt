package com.family.childtracker.data.local.encryption

import com.family.childtracker.data.local.dao.BehaviorEntryDao
import com.family.childtracker.data.local.dao.ChildProfileDao
import com.family.childtracker.data.local.dao.GrowthRecordDao
import com.family.childtracker.data.local.dao.MilestoneDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Utility class to migrate existing unencrypted data to encrypted format.
 * This is optional and can be triggered manually if needed for immediate encryption.
 * Otherwise, data will be encrypted gradually as it's accessed and updated.
 */
class DataEncryptionMigrator(
    private val childProfileDao: ChildProfileDao,
    private val growthRecordDao: GrowthRecordDao,
    private val milestoneDao: MilestoneDao,
    private val behaviorEntryDao: BehaviorEntryDao
) {
    
    private val encryptionManager = EncryptionManager()
    
    /**
     * Migrate all existing unencrypted data to encrypted format.
     * This is a one-time operation that can be run to immediately encrypt all data.
     */
    suspend fun migrateAllData() = withContext(Dispatchers.IO) {
        migrateChildProfiles()
        migrateGrowthRecords()
        migrateMilestones()
        migrateBehaviorEntries()
    }
    
    /**
     * Migrate child profile names
     */
    private suspend fun migrateChildProfiles() {
        val profiles = childProfileDao.getAllProfilesList()
        profiles.forEach { profile ->
            // Check if name is already encrypted
            if (!encryptionManager.isEncrypted(profile.name)) {
                val encryptedName = encryptionManager.encrypt(profile.name)
                if (encryptedName != null) {
                    val updatedProfile = profile.copy(name = encryptedName)
                    childProfileDao.updateProfile(updatedProfile)
                }
            }
        }
    }
    
    /**
     * Migrate growth record notes
     */
    private suspend fun migrateGrowthRecords() {
        val records = growthRecordDao.getAllRecords()
        records.forEach { record ->
            val notes = record.notes
            if (notes != null && !encryptionManager.isEncrypted(notes)) {
                val encryptedNotes = encryptionManager.encrypt(notes)
                if (encryptedNotes != null) {
                    val updatedRecord = record.copy(notes = encryptedNotes)
                    growthRecordDao.updateRecord(updatedRecord)
                }
            }
        }
    }
    
    /**
     * Migrate milestone notes
     */
    private suspend fun migrateMilestones() {
        val milestones = milestoneDao.getAllMilestones()
        milestones.forEach { milestone ->
            val notes = milestone.notes
            if (notes != null && !encryptionManager.isEncrypted(notes)) {
                val encryptedNotes = encryptionManager.encrypt(notes)
                if (encryptedNotes != null) {
                    val updatedMilestone = milestone.copy(notes = encryptedNotes)
                    milestoneDao.updateMilestone(updatedMilestone)
                }
            }
        }
    }
    
    /**
     * Migrate behavior entry notes
     */
    private suspend fun migrateBehaviorEntries() {
        val entries = behaviorEntryDao.getAllEntries()
        entries.forEach { entry ->
            val notes = entry.notes
            if (notes != null && !encryptionManager.isEncrypted(notes)) {
                val encryptedNotes = encryptionManager.encrypt(notes)
                if (encryptedNotes != null) {
                    val updatedEntry = entry.copy(notes = encryptedNotes)
                    behaviorEntryDao.updateEntry(updatedEntry)
                }
            }
        }
    }
}
