# Backup and Restore Module

## Overview
This module provides data export and import functionality for the Child Growth Tracker app, allowing users to backup all their data to JSON files and restore from previous backups.

## Features

### Data Export
- Export all app data to a JSON file
- Optional AES-256 encryption for sensitive data
- Automatic timestamped filenames
- Stored in app-specific external storage
- No special permissions required

### Data Import
- Import data from JSON backup files
- Automatic detection and decryption of encrypted backups
- Version compatibility checking
- Preserves data integrity

### Supported Data
- Child profiles
- Growth records
- Milestones
- Behavior entries
- Parenting tips
- Chat messages
- Weekly summaries
- App settings (excluding API key and PIN for security)

## Architecture

```
BackupManager
    ├── exportData() - Creates backup file
    ├── importData() - Restores from backup
    ├── getBackupFiles() - Lists available backups
    └── deleteBackupFile() - Removes a backup

BackupMapper
    ├── Domain models → Backup models
    └── Backup models → Domain models

Use Cases
    ├── ExportDataUseCase - Orchestrates export
    ├── ImportDataUseCase - Orchestrates import
    └── GetBackupFilesUseCase - Lists backups
```

## Usage

### Export Data
```kotlin
val result = exportDataUseCase(encrypt = true)
if (result.isSuccess) {
    val file = result.getOrNull()
    // Backup created at: file.absolutePath
}
```

### Import Data
```kotlin
val result = importDataUseCase(backupFile, clearExisting = false)
if (result.isSuccess) {
    // Data restored successfully
}
```

### List Backups
```kotlin
val backupFiles = getBackupFilesUseCase()
// Returns List<File> sorted by date (newest first)
```

## File Format

Backup files are JSON with the following structure:

```json
{
  "version": 1,
  "exportDate": "2025-11-28T10:30:00Z",
  "encrypted": false,
  "childProfiles": [...],
  "growthRecords": [...],
  "milestones": [...],
  "behaviorEntries": [...],
  "parentingTips": [...],
  "chatMessages": [...],
  "weeklySummaries": [...],
  "appSettings": {...}
}
```

## Storage Location

Backups are stored in app-specific external storage:
```
<external_storage>/Android/data/com.family.childtracker/files/backups/
```

Files are named: `child_tracker_backup_<timestamp>.json`

## Security

### Encryption
- Uses Android Keystore with AES-256-GCM
- Transparent encryption/decryption
- Encrypted backups automatically detected on import

### Privacy
- API keys and PIN codes are **excluded** from backups
- Users must reconfigure these after restore
- Backups stored in app-private storage
- No cloud sync or external transmission

## UI Integration

The backup/restore functionality is integrated into the Settings screen:

1. **Export Buttons**
   - "Export Data" - Unencrypted backup
   - "Export (Encrypted)" - Encrypted backup

2. **Available Backups List**
   - Shows recent backups
   - One-tap import
   - File size display

3. **Status Messages**
   - Success/failure notifications
   - Clear user feedback

## Dependencies

- **Gson**: JSON serialization
- **Android Keystore**: Encryption
- **Room**: Database access
- **Coroutines**: Async operations

## Testing

### Manual Test Scenarios
1. Export with empty database
2. Export with full database
3. Export encrypted backup
4. Import unencrypted backup
5. Import encrypted backup
6. Import with existing data
7. Verify all data types restored
8. Test error handling (corrupted files)

### Edge Cases
- Very large datasets
- Corrupted backup files
- Incompatible versions
- Storage space issues
- Concurrent operations

## Known Limitations

1. Photo files not included (only URIs)
2. No external file picker (uses internal list only)
3. No backup preview before import
4. No progress indicator for large imports
5. API key and PIN must be reconfigured after restore

## Future Enhancements

- Share functionality via Android share sheet
- Selective import (choose data types)
- Automatic scheduled backups
- Cloud backup integration
- Backup verification with checksums
- Photo file inclusion
- Compression for large backups
- Incremental backups

## Requirements Satisfied

✅ **Requirement 7.4**: Data export feature with local storage  
✅ **Requirement 7.5**: Data import feature with backup restore

## See Also

- [IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md) - Detailed implementation notes
- [BackupData.kt](model/BackupData.kt) - Data models
- [BackupMapper.kt](mapper/BackupMapper.kt) - Model conversion
- [BackupManager.kt](BackupManager.kt) - Core logic
