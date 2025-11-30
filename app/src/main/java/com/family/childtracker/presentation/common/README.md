# Common UI Components and Utilities

This directory contains reusable UI components and utilities that ensure consistent design, proper validation, and optimal performance throughout the Child Growth Tracker app.

## Components

### Loading States
- **LoadingIndicator**: Full-screen loading indicator with message
- **ButtonLoadingIndicator**: Inline loading indicator for buttons
- **LoadingOverlay**: Overlay loading state for screens
- **LoadingButton**: Button with built-in loading state support

### Error Handling
- **ErrorState**: Full-screen error state with retry option
- **ErrorMessage**: Inline error message component
- **WarningMessage**: Warning message with icon
- **ErrorSnackbar**: Snackbar for error messages

### Empty States
- **EmptyState**: Empty state component for lists with optional action button

### Form Components
- **ValidatedTextField**: Text field with built-in validation support
- **NumberTextField**: Number input field with decimal validation
- **LoadingButton**: Button with loading state
- **LoadingOutlinedButton**: Outlined button with loading state
- **LoadingTextButton**: Text button with loading state

### Cards
- **AppCard**: Standard card with consistent styling
- **ElevatedAppCard**: Elevated card for emphasis
- **OutlinedAppCard**: Outlined card for subtle emphasis

### Dialogs
- **ConfirmationDialog**: Standard confirmation dialog
- **DestructiveDialog**: Dialog for destructive actions (delete)
- **InfoDialog**: Information dialog

## Utilities

### Validation
`ValidationUtils` provides comprehensive input validation:
- Required field validation
- Number range validation
- Height, weight, head circumference validation
- Email validation
- API key validation
- Text length validation
- Sleep quality validation

### Animations
`AnimationUtils` provides consistent animations:
- Screen transitions (slide in/out, fade)
- Component animations (scale, fade)
- Standard durations and easing functions
- Shake animation for errors
- Pulse animation for highlighting

### Touch Targets
`TouchTargetUtils` ensures proper touch target sizes:
- Minimum 48dp touch targets (Material Design guideline)
- Recommended 56dp touch targets
- Extension functions: `minTouchTarget()`, `recommendedTouchTarget()`

### Auto-Save
`AutoSaveManager` provides form draft auto-save functionality:
- Save form drafts to prevent data loss
- Load saved drafts on form open
- Clear drafts after successful submission
- Supports GrowthRecord, Milestone, and BehaviorEntry drafts

### UI State Management
`UiState` sealed class for managing different UI states:
- Idle, Loading, Success, Error states
- Extension functions for state handling
- Type-safe state management

### Screen Size
`ScreenSizeUtils` provides responsive design utilities:
- Screen size categories (Small, Medium, Large, Extra Large)
- Adaptive padding based on screen size
- Adaptive column count for grids
- Landscape orientation detection

## Usage Examples

### Using Validated Text Field
```kotlin
ValidatedTextField(
    value = height,
    onValueChange = { height = it },
    label = "Height (cm)",
    errorMessage = ValidationUtils.validateHeight(height),
    keyboardType = KeyboardType.Decimal
)
```

### Using Loading Button
```kotlin
LoadingButton(
    onClick = { saveData() },
    text = "Save",
    isLoading = isSaving,
    enabled = isFormValid
)
```

### Using UI State
```kotlin
when (uiState) {
    is UiState.Loading -> LoadingIndicator()
    is UiState.Success -> DataContent(uiState.data)
    is UiState.Error -> ErrorState(uiState.message, onRetry = { retry() })
    is UiState.Idle -> EmptyState("No data available")
}
```

### Using Auto-Save
```kotlin
val autoSaveManager = AutoSaveManager(context)

// Save draft
LaunchedEffect(height, weight) {
    autoSaveManager.saveDraft(
        formId = "growth_record_${childId}",
        data = GrowthRecordDraft(height, weight, headCircumference, notes),
        serializer = { DraftSerializer.serialize(it) }
    )
}

// Load draft
LaunchedEffect(Unit) {
    autoSaveManager.loadDraft(
        formId = "growth_record_${childId}",
        deserializer = { DraftSerializer.deserialize<GrowthRecordDraft>(it) }
    ).collect { draft ->
        draft?.let {
            height = it.height
            weight = it.weight
            // ... restore other fields
        }
    }
}
```

### Using Animations
```kotlin
AnimatedVisibility(
    visible = isVisible,
    enter = AnimationUtils.slideInFromBottom(),
    exit = AnimationUtils.slideOutToBottom()
) {
    Content()
}
```

### Using Touch Targets
```kotlin
IconButton(
    onClick = { /* action */ },
    modifier = Modifier.minTouchTarget()
) {
    Icon(Icons.Default.Delete, contentDescription = "Delete")
}
```

## Performance Optimizations

### Database Indexes
All entities have proper indexes on frequently queried columns:
- `childId` for foreign key lookups
- `recordDate`, `entryDate`, `achievementDate` for date-based queries
- `weekStartDate` for summary queries

### Lazy Loading
- Use `Flow` for reactive data updates
- Implement pagination for large lists
- Load images lazily with Coil

### Memory Management
- Use `remember` for expensive computations
- Implement `derivedStateOf` for computed values
- Clear drafts after successful submission

## Accessibility

All components follow accessibility best practices:
- Minimum 48dp touch targets
- Proper content descriptions for icons
- Support for TalkBack
- High contrast colors
- Readable font sizes (minimum 16sp for body text)

## Material Design 3

All components follow Material Design 3 guidelines:
- Consistent color scheme with primary, secondary, tertiary colors
- Proper elevation and shadows
- Rounded corners with consistent shapes
- Proper spacing (8dp, 16dp, 24dp, 32dp)
- Typography scale (headings, body, captions)
