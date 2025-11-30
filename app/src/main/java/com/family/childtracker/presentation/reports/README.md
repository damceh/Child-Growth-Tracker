# Monthly Summary Reports

This module implements comprehensive monthly summary reports with export and sharing capabilities.

## Features

### 1. Monthly Summary Generation
- Aggregates data for a selected month:
  - Growth metrics with percentiles
  - Milestones achieved
  - Behavior patterns
- Highlights significant changes in growth
- Calculates average sleep quality and dominant moods

### 2. PDF Export
- Generates professional PDF reports
- Includes:
  - Child name and date range
  - Growth metrics with changes and percentiles
  - List of milestones
  - Behavior summary
  - Significant change warnings

### 3. Image Export
- Creates shareable PNG images
- Optimized for social media sharing
- Includes emojis and visual formatting
- 1080x1920 resolution

### 4. Share Functionality
- Uses Android's native share sheet
- Supports sharing PDFs and images
- Integrates with all sharing apps

## Components

### Domain Layer

#### Models
- `MonthlySummary`: Main summary data model
- `GrowthSummary`: Growth metrics and changes
- `BehaviorSummary`: Behavior patterns and statistics

#### Use Cases
- `GenerateMonthlySummaryUseCase`: Aggregates monthly data
- `ExportSummaryAsPdfUseCase`: Creates PDF documents
- `ExportSummaryAsImageUseCase`: Creates PNG images
- `ShareSummaryUseCase`: Handles Android sharing

### Presentation Layer

#### ViewModel
- `MonthlySummaryViewModel`: Manages UI state and coordinates use cases
- States: Initial, Loading, Success, Exporting, ExportSuccess, ReadyToShare, Error

#### UI
- `MonthlySummaryScreen`: Main screen with summary display
- `MonthPickerDialog`: Month selection dialog
- `SummaryContent`: Displays summary data
- `GrowthMetricRow`: Shows individual growth metrics

## Usage

### Navigation
From the dashboard, users can navigate to "Monthly Reports" which shows:
1. Month selector (defaults to previous month)
2. Generated summary with all data
3. Export buttons (PDF and Image)
4. Share functionality after export

### Month Selection
- Users can select any past month
- Summary regenerates automatically
- Defaults to previous month

### Export Flow
1. User views summary
2. Clicks "Export PDF" or "Export Image"
3. File is generated and saved
4. Success message shows file location
5. "Share" button appears
6. User can share via any app

## File Storage

Exported files are saved to:
```
/Android/data/com.family.childtracker/files/
```

Files are named:
- PDF: `summary_ChildName_YYYY-MM-DD.pdf`
- Image: `summary_ChildName_YYYY-MM-DD.png`

## Requirements Validation

This implementation satisfies:
- **Requirement 8.2**: Calculate and display growth percentiles ✓
- **Requirement 8.3**: Highlight significant changes ✓
- **Requirement 8.4**: Provide monthly summary report ✓
- **Requirement 8.5**: Share reports as PDF or image ✓

## Technical Details

### PDF Generation
- Uses Android's `PdfDocument` API
- A4 page size (595x842 points)
- Custom text formatting with multiple paint styles
- Automatic line spacing and margins

### Image Generation
- Uses Android's `Bitmap` and `Canvas` APIs
- 1080x1920 resolution (9:16 aspect ratio)
- Custom color scheme matching app theme
- Anti-aliased text rendering

### File Sharing
- Uses `FileProvider` for secure file sharing
- Grants temporary read permissions
- Compatible with all sharing apps
- Supports both PDF and image MIME types

## Future Enhancements

Potential improvements:
- Multi-page PDF support for longer summaries
- Custom report templates
- Email integration
- Cloud storage backup
- Print functionality
- Chart inclusion in reports
