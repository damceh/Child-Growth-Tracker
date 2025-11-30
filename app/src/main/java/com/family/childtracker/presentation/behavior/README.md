# Behavior Tracking Module

This module implements daily behavior tracking functionality for the Child Growth Tracker app.

## Features

- **Quick-entry behavior form** with emoji selectors for mood
- **Rating scales** for sleep quality (1-5 stars)
- **Eating habits** selection with radio buttons
- **Free-form notes** field for additional observations
- **Calendar view** showing behavior entries by date
- **List view** with date filtering and entry management

## Components

### Domain Layer
- `BehaviorEntry` - Domain model with mood and habit enums
- `CreateBehaviorEntryUseCase` - Creates new behavior entries
- `GetBehaviorEntriesUseCase` - Retrieves behavior entries
- `UpdateBehaviorEntryUseCase` - Updates existing entries
- `DeleteBehaviorEntryUseCase` - Deletes entries

### Presentation Layer
- `BehaviorViewModel` - Manages UI state and business logic
- `BehaviorEntryScreen` - Form for creating/editing entries
- `BehaviorListScreen` - List view of all entries
- `BehaviorCalendarScreen` - Calendar view of entries
- `BehaviorNavigation` - Navigation graph for behavior screens

## Navigation Routes

- `behavior_list/{childId}` - List of behavior entries
- `behavior_entry/{childId}` - Create new entry
- `behavior_edit/{childId}/{entryId}` - Edit existing entry
- `behavior_calendar/{childId}` - Calendar view

## Requirements Covered

- 4.1: Daily entry interface with mood, sleep, eating habits
- 4.2: Entries associated with date and child profile
- 4.3: Calendar format view
- 4.4: Free-form notes field
- 4.5: Quick-entry options with emoji selectors
