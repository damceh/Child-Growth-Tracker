# Project Setup Documentation

## Overview
This document describes the initial Android project setup for the Child Growth Tracker application.

## Configuration

### Build Configuration
- **Minimum SDK**: API 26 (Android 8.0 Oreo)
- **Target SDK**: API 34
- **Compile SDK**: API 34
- **Kotlin Version**: 1.9.20
- **Gradle Version**: 8.2
- **Java Version**: 17

### Dependencies Configured

#### Core Android & Jetpack Compose
- androidx.core:core-ktx:1.12.0
- androidx.lifecycle:lifecycle-runtime-ktx:2.6.2
- androidx.activity:activity-compose:1.8.1
- Compose BOM 2023.10.01 (includes UI, Material3, etc.)
- androidx.navigation:navigation-compose:2.7.5

#### Database
- Room 2.6.1 (runtime, ktx, compiler with KSP)

#### Networking
- Retrofit 2.9.0 (with Gson converter)
- OkHttp 4.12.0 (with logging interceptor)
- Gson 2.10.1

#### Security
- androidx.security:security-crypto:1.1.0-alpha06 (EncryptedSharedPreferences)
- androidx.biometric:biometric:1.1.0

#### Background Tasks
- androidx.work:work-runtime-ktx:2.9.0

#### Charts
- MPAndroidChart v3.1.0 (via JitPack)

#### Testing
- JUnit 4.13.2
- Coroutines Test 1.7.3
- Room Testing
- Espresso & Compose UI Testing

## Project Structure

```
ChildGrowthTracker/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/family/childtracker/
│   │       │   ├── ChildTrackerApplication.kt
│   │       │   ├── data/              # Data layer
│   │       │   │   └── README.md
│   │       │   ├── domain/            # Domain layer
│   │       │   │   └── README.md
│   │       │   └── presentation/      # Presentation layer
│   │       │       ├── MainActivity.kt
│   │       │       ├── theme/
│   │       │       │   ├── Color.kt
│   │       │       │   ├── Theme.kt
│   │       │       │   └── Type.kt
│   │       │       └── README.md
│   │       ├── res/
│   │       │   ├── drawable/
│   │       │   ├── mipmap-anydpi-v26/
│   │       │   ├── values/
│   │       │   │   ├── strings.xml
│   │       │   │   ├── themes.xml
│   │       │   │   └── ic_launcher_background.xml
│   │       │   └── xml/
│   │       │       ├── backup_rules.xml
│   │       │       └── data_extraction_rules.xml
│   │       └── AndroidManifest.xml
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties
├── .gitignore
├── build.gradle.kts
├── gradle.properties
├── settings.gradle.kts
└── README.md
```

## Architecture Layers

### Presentation Layer
- Jetpack Compose UI components
- ViewModels for state management
- Navigation setup
- Material Design 3 theme with custom colors

### Domain Layer
- Business logic use cases
- Domain models (entities)
- Repository interfaces

### Data Layer
- Repository implementations
- Room database and DAOs
- Retrofit API services
- Data mappers

## Theme Configuration

### Colors
- **Primary**: Soft Blue (#6B9BD1)
- **Secondary**: Warm Peach (#FFB88C)
- **Background**: Off-White (#F8F9FA)
- **Surface**: White (#FFFFFF)
- **Error**: Soft Red (#E57373)
- **Success**: Soft Green (#81C784)

### Typography
- Headings: Roboto Bold (20-24sp)
- Body: Roboto Regular (16sp)
- Captions: Roboto Light (14sp)

## Permissions Configured
- INTERNET (for OpenRouter API)
- USE_BIOMETRIC (for authentication)
- READ_MEDIA_IMAGES (for photo picker, API 33+)
- READ_EXTERNAL_STORAGE (for older devices, API 26-32)

## Security Features
- Encrypted SharedPreferences for API keys
- Android Keystore integration ready
- Biometric authentication support
- Backup exclusions for sensitive data

## Next Steps
The project structure is now ready for implementation of:
1. Core data layer (Room database entities and DAOs)
2. Child profile management
3. Growth tracking features
4. Milestone logging
5. Behavior tracking
6. AI integration with OpenRouter
7. And more features as per the implementation plan

## Build Instructions
1. Open project in Android Studio
2. Sync Gradle files
3. Build and run on emulator or device (API 26+)
