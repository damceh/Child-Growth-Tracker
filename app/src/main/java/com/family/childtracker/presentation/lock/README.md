# Biometric Authentication Implementation

## Overview

This module implements biometric authentication (fingerprint/face recognition) with PIN fallback for the Child Growth Tracker app. It provides app-level security to protect sensitive family data.

## Components

### 1. BiometricAuthManager
**Location**: `data/local/security/BiometricAuthManager.kt`

Handles biometric authentication using Android's BiometricPrompt API.

**Key Features**:
- Check if biometric hardware is available
- Get detailed biometric status (available, no hardware, none enrolled, etc.)
- Show biometric authentication prompt
- Handle authentication callbacks (success, error, failed)

**Usage**:
```kotlin
val biometricManager = BiometricAuthManager(context)

if (biometricManager.isBiometricAvailable()) {
    biometricManager.authenticate(
        activity = activity,
        title = "Unlock App",
        subtitle = "Use biometric authentication",
        negativeButtonText = "Use PIN",
        onSuccess = { /* Handle success */ },
        onError = { errorCode, errorMessage -> /* Handle error */ },
        onFailed = { /* Handle failed attempt */ }
    )
}
```

### 2. LockStateManager
**Location**: `data/local/security/LockStateManager.kt`

Manages the app's lock/unlock state and auto-lock functionality.

**Key Features**:
- Track lock/unlock state using StateFlow
- Monitor last active time
- Auto-lock after configurable timeout
- Singleton pattern for app-wide access

**Usage**:
```kotlin
val lockStateManager = LockStateManager.getInstance(context)

// Lock the app
lockStateManager.lock()

// Unlock the app
lockStateManager.unlock()

// Check if should auto-lock
if (lockStateManager.shouldAutoLock()) {
    lockStateManager.lock()
}

// Observe lock state
lockStateManager.isLocked.collect { isLocked ->
    // Update UI based on lock state
}
```

### 3. AppLockScreen
**Location**: `presentation/lock/AppLockScreen.kt`

Composable screen that displays when the app is locked.

**Key Features**:
- Automatic biometric prompt on first display (if enabled)
- PIN input with password masking
- Error message display
- Fallback to PIN if biometric fails
- Button to retry biometric authentication

**Parameters**:
- `biometricEnabled`: Whether biometric auth is enabled
- `pinCode`: The configured PIN code
- `onUnlock`: Callback when authentication succeeds

### 4. Settings Integration

The Settings screen includes a new "Security" section with:
- **Enable App Lock**: Toggle to enable/disable app lock
- **PIN Code**: Input field for 4-6 digit PIN (shown when app lock enabled)
- **Enable Biometric Authentication**: Toggle for biometric auth (only enabled if hardware available)
- **Auto-lock timeout**: Dropdown to select timeout (1, 2, 5, 10, 15, 30 minutes)

### 5. MainActivity Integration

MainActivity now:
- Checks lock state on startup
- Shows AppLockScreen when locked
- Checks for auto-lock on resume
- Updates last active time on pause

## Data Model Changes

### AppSettings Entity
Added fields:
- `biometricAuthEnabled: Boolean` - Whether biometric auth is enabled
- `appLockEnabled: Boolean` - Whether app lock is enabled
- `pinCode: String?` - Encrypted PIN code
- `autoLockTimeoutMinutes: Int` - Auto-lock timeout in minutes (default: 5)

### Database Migration
- Migration 3→4 adds the new security fields to `app_settings` table

## Security Considerations

1. **PIN Storage**: PIN is stored in the database (should be encrypted using EncryptedTypeConverters)
2. **Biometric Fallback**: PIN is always required as fallback for biometric auth
3. **Auto-lock**: App automatically locks after inactivity to protect data
4. **Lock on Startup**: If app lock is enabled, app starts in locked state
5. **Background Lock**: App locks when moved to background and timeout expires

## User Flow

### First Time Setup
1. User opens Settings
2. Enables "App Lock"
3. Enters a PIN (minimum 4 digits)
4. Optionally enables biometric authentication (if available)
5. Selects auto-lock timeout
6. Saves settings

### Unlocking the App
1. App shows lock screen
2. If biometric enabled and available:
   - Biometric prompt appears automatically
   - User can authenticate with fingerprint/face
   - Or tap "Use PIN" to enter PIN manually
3. If biometric not enabled or unavailable:
   - PIN input field is shown
   - User enters PIN and taps "Unlock"
4. On successful authentication, app unlocks

### Auto-lock Behavior
1. User uses the app normally
2. User switches to another app or locks device
3. After configured timeout (e.g., 5 minutes):
   - App automatically locks
4. When user returns to app:
   - Lock screen is shown
   - User must authenticate again

## Testing Checklist

- [ ] App lock can be enabled/disabled
- [ ] PIN validation (minimum 4 digits)
- [ ] Biometric authentication works on supported devices
- [ ] PIN fallback works when biometric fails
- [ ] Auto-lock triggers after timeout
- [ ] App locks on startup when enabled
- [ ] Settings persist across app restarts
- [ ] Lock state survives app backgrounding
- [ ] Error messages display correctly
- [ ] Biometric status detection works (available, no hardware, none enrolled)

## Requirements Validation

This implementation satisfies **Requirement 7.3**:
- ✅ BiometricPrompt implemented for fingerprint/face recognition
- ✅ App lock screen created with PIN fallback
- ✅ Auto-lock after inactivity timer implemented
- ✅ Authentication preferences stored in settings
- ✅ Lock/unlock state management implemented

## Future Enhancements

1. **Biometric-only mode**: Option to use biometric without PIN (less secure)
2. **Failed attempt lockout**: Lock app after X failed PIN attempts
3. **Emergency unlock**: Secret gesture or code to unlock if PIN forgotten
4. **Per-section locks**: Lock only sensitive sections (e.g., chat, summaries)
5. **Biometric re-authentication**: Require re-auth for sensitive operations
