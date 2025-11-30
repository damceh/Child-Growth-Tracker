package com.family.childtracker.data.backup.mapper

import com.family.childtracker.data.backup.model.*
import com.family.childtracker.domain.model.*
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Maps between domain models and backup models
 */
object BackupMapper {
    
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val timestampFormatter = DateTimeFormatter.ISO_INSTANT
    
    // ChildProfile mappings
    fun ChildProfile.toBackup(): ChildProfileBackup = ChildProfileBackup(
        id = id,
        name = name,
        dateOfBirth = dateOfBirth.format(dateFormatter),
        gender = gender.name,
        createdAt = createdAt.toString(),
        updatedAt = updatedAt.toString()
    )
    
    fun ChildProfileBackup.toDomain(): ChildProfile = ChildProfile(
        id = id,
        name = name,
        dateOfBirth = LocalDate.parse(dateOfBirth, dateFormatter),
        gender = Gender.valueOf(gender),
        createdAt = Instant.parse(createdAt),
        updatedAt = Instant.parse(updatedAt)
    )
    
    // GrowthRecord mappings
    fun GrowthRecord.toBackup(): GrowthRecordBackup = GrowthRecordBackup(
        id = id,
        childId = childId,
        recordDate = recordDate.format(dateFormatter),
        height = height,
        weight = weight,
        headCircumference = headCircumference,
        notes = notes,
        createdAt = createdAt.toString()
    )
    
    fun GrowthRecordBackup.toDomain(): GrowthRecord = GrowthRecord(
        id = id,
        childId = childId,
        recordDate = LocalDate.parse(recordDate, dateFormatter),
        height = height,
        weight = weight,
        headCircumference = headCircumference,
        notes = notes,
        createdAt = Instant.parse(createdAt)
    )
    
    // Milestone mappings
    fun Milestone.toBackup(): MilestoneBackup = MilestoneBackup(
        id = id,
        childId = childId,
        category = category.name,
        description = description,
        achievementDate = achievementDate.format(dateFormatter),
        notes = notes,
        photoUri = photoUri,
        createdAt = createdAt.toString()
    )
    
    fun MilestoneBackup.toDomain(): Milestone = Milestone(
        id = id,
        childId = childId,
        category = MilestoneCategory.valueOf(category),
        description = description,
        achievementDate = LocalDate.parse(achievementDate, dateFormatter),
        notes = notes,
        photoUri = photoUri,
        createdAt = Instant.parse(createdAt)
    )
    
    // BehaviorEntry mappings
    fun BehaviorEntry.toBackup(): BehaviorEntryBackup = BehaviorEntryBackup(
        id = id,
        childId = childId,
        entryDate = entryDate.format(dateFormatter),
        mood = mood?.name,
        sleepQuality = sleepQuality,
        eatingHabits = eatingHabits?.name,
        notes = notes,
        createdAt = createdAt.toString()
    )
    
    fun BehaviorEntryBackup.toDomain(): BehaviorEntry = BehaviorEntry(
        id = id,
        childId = childId,
        entryDate = LocalDate.parse(entryDate, dateFormatter),
        mood = mood?.let { Mood.valueOf(it) },
        sleepQuality = sleepQuality,
        eatingHabits = eatingHabits?.let { EatingHabits.valueOf(it) },
        notes = notes,
        createdAt = Instant.parse(createdAt)
    )
    
    // ParentingTip mappings
    fun ParentingTip.toBackup(): ParentingTipBackup = ParentingTipBackup(
        id = id,
        title = title,
        content = content,
        category = category.name,
        ageRange = ageRange.name,
        createdAt = createdAt.toString()
    )
    
    fun ParentingTipBackup.toDomain(): ParentingTip = ParentingTip(
        id = id,
        title = title,
        content = content,
        category = TipCategory.valueOf(category),
        ageRange = AgeRange.valueOf(ageRange),
        createdAt = Instant.parse(createdAt)
    )
    
    // ChatMessage mappings
    fun ChatMessage.toBackup(): ChatMessageBackup = ChatMessageBackup(
        id = id,
        role = role.name,
        content = content,
        timestamp = timestamp.toString()
    )
    
    fun ChatMessageBackup.toDomain(): ChatMessage = ChatMessage(
        id = id,
        role = MessageRole.valueOf(role),
        content = content,
        timestamp = Instant.parse(timestamp)
    )
    
    // WeeklySummary mappings
    fun WeeklySummary.toBackup(): WeeklySummaryBackup = WeeklySummaryBackup(
        id = id,
        childId = childId,
        weekStartDate = weekStartDate.format(dateFormatter),
        weekEndDate = weekEndDate.format(dateFormatter),
        summaryContent = summaryContent,
        generatedAt = generatedAt.toString()
    )
    
    fun WeeklySummaryBackup.toDomain(): WeeklySummary = WeeklySummary(
        id = id,
        childId = childId,
        weekStartDate = LocalDate.parse(weekStartDate, dateFormatter),
        weekEndDate = LocalDate.parse(weekEndDate, dateFormatter),
        summaryContent = summaryContent,
        generatedAt = Instant.parse(generatedAt)
    )
    
    // AppSettings mappings
    fun AppSettings.toBackup(): AppSettingsBackup = AppSettingsBackup(
        id = id,
        selectedModel = selectedModel,
        autoGenerateWeeklySummary = autoGenerateWeeklySummary,
        biometricAuthEnabled = biometricAuthEnabled,
        appLockEnabled = appLockEnabled,
        autoLockTimeoutMinutes = autoLockTimeoutMinutes
    )
    
    fun AppSettingsBackup.toDomain(
        openRouterApiKey: String? = null,
        cachedModelsJson: String? = null,
        modelsCacheTimestamp: Long? = null,
        pinCode: String? = null
    ): AppSettings = AppSettings(
        id = id,
        openRouterApiKey = openRouterApiKey,
        selectedModel = selectedModel,
        autoGenerateWeeklySummary = autoGenerateWeeklySummary,
        cachedModelsJson = cachedModelsJson,
        modelsCacheTimestamp = modelsCacheTimestamp,
        biometricAuthEnabled = biometricAuthEnabled,
        appLockEnabled = appLockEnabled,
        pinCode = pinCode,
        autoLockTimeoutMinutes = autoLockTimeoutMinutes
    )
}
