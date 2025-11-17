package com.family.childtracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "child_profiles")
data class ChildProfileEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val dateOfBirth: Long, // Stored as epoch day
    val gender: String,
    val createdAt: Long, // Stored as epoch millis
    val updatedAt: Long  // Stored as epoch millis
)
