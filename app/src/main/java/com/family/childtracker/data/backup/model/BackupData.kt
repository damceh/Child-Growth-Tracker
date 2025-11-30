package com.family.childtracker.data.backup.model

import com.google.gson.annotations.SerializedName

/**
 * Root backup data structure containing all app data
 */
data class BackupData(
    @SerializedName("version")
    val version: Int = CURRENT_VERSION,
    
    @SerializedName("exportDate")
    val exportDate: String, // ISO-8601 format
    
    @SerializedName("encrypted")
    val encrypted: Boolean = false,
    
    @SerializedName("childProfiles")
    val childProfiles: List<ChildProfileBackup>,
    
    @SerializedName("growthRecords")
    val growthRecords: List<GrowthRecordBackup>,
    
    @SerializedName("milestones")
    val milestones: List<MilestoneBackup>,
    
    @SerializedName("behaviorEntries")
    val behaviorEntries: List<BehaviorEntryBackup>,
    
    @SerializedName("parentingTips")
    val parentingTips: List<ParentingTipBackup>,
    
    @SerializedName("chatMessages")
    val chatMessages: List<ChatMessageBackup>,
    
    @SerializedName("weeklySummaries")
    val weeklySummaries: List<WeeklySummaryBackup>,
    
    @SerializedName("appSettings")
    val appSettings: AppSettingsBackup?
) {
    companion object {
        const val CURRENT_VERSION = 1
    }
}

data class ChildProfileBackup(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("dateOfBirth")
    val dateOfBirth: String, // ISO-8601 date format
    
    @SerializedName("gender")
    val gender: String,
    
    @SerializedName("createdAt")
    val createdAt: String, // ISO-8601 timestamp
    
    @SerializedName("updatedAt")
    val updatedAt: String // ISO-8601 timestamp
)

data class GrowthRecordBackup(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("childId")
    val childId: String,
    
    @SerializedName("recordDate")
    val recordDate: String, // ISO-8601 date format
    
    @SerializedName("height")
    val height: Float?,
    
    @SerializedName("weight")
    val weight: Float?,
    
    @SerializedName("headCircumference")
    val headCircumference: Float?,
    
    @SerializedName("notes")
    val notes: String?,
    
    @SerializedName("createdAt")
    val createdAt: String // ISO-8601 timestamp
)

data class MilestoneBackup(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("childId")
    val childId: String,
    
    @SerializedName("category")
    val category: String,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("achievementDate")
    val achievementDate: String, // ISO-8601 date format
    
    @SerializedName("notes")
    val notes: String?,
    
    @SerializedName("photoUri")
    val photoUri: String?,
    
    @SerializedName("createdAt")
    val createdAt: String // ISO-8601 timestamp
)

data class BehaviorEntryBackup(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("childId")
    val childId: String,
    
    @SerializedName("entryDate")
    val entryDate: String, // ISO-8601 date format
    
    @SerializedName("mood")
    val mood: String?,
    
    @SerializedName("sleepQuality")
    val sleepQuality: Int?,
    
    @SerializedName("eatingHabits")
    val eatingHabits: String?,
    
    @SerializedName("notes")
    val notes: String?,
    
    @SerializedName("createdAt")
    val createdAt: String // ISO-8601 timestamp
)

data class ParentingTipBackup(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("content")
    val content: String,
    
    @SerializedName("category")
    val category: String,
    
    @SerializedName("ageRange")
    val ageRange: String,
    
    @SerializedName("createdAt")
    val createdAt: String // ISO-8601 timestamp
)

data class ChatMessageBackup(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("role")
    val role: String,
    
    @SerializedName("content")
    val content: String,
    
    @SerializedName("timestamp")
    val timestamp: String // ISO-8601 timestamp
)

data class WeeklySummaryBackup(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("childId")
    val childId: String,
    
    @SerializedName("weekStartDate")
    val weekStartDate: String, // ISO-8601 date format
    
    @SerializedName("weekEndDate")
    val weekEndDate: String, // ISO-8601 date format
    
    @SerializedName("summaryContent")
    val summaryContent: String,
    
    @SerializedName("generatedAt")
    val generatedAt: String // ISO-8601 timestamp
)

data class AppSettingsBackup(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("selectedModel")
    val selectedModel: String?,
    
    @SerializedName("autoGenerateWeeklySummary")
    val autoGenerateWeeklySummary: Boolean,
    
    @SerializedName("biometricAuthEnabled")
    val biometricAuthEnabled: Boolean,
    
    @SerializedName("appLockEnabled")
    val appLockEnabled: Boolean,
    
    @SerializedName("autoLockTimeoutMinutes")
    val autoLockTimeoutMinutes: Int
    
    // Note: API key and PIN code are intentionally excluded from backup for security
)
