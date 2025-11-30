# Data Encryption Implementation

## Overview

This package implements AES-256-GCM encryption for sensitive data in the Child Growth Tracker app using Android Keystore. The encryption is transparent to the application layer through Room TypeConverters.

## Components

### EncryptionManager
- Manages encryption/decryption using Android Keystore
- Uses AES-256-GCM for strong encryption
- Generates and stores keys securely in Android Keystore
- Handles IV (Initialization Vector) generation for each encryption operation
- Format: `{IV_Base64}:{Ciphertext_Base64}`

### EncryptedTypeConverters
- Room TypeConverter for automatic encryption/decryption
- Transparently encrypts data when writing to database
- Transparently decrypts data when reading from database
- Handles backward compatibility with unencrypted data

### DataEncryptionMigrator
- Utility for batch migration of existing unencrypted data
- Can be triggered manually if immediate encryption is needed
- Otherwise, data is encrypted gradually as it's accessed

## Encrypted Fields

The following sensitive fields are encrypted:

1. **ChildProfileEntity**
   - `name` - Child's name

2. **GrowthRecordEntity**
   - `notes` - Optional notes about growth measurements

3. **MilestoneEntity**
   - `notes` - Optional notes about milestones

4. **BehaviorEntryEntity**
   - `notes` - Optional notes about behavior

5. **API Key** (via SecurePreferences)
   - OpenRouter API key stored in EncryptedSharedPreferences

## Migration Strategy

### Automatic (Gradual) Migration
The TypeConverters automatically handle unencrypted data:
1. When reading: Check if data is encrypted
2. If not encrypted: Return as-is (backward compatibility)
3. When writing: Always encrypt

This means existing data will be encrypted the next time it's updated.

### Manual (Immediate) Migration
Use `DataEncryptionMigrator` to encrypt all data immediately:

```kotlin
val migrator = DataEncryptionMigrator(
    childProfileDao,
    growthRecordDao,
    milestoneDao,
    behaviorEntryDao
)

// In a coroutine
migrator.migrateAllData()
```

## Security Features

1. **Android Keystore**: Keys never leave secure hardware
2. **AES-256-GCM**: Industry-standard authenticated encryption
3. **Unique IVs**: Each encryption uses a random IV
4. **Authentication**: GCM mode provides integrity verification
5. **No Key Export**: Keys cannot be extracted from Keystore

## Database Schema

No schema changes required. Encrypted data is stored as TEXT in the same columns, with the format:
```
{IV_Base64}:{Ciphertext_Base64}
```

Example:
```
abc123def456:xyz789ghi012jkl345mno678
```

## Error Handling

- Encryption failures: Return null, log error
- Decryption failures: Return null, log error
- Missing key: Automatically generated on first use
- Corrupted data: Gracefully handled, returns null

## Testing

To test encryption:

1. Create test data
2. Verify data is encrypted in database (contains ":")
3. Read data back and verify it's decrypted correctly
4. Test backward compatibility with unencrypted data

## Performance Considerations

- Encryption adds minimal overhead (~1-2ms per operation)
- TypeConverters are called automatically by Room
- No impact on query performance
- Keys are cached in memory by Android Keystore

## Requirements Satisfied

- **Requirement 7.2**: Encrypt sensitive Child Profile data using Android Keystore system
- **Requirement 11.2**: API key encryption (via EncryptedSharedPreferences)

## Future Enhancements

- Optional full database encryption
- Biometric authentication before decryption
- Key rotation mechanism
- Backup encryption with user password
