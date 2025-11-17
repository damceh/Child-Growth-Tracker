package com.family.childtracker.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.family.childtracker.data.local.dao.*
import com.family.childtracker.data.local.entity.*

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
    version = 1,
    exportSchema = true
)
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
