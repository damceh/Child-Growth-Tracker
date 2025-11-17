package com.family.childtracker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "milestones",
    foreignKeys = [
        ForeignKey(
            entity = ChildProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["childId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("childId"), Index("achievementDate")]
)
data class MilestoneEntity(
    @PrimaryKey
    val id: String,
    val childId: String,
    val category: String,
    val description: String,
    val achievementDate: Long, // Stored as epoch day
    val notes: String?,
    val photoUri: String?,
    val createdAt: Long // Stored as epoch millis
)
