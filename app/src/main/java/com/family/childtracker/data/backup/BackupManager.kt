package com.family.childtracker.data.backup

import android.content.Context
import com.family.childtracker.data.backup.mapper.BackupMapper
import com.family.childtracker.data.backup.mapper.BackupMapper.toBackup
import com.family.childtracker.data.backup.mapper.BackupMapper.toDomain
import com.family.childtracker.data.backup.model.BackupData
import com.family.childtracker.data.local.encryption.EncryptionManager
import com.family.childtracker.domain.model.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.time.Instant

/**
 * Manages backup and restore operations for app data
 */
class BackupManager(
    private val context: Context,
    private val encryptionManager: EncryptionManager
) {
    
    private val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .create()
    
    /**
     * Export all app data to a JSON backup file
     * 
     * @param childProfiles List of child profiles
     * @param growthRecords List of growth records
     * @param milestones List of milestones
     * @param behaviorEntries List of behavior entries
     * @param parentingTips List of parenting tips
     * @param chatMessages List of chat messages
     * @param weeklySummaries List of weekly summaries
     * @param appSettings App settings
     * @param encrypt Whether to encrypt the backup file
     * @return File object pointing to the created backup file
     */
    fun exportData(
        childProfiles: List<ChildProfile>,
        growthRecords: List<GrowthRecord>,
        milestones: List<Milestone>,
        behaviorEntries: List<BehaviorEntry>,
        parentingTips: List<ParentingTip>,
        chatMessages: List<ChatMessage>,
        weeklySummaries: List<WeeklySummary>,
        appSettings: AppSettings?,
        encrypt: Boolean = false
    ): Result<File> {
        return try {
            // Create backup data structure
            val backupData = BackupData(
                exportDate = Instant.now().toString(),
                encrypted = encrypt,
                childProfiles = childProfiles.map { it.toBackup() },
                growthRecords = growthRecords.map { it.toBackup() },
                milestones = milestones.map { it.toBackup() },
                behaviorEntries = behaviorEntries.map { it.toBackup() },
                parentingTips = parentingTips.map { it.toBackup() },
                chatMessages = chatMessages.map { it.toBackup() },
                weeklySummaries = weeklySummaries.map { it.toBackup() },
                appSettings = appSettings?.toBackup()
            )
            
            // Convert to JSON
            val jsonString = gson.toJson(backupData)
            
            // Encrypt if requested
            val finalContent = if (encrypt) {
                encryptionManager.encrypt(jsonString) 
                    ?: throw Exception("Failed to encrypt backup data")
            } else {
                jsonString
            }
            
            // Create backup file
            val backupDir = getBackupDirectory()
            val timestamp = System.currentTimeMillis()
            val fileName = "child_tracker_backup_$timestamp.json"
            val backupFile = File(backupDir, fileName)
            
            // Write to file
            FileOutputStream(backupFile).use { outputStream ->
                outputStream.write(finalContent.toByteArray(Charsets.UTF_8))
            }
            
            Result.success(backupFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Import app data from a JSON backup file
     * 
     * @param file The backup file to import
     * @return Result containing the imported BackupData
     */
    fun importData(file: File): Result<BackupData> {
        return try {
            // Read file content
            val fileContent = FileInputStream(file).use { inputStream ->
                inputStream.readBytes().toString(Charsets.UTF_8)
            }
            
            // Check if encrypted and decrypt if needed
            val jsonString = if (encryptionManager.isEncrypted(fileContent)) {
                encryptionManager.decrypt(fileContent)
                    ?: throw Exception("Failed to decrypt backup data")
            } else {
                fileContent
            }
            
            // Parse JSON
            val backupData = gson.fromJson(jsonString, BackupData::class.java)
            
            // Validate version
            if (backupData.version > BackupData.CURRENT_VERSION) {
                throw Exception("Backup file version (${backupData.version}) is newer than supported version (${BackupData.CURRENT_VERSION})")
            }
            
            Result.success(backupData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get the directory for storing backup files
     */
    private fun getBackupDirectory(): File {
        // Use app-specific external storage (doesn't require permissions)
        val backupDir = File(context.getExternalFilesDir(null), "backups")
        if (!backupDir.exists()) {
            backupDir.mkdirs()
        }
        return backupDir
    }
    
    /**
     * Get list of all backup files
     */
    fun getBackupFiles(): List<File> {
        val backupDir = getBackupDirectory()
        return backupDir.listFiles()?.filter { it.extension == "json" }?.sortedByDescending { it.lastModified() } ?: emptyList()
    }
    
    /**
     * Delete a backup file
     */
    fun deleteBackupFile(file: File): Boolean {
        return try {
            file.delete()
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Convert BackupData to domain models
     */
    fun BackupData.toDomainModels(): DomainBackupData {
        return DomainBackupData(
            childProfiles = childProfiles.map { it.toDomain() },
            growthRecords = growthRecords.map { it.toDomain() },
            milestones = milestones.map { it.toDomain() },
            behaviorEntries = behaviorEntries.map { it.toDomain() },
            parentingTips = parentingTips.map { it.toDomain() },
            chatMessages = chatMessages.map { it.toDomain() },
            weeklySummaries = weeklySummaries.map { it.toDomain() },
            appSettings = appSettings?.toDomain()
        )
    }
}

/**
 * Container for domain model data from backup
 */
data class DomainBackupData(
    val childProfiles: List<ChildProfile>,
    val growthRecords: List<GrowthRecord>,
    val milestones: List<Milestone>,
    val behaviorEntries: List<BehaviorEntry>,
    val parentingTips: List<ParentingTip>,
    val chatMessages: List<ChatMessage>,
    val weeklySummaries: List<WeeklySummary>,
    val appSettings: AppSettings?
)
