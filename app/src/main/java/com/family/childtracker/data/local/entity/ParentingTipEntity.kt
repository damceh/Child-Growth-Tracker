package com.family.childtracker.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "parenting_tips",
    indices = [Index("category"), Index("ageRange")]
)
data class ParentingTipEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val content: String,
    val category: String,
    val ageRange: String,
    val createdAt: Long // Stored as epoch millis
)
