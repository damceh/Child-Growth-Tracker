# Parenting Tips Module

This module provides parenting tips and advice to users through a library interface and daily tip display.

## Features

- **Daily Tip Display**: Shows a random parenting tip on the dashboard with refresh capability
- **Tips Library**: Browse all available tips with filtering and search
- **Category Filtering**: Filter tips by category (Nutrition, Sleep, Development, Behavior, Safety, Health)
- **Age Range Filtering**: Filter tips by child's age range (Newborn, Infant, Toddler, Preschool, School Age)
- **Search Functionality**: Search tips by title or content
- **Favorites**: Mark tips as favorites for quick access
- **Sample Content**: Pre-populated with 20+ helpful parenting tips

## Components

### ViewModel
- `ParentingTipsViewModel`: Manages UI state, filtering, search, and favorites

### UI Screens
- `TipsLibraryScreen`: Main screen showing all tips with filters
- `DailyTipCard`: Compact card component for dashboard display
- `TipCard`: Individual tip card in the library
- `TipDetailDialog`: Full tip content in a dialog

### Use Cases
- `GetAllParentingTipsUseCase`: Retrieve all tips
- `GetTipsByCategoryUseCase`: Filter tips by category
- `GetTipsByAgeRangeUseCase`: Filter tips by age range
- `GetRandomParentingTipUseCase`: Get a random tip for daily display
- `InitializeParentingTipsUseCase`: Populate database with sample tips

## Usage

### Initialize Sample Tips
Call this once when the app first launches or when tips are empty:
```kotlin
viewModel.initializeSampleTips()
```

### Display Daily Tip on Dashboard
```kotlin
DailyTipCard(
    tip = uiState.dailyTip,
    onRefresh = { viewModel.refreshDailyTip() },
    onReadMore = { /* Navigate to tips library */ }
)
```

### Navigate to Tips Library
```kotlin
navController.navigate(TIPS_LIBRARY_ROUTE)
```

## Requirements Satisfied

- **5.1**: Daily tip display on dashboard with rotation logic ✓
- **5.2**: Tips categorized by age range and developmental stage ✓
- **5.3**: Browse tips library organized by topic ✓
- **5.4**: Save favorite tips for later reference ✓
- **5.5**: Age-appropriate filtering based on child's date of birth ✓
