# Implementation Plan

- [x] 1. Set up project structure and dependencies
  - Create new Android project with Kotlin and Jetpack Compose
  - Configure build.gradle with required dependencies (Room, Compose, Retrofit/Ktor, EncryptedSharedPreferences)
  - Set up project package structure (presentation, domain, data layers)
  - Configure minimum SDK version (API 26+)
  - _Requirements: 1.1, 6.1_

- [x] 2. Implement core data layer foundation
  - Create Room database schema and entities (ChildProfile, GrowthRecord, Milestone, BehaviorEntry, ParentingTip, ChatMessage, WeeklySummary, AppSettings)
  - Write DAO interfaces for all entities with CRUD operations
  - Implement database migrations strategy
  - Create repository interfaces in domain layer
  - Implement repository classes in data layer
  - _Requirements: 1.5, 7.1_

- [x] 3. Implement child profile management
  - Create ChildProfile domain models and use cases
  - Build profile creation form UI with Compose
  - Implement profile list screen with selection capability
  - Add profile edit functionality
  - Wire profile data flow from UI to database
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5_

- [x] 4. Build growth tracking module
  - Create GrowthRecord domain models and validation logic
  - Implement growth entry form with date picker and number inputs
  - Build historical records list screen
  - Add edit and delete functionality for growth records
  - Implement growth percentile calculation logic
  - _Requirements: 2.1, 2.2, 2.3, 2.5_

- [ ] 5. Implement growth visualization
  - Integrate charting library (MPAndroidChart or Vico)
  - Create chart composables for height, weight, and head circumference trends
  - Implement data transformation for chart display
  - Add percentile overlay on charts
  - Build chart screen with tab navigation between metrics
  - _Requirements: 2.4_

- [ ] 6. Build milestone tracking module
  - Create Milestone domain models with category enum
  - Implement milestone entry form with category selector and date picker
  - Add photo attachment capability using Android photo picker
  - Build timeline view displaying milestones chronologically
  - Implement milestone filtering by category
  - Add edit and delete functionality for milestones
  - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5_

- [ ] 7. Implement daily behavior tracking
  - Create BehaviorEntry domain models with mood and habit enums
  - Build quick-entry behavior form with emoji selectors and rating scales
  - Implement calendar view for behavior entries
  - Add free-form notes field to behavior entries
  - Create behavior entry list view with date filtering
  - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5_

- [ ] 8. Build parenting tips module
  - Create ParentingTip domain models with categories and age ranges
  - Populate initial tips database with sample content
  - Implement daily tip display on dashboard with rotation logic
  - Build tips library screen with category filtering
  - Add search functionality for tips
  - Implement favorites functionality for saving tips
  - Add age-appropriate filtering based on child's date of birth
  - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5_

- [ ] 9. Create dashboard and navigation
  - Build main dashboard screen with summary cards
  - Implement bottom navigation bar for main sections
  - Add floating action button for quick data entry
  - Create navigation graph connecting all screens
  - Implement child profile selector on dashboard
  - Display latest growth, milestone, and behavior summaries
  - _Requirements: 6.1, 6.2, 8.1_

- [ ] 10. Implement OpenRouter API client
  - Create OpenRouter API service interface with Retrofit or Ktor
  - Implement request/response data models for chat completions
  - Add authentication header injection with API key
  - Implement error handling for API responses (401, 429, 500, network errors)
  - Create repository for API interactions
  - Add retry logic with exponential backoff
  - _Requirements: 9.2, 9.6, 11.6_

- [ ] 11. Build API key configuration
  - Create AppSettings entity and DAO
  - Implement EncryptedSharedPreferences for API key storage
  - Build settings screen UI for API key input
  - Add API key masking in UI (show last 4 characters)
  - Implement "Test Connection" functionality
  - Create use case for validating API key
  - _Requirements: 11.1, 11.2, 11.3, 11.6_

- [ ] 12. Implement AI model selection
  - Create API endpoint for fetching available models
  - Implement model list caching with daily refresh
  - Build model selector dropdown in settings
  - Add default model preference storage
  - Display model information (name, description, cost)
  - _Requirements: 9.3, 11.4, 11.5_

- [ ] 13. Build AI chat interface
  - Create ChatMessage entity and DAO for conversation history
  - Implement chat screen UI with message list and input field
  - Build message composables for user and assistant messages
  - Add loading indicator for AI responses
  - Implement conversation context management (last 10 messages)
  - Create system prompt with child context (age, recent milestones)
  - Wire chat UI to OpenRouter API
  - Handle API key missing state with prompt to configure
  - _Requirements: 9.1, 9.2, 9.4, 9.5, 9.6, 9.7_

- [ ] 14. Implement weekly summary generation
  - Create WeeklySummary entity and DAO
  - Build data aggregation logic for past 7 days (growth, milestones, behaviors)
  - Create summary prompt template with child data
  - Implement summary generation use case calling OpenRouter API
  - Build weekly summaries screen showing current and historical summaries
  - Add manual summary generation trigger
  - _Requirements: 10.2, 10.3, 10.5, 10.6_

- [ ] 15. Set up automatic weekly summary generation
  - Implement WorkManager for background task scheduling
  - Create weekly summary worker that runs every Sunday at 8 PM
  - Add notification when summary is ready
  - Implement API key check before generation (skip if not configured)
  - Add settings toggle to enable/disable auto-generation
  - Configure worker constraints (network required)
  - _Requirements: 10.1, 10.4, 10.7, 11.7_

- [ ] 16. Implement data encryption
  - Set up Android Keystore for key generation
  - Implement encryption/decryption utilities using AES-256
  - Encrypt sensitive fields in database (child name, notes, API key)
  - Add migration for existing unencrypted data
  - _Requirements: 7.2, 11.2_

- [ ] 17. Add biometric authentication
  - Implement BiometricPrompt for fingerprint/face recognition
  - Create app lock screen with PIN fallback
  - Add auto-lock after inactivity timer
  - Store authentication preference in settings
  - Implement lock/unlock state management
  - _Requirements: 7.3_

- [ ] 18. Build data export and import
  - Create JSON serialization for all data entities
  - Implement export functionality to generate backup file
  - Build import functionality to restore from backup
  - Add file picker integration for import
  - Store backup files in app-specific external storage
  - Add encryption option for exported files
  - _Requirements: 7.4, 7.5_

- [ ] 19. Implement summary reports and sharing
  - Create monthly summary report generation logic
  - Build PDF generation for summary reports
  - Implement image export for summaries
  - Add share functionality using Android share sheet
  - Display growth percentiles in reports
  - Highlight significant changes in reports
  - _Requirements: 8.2, 8.3, 8.4, 8.5_

- [ ] 20. Polish UI and optimize performance
  - Apply Material Design 3 theming with custom color palette
  - Implement loading states and error messages throughout app
  - Add input validation with inline error messages
  - Optimize database queries with indexes
  - Implement auto-save for form drafts
  - Add animations and transitions between screens
  - Ensure all touch targets are minimum 48dp
  - Test on various screen sizes (5-7 inches)
  - _Requirements: 6.1, 6.2, 6.3, 6.4, 6.5_

- [ ]* 21. Write unit tests for core business logic
  - Write tests for growth percentile calculations
  - Test data validation logic for all entities
  - Test encryption/decryption utilities
  - Test repository implementations with in-memory database
  - Test use cases for profile, growth, milestone, and behavior management
  - Test OpenRouter API client with mock responses
  - Test weekly summary data aggregation logic
  - _Requirements: All core requirements_

- [ ]* 22. Write integration tests
  - Test end-to-end data flow from UI to database
  - Test navigation flows between screens
  - Test ViewModel and use case integration
  - Test API integration with real OpenRouter endpoints (using test key)
  - Test WorkManager background task execution
  - _Requirements: All core requirements_

- [ ]* 23. Write UI tests for critical paths
  - Test child profile creation and selection
  - Test growth record entry and display
  - Test milestone logging flow
  - Test behavior entry submission
  - Test AI chat message sending and receiving
  - Test settings configuration
  - _Requirements: 1.1, 2.1, 3.2, 4.1, 9.2, 11.1_

- [ ] 24. Final testing and bug fixes
  - Perform manual testing on physical Android devices
  - Test with different screen sizes and orientations
  - Verify biometric authentication on supported devices
  - Test data export/import with large datasets
  - Verify API error handling with various scenarios
  - Test app behavior with no internet connection
  - Fix any discovered bugs and issues
  - _Requirements: All requirements_
