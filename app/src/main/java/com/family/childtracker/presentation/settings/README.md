# Settings Screen - API Key Configuration

This module implements secure API key configuration for OpenRouter integration.

## Features

### 1. Secure API Key Storage
- Uses Android `EncryptedSharedPreferences` with AES256-GCM encryption
- API key is never stored in plain text in the database
- Automatic encryption/decryption handled by Android Keystore

### 2. API Key Masking
- Displays only last 4 characters in UI (e.g., `sk-••••abcd`)
- Password field with show/hide toggle
- Prevents shoulder surfing and accidental exposure

### 3. Connection Testing
- "Test Connection" button validates API key
- Fetches models list from OpenRouter to verify authentication
- Provides clear error messages for different failure scenarios:
  - Invalid API key (401)
  - Network errors
  - Server errors
  - Rate limiting

### 4. Settings Management
- Default AI model selection
- Auto-generate weekly summaries toggle
- Persistent storage across app restarts

## Architecture

### Components

**SecurePreferences**
- Wrapper around EncryptedSharedPreferences
- Handles API key encryption/decryption
- Provides masking functionality

**SettingsViewModel**
- Manages UI state
- Coordinates between repository and use cases
- Handles validation and saving

**SettingsScreen**
- Material 3 UI with proper accessibility
- Form validation
- Loading states and error handling

**ValidateApiKeyUseCase**
- Business logic for API key validation
- Converts API errors to user-friendly messages

## Usage

### Navigation

```kotlin
// In your navigation graph
settingsScreen(
    context = context,
    onNavigateBack = { navController.popBackStack() }
)

// Navigate to settings
navController.navigateToSettings()
```

### Accessing API Key

```kotlin
// In your repository or use case
val securePreferences = SecurePreferences(context)
val apiKey = securePreferences.getApiKey()

// Use with OpenRouter client
val apiService = OpenRouterClient.create(
    apiKeyProvider = { securePreferences.getApiKey() }
)
```

## Security Considerations

1. **Encryption**: API key is encrypted using Android Keystore (hardware-backed when available)
2. **No Logging**: API key is never logged or exposed in debug builds
3. **Memory Safety**: API key is not kept in memory longer than necessary
4. **UI Masking**: Default view shows masked key to prevent exposure

## Requirements Satisfied

- ✅ **11.1**: Settings screen with API key input field
- ✅ **11.2**: EncryptedSharedPreferences for secure storage
- ✅ **11.3**: API key masking (last 4 characters)
- ✅ **11.6**: Test Connection functionality

## Testing

### Manual Testing Steps

1. **Save API Key**
   - Enter valid OpenRouter API key
   - Click "Save Settings"
   - Verify success message

2. **Test Connection**
   - Click "Test Connection"
   - Verify success message for valid key
   - Verify error message for invalid key

3. **API Key Masking**
   - Save API key
   - Navigate away and back
   - Verify only last 4 characters shown

4. **Toggle Visibility**
   - Click eye icon to show/hide API key
   - Verify password masking works

5. **Auto-generate Toggle**
   - Toggle weekly summary setting
   - Save and verify persistence

### Error Scenarios to Test

- Invalid API key → "Invalid API key" message
- No internet → "No internet connection" message
- Empty API key → Test button disabled
- Server error → "Service unavailable" message

## Integration

To integrate settings into your app:

1. Add settings navigation to main nav graph
2. Add settings menu item to dashboard/toolbar
3. Check for API key before using AI features:

```kotlin
if (!securePreferences.hasApiKey()) {
    // Show prompt to configure API key
    navController.navigateToSettings()
}
```

## Files Created

- `SecurePreferences.kt` - Encrypted storage wrapper
- `SettingsViewModel.kt` - UI state management
- `SettingsScreen.kt` - UI implementation
- `SettingsViewModelFactory.kt` - ViewModel factory
- `SettingsNavigation.kt` - Navigation setup
- `ValidateApiKeyUseCase.kt` - Validation logic

## Next Steps

After implementing this task:
1. Add settings menu item to dashboard
2. Implement model selection dropdown (Task 12)
3. Use API key in chat interface (Task 13)
4. Use API key in weekly summaries (Task 14)
