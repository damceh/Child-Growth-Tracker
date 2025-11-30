package com.family.childtracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.family.childtracker.data.local.encryption.EncryptedTypeConverters

@Entity(tableName = "child_profiles")
data class ChildProfileEntity(
    @PrimaryKey
    val id: String,
    @TypeConverters(EncryptedTypeConverters::class)
    val name: String, // Encrypted
    val dateOfBirth: Long, // Stored as epoch day
    val gender: String,
    val createdAt: Long, // Stored as epoch millis
    val updatedAt: Long  // Stored as epoch millis
)
