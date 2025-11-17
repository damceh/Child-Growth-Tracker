package com.family.childtracker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "weekly_summaries",
    foreignKeys = [
        ForeignKey(
            entity = ChildProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["childId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("childId"), Index("weekStartDate")]
)
data class WeeklySummaryEntity(
    @PrimaryKey
    val id: String,
    val childId: String,
    val weekStartDate: Long, // Stored as epoch day
    val weekEndDate: Long, // Stored as epoch day
    val summaryContent: String,
    val generatedAt: Long // Stored as epoch millis
)
