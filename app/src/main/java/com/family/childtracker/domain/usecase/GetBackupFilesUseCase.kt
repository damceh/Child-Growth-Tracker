package com.family.childtracker.domain.usecase

import com.family.childtracker.data.backup.BackupManager
import java.io.File

/**
 * Use case for getting list of available backup files
 */
class GetBackupFilesUseCase(
    private val backupManager: BackupManager
) {
    
    /**
     * Get list of all backup files sorted by date (newest first)
     */
    operator fun invoke(): List<File> {
        return backupManager.getBackupFiles()
    }
}
