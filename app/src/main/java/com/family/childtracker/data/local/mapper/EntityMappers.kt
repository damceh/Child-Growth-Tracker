package com.family.childtracker.data.local.mapper

import com.family.childtracker.data.local.entity.*
import com.family.childtracker.domain.model.*
import java.time.Instant
import java.time.LocalDate

// ChildProfile Mappers
fun ChildProfileEntity.toDomain(): ChildProfile {
    return ChildProfile(
        id = id,
        name = name,
        dateOfBirth = LocalDate.ofEpochDay(dateOfBirth),
        gender = Gender.valueOf(gender),
        createdAt = Instant.ofEpochMilli(createdAt),
        updatedAt = Instant.ofEpochMilli(updatedAt)
    )
}

fun ChildProfile.toEntity(): ChildProfileEntity {
    return ChildProfileEntity(
        id = id,
        name = name,
        dateOfBirth = dateOfBirth.toEpochDay(),
        gender = gender.name,
        createdAt = createdAt.toEpochMilli(),
        updatedAt = updatedAt.toEpochMilli()
    )
}

// GrowthRecord Mappers
fun GrowthRecordEntity.toDomain(): GrowthRecord {
    return GrowthRecord(
        id = id,
        childId = childId,
        recordDate = LocalDate.ofEpochDay(recordDate),
        height = height,
        weight = weight,
        headCircumference = headCircumference,
        notes = notes,
        createdAt = Instant.ofEpochMilli(createdAt)
    )
}

fun GrowthRecord.toEntity(): GrowthRecordEntity {
    return GrowthRecordEntity(
        id = id,
        childId = childId,
        recordDate = recordDate.toEpochDay(),
        height = height,
        weight = weight,
        headCircumference = headCircumference,
        notes = notes,
        createdAt = createdAt.toEpochMilli()
    )
}

// Milestone Mappers
fun MilestoneEntity.toDomain(): Milestone {
    return Milestone(
        id = id,
        childId = childId,
        category = MilestoneCategory.valueOf(category),
        description = description,
        achievementDate = LocalDate.ofEpochDay(achievementDate),
        notes = notes,
        photoUri = photoUri,
        createdAt = Instant.ofEpochMilli(createdAt)
    )
}

fun Milestone.toEntity(): MilestoneEntity {
    return MilestoneEntity(
        id = id,
        childId = childId,
        category = category.name,
        description = description,
        achievementDate = achievementDate.toEpochDay(),
        notes = notes,
        photoUri = photoUri,
        createdAt = createdAt.toEpochMilli()
    )
}

// BehaviorEntry Mappers
fun BehaviorEntryEntity.toDomain(): BehaviorEntry {
    return BehaviorEntry(
        id = id,
        childId = childId,
        entryDate = LocalDate.ofEpochDay(entryDate),
        mood = mood?.let { Mood.valueOf(it) },
        sleepQuality = sleepQuality,
        eatingHabits = eatingHabits?.let { EatingHabits.valueOf(it) },
        notes = notes,
        createdAt = Instant.ofEpochMilli(createdAt)
    )
}

fun BehaviorEntry.toEntity(): BehaviorEntryEntity {
    return BehaviorEntryEntity(
        id = id,
        childId = childId,
        entryDate = entryDate.toEpochDay(),
        mood = mood?.name,
        sleepQuality = sleepQuality,
        eatingHabits = eatingHabits?.name,
        notes = notes,
        createdAt = createdAt.toEpochMilli()
    )
}

// ParentingTip Mappers
fun ParentingTipEntity.toDomain(): ParentingTip {
    return ParentingTip(
        id = id,
        title = title,
        content = content,
        category = TipCategory.valueOf(category),
        ageRange = AgeRange.valueOf(ageRange),
        createdAt = Instant.ofEpochMilli(createdAt)
    )
}

fun ParentingTip.toEntity(): ParentingTipEntity {
    return ParentingTipEntity(
        id = id,
        title = title,
        content = content,
        category = category.name,
        ageRange = ageRange.name,
        createdAt = createdAt.toEpochMilli()
    )
}

// ChatMessage Mappers
fun ChatMessageEntity.toDomain(): ChatMessage {
    return ChatMessage(
        id = id,
        role = MessageRole.valueOf(role),
        content = content,
        timestamp = Instant.ofEpochMilli(timestamp)
    )
}

fun ChatMessage.toEntity(): ChatMessageEntity {
    return ChatMessageEntity(
        id = id,
        role = role.name,
        content = content,
        timestamp = timestamp.toEpochMilli()
    )
}

// WeeklySummary Mappers
fun WeeklySummaryEntity.toDomain(): WeeklySummary {
    return WeeklySummary(
        id = id,
        childId = childId,
        weekStartDate = LocalDate.ofEpochDay(weekStartDate),
        weekEndDate = LocalDate.ofEpochDay(weekEndDate),
        summaryContent = summaryContent,
        generatedAt = Instant.ofEpochMilli(generatedAt)
    )
}

fun WeeklySummary.toEntity(): WeeklySummaryEntity {
    return WeeklySummaryEntity(
        id = id,
        childId = childId,
        weekStartDate = weekStartDate.toEpochDay(),
        weekEndDate = weekEndDate.toEpochDay(),
        summaryContent = summaryContent,
        generatedAt = generatedAt.toEpochMilli()
    )
}

// AppSettings Mappers
fun AppSettingsEntity.toDomain(): AppSettings {
    return AppSettings(
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

fun AppSettings.toEntity(): AppSettingsEntity {
    return AppSettingsEntity(
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
