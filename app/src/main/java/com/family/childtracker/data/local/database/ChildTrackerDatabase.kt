package com.family.childtracker.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.family.childtracker.data.local.dao.*
import com.family.childtracker.data.local.entity.*
import com.family.childtracker.data.local.encryption.EncryptedTypeConverters

@Database(
    entities = [
        ChildProfileEntity::class,
        GrowthRecordEntity::class,
        MilestoneEntity::class,
        BehaviorEntryEntity::class,
        ParentingTipEntity::class,
        ChatMessageEntity::class,
        WeeklySummaryEntity::class,
        AppSettingsEntity::class
    ],
    version = 4, // Incremented for biometric authentication fields
    exportSchema = true
)
@TypeConverters(EncryptedTypeConverters::class)
abstract class ChildTrackerDatabase : RoomDatabase() {
    abstract fun childProfileDao(): ChildProfileDao
    abstract fun growthRecordDao(): GrowthRecordDao
    abstract fun milestoneDao(): MilestoneDao
    abstract fun behaviorEntryDao(): BehaviorEntryDao
    abstract fun parentingTipDao(): ParentingTipDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun weeklySummaryDao(): WeeklySummaryDao
    abstract fun appSettingsDao(): AppSettingsDao
}
