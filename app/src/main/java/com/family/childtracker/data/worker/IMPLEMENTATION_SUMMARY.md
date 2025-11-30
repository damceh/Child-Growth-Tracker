# Weekly Summary Worker Implementation

## Overview
This module implements automatic weekly summary generation using WorkManager. The system schedules a background worker to run every Sunday at 8 PM, generating AI-powered summaries for all child profiles.

## Components

### 1. WeeklySummaryWorker
**Location**: `data/worker/WeeklySummaryWorker.kt`

A CoroutineWorker that:
- Checks if auto-generation is enabled in settings
- Verifies API key is configured (skips silently if not)
- Generates summaries for all child profiles
- Shows notification when summaries are ready
- Handles errors gracefully

**Key Features**:
- Network constraint required (internet connection needed for API calls)
- Creates notification channel for Android O+
- Displays count of generated summaries
- Taps notification to open the app

### 2. WeeklySummaryScheduler
**Location**: `data/worker/WeeklySummaryScheduler.kt`

Utility object for managing worker scheduling:
- `scheduleWeeklySummary()`: Schedules periodic work every 7 days
- `cancelWeeklySummary()`: Cancels scheduled work
- `isScheduled()`: Checks if worker is currently scheduled
- `triggerManualGeneration()`: Manually triggers summary generation
- `calculateInitialDelay()`: Calculates delay until next Sunday 8 PM

**Scheduling Logic**:
- Runs every Sunday at 8:00 PM
- Uses `PeriodicWorkRequest` with 7-day interval
- Calculates initial delay to align with Sunday schedule
- Uses `KEEP` policy to avoid duplicate scheduling

### 3. Application Integration
**Location**: `ChildTrackerApplication.kt`

The Application class now:
- Initializes weekly summary scheduling on app startup
- Checks if auto-generation is enabled in settings
- Schedules worker if enabled

### 4. Settings Integration
**Location**: `presentation/settings/SettingsViewModel.kt`

The ViewModel now:
- Accepts Context parameter for WorkManager access
- Schedules worker when auto-generation is enabled
- Cancels worker when auto-generation is disabled
- Updates happen when settings are saved

## User Flow

### Enabling Auto-Generation
1. User opens Settings screen
2. User toggles "Auto-generate weekly summaries" switch
3. User taps "Save Settings"
4. ViewModel saves settings to database
5. ViewModel schedules WorkManager worker
6. Worker will run every Sunday at 8 PM

### Disabling Auto-Generation
1. User opens Settings screen
2. User toggles off "Auto-generate weekly summaries" switch
3. User taps "Save Settings"
4. ViewModel saves settings to database
5. ViewModel cancels WorkManager worker
6. No more automatic summaries generated

### Automatic Generation
1. Sunday at 8 PM arrives
2. WorkManager triggers WeeklySummaryWorker
3. Worker checks if auto-generation is enabled
4. Worker checks if API key is configured
5. Worker loads all child profiles
6. Worker generates summary for each child
7. Worker shows notification with count
8. User taps notification to view summaries

## Notification

**Channel**: "Weekly Summaries"
**Title**: "Weekly Summary Ready"
**Content**: 
- Single child: "Your child's weekly summary is ready to view"
- Multiple children: "Weekly summaries for X children are ready"

**Behavior**:
- Tapping opens the app (MainActivity)
- Auto-cancels when tapped
- Default priority

## Permissions

Added to AndroidManifest.xml:
```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

Required for Android 13+ (API 33+) to show notifications.

## Error Handling

### No API Key
- Worker skips generation silently
- No error notification shown
- User can configure API key in Settings

### No Child Profiles
- Worker completes successfully
- No summaries generated
- No notification shown

### API Errors
- Worker marks as failure
- Individual profile errors don't stop other profiles
- Error count tracked but not shown to user

### Network Unavailable
- WorkManager respects network constraint
- Worker won't run without internet
- Will retry when network available

## Testing

### Manual Testing
1. Enable auto-generation in Settings
2. Use `WeeklySummaryScheduler.triggerManualGeneration()` for immediate test
3. Check notification appears
4. Verify summaries are created in database

### Scheduled Testing
1. Enable auto-generation
2. Wait until Sunday 8 PM
3. Verify worker runs automatically
4. Check notification and summaries

## Dependencies

- WorkManager 2.9.0 (already in build.gradle)
- Requires internet connection (NetworkType.CONNECTED)
- Uses existing repositories and use cases
- No additional dependencies needed

## Future Enhancements

Potential improvements:
- Allow user to customize schedule time
- Add retry logic for failed generations
- Show detailed error notifications
- Add summary generation history
- Support multiple notification channels
- Add notification preferences (sound, vibration)
