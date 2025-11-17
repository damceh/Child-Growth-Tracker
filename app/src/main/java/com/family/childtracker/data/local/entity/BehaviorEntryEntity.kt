package com.family.childtracker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "behavior_entries",
    foreignKeys = [
        ForeignKey(
            entity = ChildProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["childId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("childId"), Index("entryDate")]
)
data class BehaviorEntryEntity(
    @PrimaryKey
    val id: String,
    val childId: String,
    val entryDate: Long, // Stored as epoch day
    val mood: String?,
    val sleepQuality: Int?,
    val eatingHabits: String?,
    val notes: String?,
    val createdAt: Long // Stored as epoch millis
)
