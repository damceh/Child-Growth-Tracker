package com.family.childtracker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "growth_records",
    foreignKeys = [
        ForeignKey(
            entity = ChildProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["childId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("childId"), Index("recordDate")]
)
data class GrowthRecordEntity(
    @PrimaryKey
    val id: String,
    val childId: String,
    val recordDate: Long, // Stored as epoch day
    val height: Float?,
    val weight: Float?,
    val headCircumference: Float?,
    val notes: String?,
    val createdAt: Long // Stored as epoch millis
)
