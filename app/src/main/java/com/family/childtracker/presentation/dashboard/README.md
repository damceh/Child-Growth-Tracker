# Dashboard Module

This module implements the main dashboard screen with navigation for the Child Growth Tracker app.

## Components

### DashboardScreen
The main dashboard UI that displays:
- Child profile selector dropdown
- Daily parenting tip card
- Latest growth record summary
- Recent milestone summary
- Behavior trends summary

### DashboardViewModel
Manages the dashboard state including:
- Loading and managing child profiles
- Fetching latest growth records
- Fetching latest milestones
- Generating behavior summaries
- Loading daily parenting tips

### MainNavigation
The main navigation component that provides:
- Bottom navigation bar with Dashboard, Growth, Milestones, and More tabs
- Floating Action Button (FAB) for quick data entry
- Navigation graph connecting all app screens
- Quick entry menu for adding growth records, milestones, and behavior entries

## Features

### Child Profile Selector
- Displays the currently selected child's name and age
- Allows switching between multiple child profiles
- Shows "Add your first child profile" prompt when no profiles exist

### Summary Cards
Each summary card is clickable and navigates to the respective detail screen:
- **Latest Growth**: Shows height and weight from the most recent growth record
- **Recent Milestone**: Displays the most recently achieved milestone
- **Behavior Trends**: Summarizes mood and sleep patterns from the past week

### Daily Parenting Tip
- Displays a random parenting tip on the dashboard
- Shows tip title and preview of content
- "Read More" button navigates to the tips library

### Bottom Navigation
Four main sections:
1. **Dashboard**: Home screen with summaries
2. **Growth**: Quick access to growth tracking
3. **Milestones**: Quick access to milestone timeline
4. **More**: Access to profile management and other features

### Floating Action Button (FAB)
- Visible on main screens (Dashboard, Growth, Milestones, Behavior)
- Opens quick entry menu for fast data entry
- Allows adding growth records, milestones, or behavior entries

## Navigation Flow

```
Dashboard (Start)
├── Profile Selector → Profile List
├── Daily Tip → Tips Library
├── Latest Growth → Growth Tracking
├── Recent Milestone → Milestone Timeline
├── Behavior Trends → Behavior List
└── FAB → Quick Entry Menu
    ├── Growth Record → Growth Entry Form
    ├── Milestone → Milestone Entry Form
    └── Behavior Entry → Behavior Entry Form
```

## Requirements Satisfied

- **Requirement 6.1**: Dashboard with clear navigation to all main sections within two taps
- **Requirement 6.2**: Consistent visual design language throughout all screens
- **Requirement 8.1**: Summary view on Dashboard showing most recent data

## Implementation Notes

- Uses Jetpack Compose for UI
- Follows MVVM architecture pattern
- Integrates with existing navigation graphs for profiles, growth, milestones, behavior, and tips
- Auto-selects first profile when app launches
- Generates behavior summaries from the past 7 days of entries
