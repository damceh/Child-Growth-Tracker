# Child Growth Tracker

A privacy-focused Android application for parents to monitor and record their child's physical growth, developmental milestones, daily behaviors, and receive AI-powered parenting guidance.

## 📱 Project Overview

Child Growth Tracker is a native Android app built with modern Android development practices, focusing on local-first data storage and user privacy. The app helps parents track multiple children's development over time with features like growth charts, milestone logging, behavior tracking, and optional AI-powered insights.

## 🎯 Current Implementation Status

### ✅ Completed Features (Tasks 1-4)

#### 1. Project Setup & Infrastructure
- Android project with Kotlin and Jetpack Compose
- Clean Architecture (Presentation, Domain, Data layers)
- Room database with migration support
- Material Design 3 theming
- Minimum SDK: API 26 (Android 8.0)

#### 2. Core Data Layer
- Complete Room database schema with 8 entities
- DAO interfaces for all data operations
- Repository pattern implementation
- Entity-to-Domain model mappers
- Database provider with singleton pattern

#### 3. Child Profile Management
- Create, read, update, delete child profiles
- Profile list screen with age calculation
- Profile form with name, date of birth, and gender
- Profile selection for feature navigation

#### 4. Growth Tracking Module
- Record height, weight, and head circumference
- Date-based growth entries with validation
- Historical records list with edit/delete
- WHO-based percentile calculations
- Growth entry form with date picker

### 🚧 Pending Features (Tasks 5-24)
- Growth visualization with charts
- Milestone tracking with photos
- Daily behavior tracking
- Parenting tips library
- Dashboard and navigation
- AI chat assistant (OpenRouter integration)
- Weekly AI-generated summaries
- Data encryption and biometric auth
- Export/import functionality
- Testing suite

## 🏗️ Architecture

The project follows **Clean Architecture** principles with clear separation of concerns:

```
┌─────────────────────────────────────────────────────────┐
│                   PRESENTATION LAYER                     │
│  (UI, ViewModels, Navigation, Compose Screens)          │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│                    DOMAIN LAYER                          │
│  (Use Cases, Domain Models, Repository Interfaces)      │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│                     DATA LAYER                           │
│  (Repository Impl, Room Database, DAOs, Entities)       │
└─────────────────────────────────────────────────────────┘
```

### Layer Responsibilities

**Presentation Layer** (`presentation/`)
- Jetpack Compose UI components
- ViewModels for state management
- Navigation logic
- User input handling

**Domain Layer** (`domain/`)
- Business logic in Use Cases
- Domain models (pure Kotlin classes)
- Repository interfaces (contracts)
- Business rules and validation

**Data Layer** (`data/`)
- Repository implementations
- Room database and entities
- DAOs for database operations
- Data mappers (Entity ↔ Domain)
- Future: API clients, local storage

## 📂 Project Structure

```
app/src/main/java/com/family/childtracker/
│
├── presentation/                    # UI Layer
│   ├── MainActivity.kt             # App entry point
│   ├── theme/                      # Material Design 3 theme
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   └── Type.kt
│   │
│   ├── profile/                    # Child Profile Feature
│   │   ├── ChildProfileViewModel.kt
│   │   ├── ChildProfileViewModelFactory.kt
│   │   ├── ProfileListScreen.kt
│   │   ├── ProfileFormScreen.kt
│   │   └── ProfileNavigation.kt
│   │
│   └── growth/                     # Growth Tracking Feature
│       ├── GrowthViewModel.kt
│       ├── GrowthViewModelFactory.kt
│       ├── GrowthListScreen.kt
│       ├── GrowthEntryScreen.kt
│       └── GrowthNavigation.kt
│
├── domain/                         # Business Logic Layer
│   ├── model/                      # Domain Models
│   │   ├── ChildProfile.kt
│   │   ├── GrowthRecord.kt
│   │   ├── Milestone.kt
│   │   ├── BehaviorEntry.kt
│   │   ├── ParentingTip.kt
│   │   ├── ChatMessage.kt
│   │   ├── WeeklySummary.kt
│   │   └── AppSettings.kt
│   │
│   ├── repository/                 # Repository Interfaces
│   │   ├── ChildProfileRepository.kt
│   │   ├── GrowthRecordRepository.kt
│   │   ├── MilestoneRepository.kt
│   │   ├── BehaviorEntryRepository.kt
│   │   ├── ParentingTipRepository.kt
│   │   ├── ChatMessageRepository.kt
│   │   ├── WeeklySummaryRepository.kt
│   │   └── AppSettingsRepository.kt
│   │
│   └── usecase/                    # Use Cases (Business Logic)
│       ├── CreateChildProfileUseCase.kt
│       ├── GetAllChildProfilesUseCase.kt
│       ├── UpdateChildProfileUseCase.kt
│       ├── DeleteChildProfileUseCase.kt
│       ├── GetChildProfileByIdUseCase.kt
│       ├── GetGrowthRecordsUseCase.kt
│       ├── CreateGrowthRecordUseCase.kt
│       ├── UpdateGrowthRecordUseCase.kt
│       ├── DeleteGrowthRecordUseCase.kt
│       └── CalculateGrowthPercentilesUseCase.kt
│
└── data/                           # Data Layer
    ├── local/
    │   ├── database/
    │   │   ├── ChildTrackerDatabase.kt    # Room Database
    │   │   ├── DatabaseProvider.kt        # Singleton provider
    │   │   └── Migrations.kt              # Database migrations
    │   │
    │   ├── entity/                        # Room Entities
    │   │   ├── ChildProfileEntity.kt
    │   │   ├── GrowthRecordEntity.kt
    │   │   ├── MilestoneEntity.kt
    │   │   ├── BehaviorEntryEntity.kt
    │   │   ├── ParentingTipEntity.kt
    │   │   ├── ChatMessageEntity.kt
    │   │   ├── WeeklySummaryEntity.kt
    │   │   └── AppSettingsEntity.kt
    │   │
    │   ├── dao/                           # Data Access Objects
    │   │   ├── ChildProfileDao.kt
    │   │   ├── GrowthRecordDao.kt
    │   │   ├── MilestoneDao.kt
    │   │   ├── BehaviorEntryDao.kt
    │   │   ├── ParentingTipDao.kt
    │   │   ├── ChatMessageDao.kt
    │   │   ├── WeeklySummaryDao.kt
    │   │   └── AppSettingsDao.kt
    │   │
    │   └── mapper/
    │       └── EntityMappers.kt           # Entity ↔ Domain mappers
    │
    └── repository/                        # Repository Implementations
        ├── ChildProfileRepositoryImpl.kt
        ├── GrowthRecordRepositoryImpl.kt
        ├── MilestoneRepositoryImpl.kt
        ├── BehaviorEntryRepositoryImpl.kt
        ├── ParentingTipRepositoryImpl.kt
        ├── ChatMessageRepositoryImpl.kt
        ├── WeeklySummaryRepositoryImpl.kt
        └── AppSettingsRepositoryImpl.kt
```

## 🔧 Technical Stack

### Core Technologies
- **Language**: Kotlin 1.9.20
- **UI Framework**: Jetpack Compose with Material Design 3
- **Architecture**: MVVM + Clean Architecture
- **Minimum SDK**: API 26 (Android 8.0 Oreo)
- **Target SDK**: API 34

### Key Dependencies

#### Android Jetpack
```kotlin
// Core
androidx.core:core-ktx:1.12.0
androidx.lifecycle:lifecycle-runtime-ktx:2.6.2
androidx.activity:activity-compose:1.8.1

// Compose
androidx.compose.bom:2023.10.01
androidx.compose.ui:ui
androidx.compose.material3:material3
androidx.compose.ui:ui-tooling-preview

// Navigation
androidx.navigation:navigation-compose:2.7.5
```

#### Database
```kotlin
// Room
androidx.room:room-runtime:2.6.1
androidx.room:room-ktx:2.6.1
ksp("androidx.room:room-compiler:2.6.1")
```

#### Networking (for future AI features)
```kotlin
// Retrofit
com.squareup.retrofit2:retrofit:2.9.0
com.squareup.retrofit2:converter-gson:2.9.0

// OkHttp
com.squareup.okhttp3:okhttp:4.12.0
com.squareup.okhttp3:logging-interceptor:4.12.0
```

#### Security (for future features)
```kotlin
// Encrypted Storage
androidx.security:security-crypto:1.1.0-alpha06

// Biometric Auth
androidx.biometric:biometric:1.1.0
```

#### Background Tasks (for future features)
```kotlin
// WorkManager
androidx.work:work-runtime-ktx:2.9.0
```

#### Charts (for future features)
```kotlin
// MPAndroidChart
com.github.PhilJay:MPAndroidChart:v3.1.0
```

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 34
- Gradle 8.2+

### Setup Instructions

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd ChildGrowthTracker
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory
   - Wait for Gradle sync to complete

3. **Build the project**
   ```bash
   # From Android Studio: Build > Make Project
   # Or from terminal:
   ./gradlew build
   ```

4. **Run the app**
   - Connect an Android device (API 26+) or start an emulator
   - Click "Run" in Android Studio or use:
   ```bash
   ./gradlew installDebug
   ```

### First Run
1. The app will open to the Profile List screen
2. Tap the "+" button to create your first child profile
3. Enter name, date of birth, and gender
4. Tap "Save" to create the profile
5. Tap "View Growth" to start tracking growth measurements

## 💾 Database Schema

### Entities Overview

#### ChildProfile
Stores basic information about each child being tracked.
```kotlin
- id: String (UUID)
- name: String
- dateOfBirth: LocalDate
- gender: Gender (MALE, FEMALE, OTHER)
- createdAt: Instant
- updatedAt: Instant
```

#### GrowthRecord
Tracks physical growth measurements over time.
```kotlin
- id: String (UUID)
- childId: String (FK → ChildProfile)
- recordDate: LocalDate
- height: Float? (cm)
- weight: Float? (kg)
- headCircumference: Float? (cm)
- notes: String?
- createdAt: Instant
```

#### Milestone
Logs developmental achievements.
```kotlin
- id: String (UUID)
- childId: String (FK → ChildProfile)
- category: MilestoneCategory
- description: String
- achievementDate: LocalDate
- notes: String?
- photoUri: String?
- createdAt: Instant
```

#### BehaviorEntry
Records daily behaviors and habits.
```kotlin
- id: String (UUID)
- childId: String (FK → ChildProfile)
- entryDate: LocalDate
- mood: Mood?
- sleepQuality: Int? (1-5)
- eatingHabits: EatingHabits?
- notes: String?
- createdAt: Instant
```

#### ParentingTip
Stores parenting advice and tips.
```kotlin
- id: String (UUID)
- title: String
- content: String
- category: TipCategory
- ageRange: AgeRange
- createdAt: Instant
```

#### ChatMessage
Stores AI chat conversation history.
```kotlin
- id: String (UUID)
- role: MessageRole (USER, ASSISTANT)
- content: String
- timestamp: Instant
```

#### WeeklySummary
Stores AI-generated weekly summaries.
```kotlin
- id: String (UUID)
- childId: String (FK → ChildProfile)
- weekStartDate: LocalDate
- weekEndDate: LocalDate
- summaryContent: String
- generatedAt: Instant
```

#### AppSettings
Stores app configuration.
```kotlin
- id: Int (always 1)
- openRouterApiKey: String?
- selectedModel: String?
- autoGenerateWeeklySummary: Boolean
```

## 🎨 UI/UX Design

### Theme
The app uses Material Design 3 with a custom color palette:

- **Primary**: Soft Blue (#6B9BD1) - Trust and calmness
- **Secondary**: Warm Peach (#FFB88C) - Warmth and care
- **Background**: Off-White (#F8F9FA) - Clean and spacious
- **Surface**: White (#FFFFFF) - Clear content areas
- **Error**: Soft Red (#E57373) - Gentle error indication
- **Success**: Soft Green (#81C784) - Positive feedback

### Typography
- **Headings**: Roboto Bold (20-24sp)
- **Body**: Roboto Regular (16sp)
- **Captions**: Roboto Light (14sp)

### Navigation Pattern
- Bottom navigation for main sections (planned)
- Floating action buttons for quick entry
- Back navigation with top app bar
- Modal screens for forms

## 🔐 Privacy & Security

### Current Implementation
- All data stored locally using Room database
- No cloud sync or external data transmission
- SQLite database stored in app-private directory

### Planned Security Features
- AES-256 encryption for sensitive data
- Android Keystore integration
- Biometric authentication (fingerprint/face)
- Encrypted SharedPreferences for API keys
- Optional encrypted backups

## 📊 Key Features Explained

### 1. Child Profile Management
**Location**: `presentation/profile/`

Manages multiple child profiles with CRUD operations.

**Flow**:
1. User opens app → ProfileListScreen
2. Tap "+" → ProfileFormScreen (create mode)
3. Enter details → ViewModel validates → Repository saves
4. Profile appears in list with calculated age

**Key Files**:
- `ProfileListScreen.kt`: Displays all profiles
- `ProfileFormScreen.kt`: Create/edit form
- `ChildProfileViewModel.kt`: State management
- `CreateChildProfileUseCase.kt`: Business logic

### 2. Growth Tracking
**Location**: `presentation/growth/`

Records and displays physical growth measurements with percentile calculations.

**Flow**:
1. Select profile → Tap "View Growth"
2. GrowthListScreen shows historical records
3. Tap "+" → GrowthEntryScreen
4. Enter measurements → Validation → Save
5. Percentiles calculated and displayed

**Key Files**:
- `GrowthListScreen.kt`: Historical records
- `GrowthEntryScreen.kt`: Entry form
- `GrowthViewModel.kt`: State management
- `CalculateGrowthPercentilesUseCase.kt`: WHO-based calculations

**Validation Rules**:
- At least one measurement required
- All values must be positive
- Height: 0-200 cm
- Weight: 0-100 kg
- Head circumference: 0-60 cm

**Percentile Calculation**:
Uses simplified WHO Child Growth Standards with LMS method. For production, consider using official WHO tables.

## 🧪 Testing Strategy (Planned)

### Unit Tests
- Use case business logic
- Percentile calculations
- Data validation
- Repository implementations

### Integration Tests
- ViewModel + Use Case integration
- Database operations
- Navigation flows

### UI Tests
- Critical user flows
- Form validation
- Screen navigation

## 🔄 Data Flow Example

### Creating a Growth Record

```
User Input (GrowthEntryScreen)
    ↓
ViewModel.saveGrowthRecord()
    ↓
CreateGrowthRecordUseCase.invoke()
    ↓ (validates input)
Repository.insertRecord()
    ↓
DAO.insertRecord()
    ↓
Room Database
    ↓ (Flow emission)
DAO.getRecordsByChildId()
    ↓
Repository (maps Entity → Domain)
    ↓
ViewModel (updates UI state)
    ↓
GrowthListScreen (displays updated list)
```

## 🛠️ Development Guidelines

### Code Style
- Follow Kotlin coding conventions
- Use meaningful variable names
- Keep functions small and focused
- Document complex logic

### Architecture Rules
- Presentation layer only depends on Domain
- Domain layer has no dependencies
- Data layer implements Domain interfaces
- Use dependency injection (manual for now)

### Commit Guidelines
- Use conventional commits format
- Keep commits atomic and focused
- Write descriptive commit messages

Example:
```
feat(growth): add percentile calculation
fix(profile): correct age calculation for leap years
docs(readme): update setup instructions
```

## 📝 Future Development

### Immediate Next Steps (Tasks 5-9)
1. Growth visualization with charts
2. Milestone tracking module
3. Daily behavior tracking
4. Parenting tips library
5. Main dashboard and navigation

### AI Features (Tasks 10-15)
1. OpenRouter API integration
2. AI chat assistant
3. Weekly summary generation
4. Automatic background summaries

### Security & Polish (Tasks 16-20)
1. Data encryption
2. Biometric authentication
3. Export/import functionality
4. UI polish and optimization

### Testing (Tasks 21-24)
1. Unit test suite
2. Integration tests
3. UI tests
4. Final QA and bug fixes

## 🤝 Contributing

This is a personal project, but suggestions and feedback are welcome!

### Development Workflow
1. Check `.kiro/specs/child-growth-tracker/tasks.md` for current progress
2. Review `design.md` for architecture decisions
3. Follow the existing code patterns
4. Test thoroughly before committing

## 📄 License

This is a private, non-commercial application for personal use.

## 📞 Support

For questions or issues, please refer to:
- `PROJECT_SETUP.md` for detailed setup information
- `.kiro/specs/child-growth-tracker/` for requirements and design docs
- Code comments for implementation details

## 🙏 Acknowledgments

- WHO Child Growth Standards for percentile calculations
- Material Design 3 for UI guidelines
- Android Jetpack for modern Android development tools

---

**Last Updated**: November 17, 2025
**Version**: 0.1.0 (Development)
**Status**: Active Development - Tasks 1-4 Complete
