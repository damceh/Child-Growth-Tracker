package com.family.childtracker.domain.model

import java.time.Instant
import java.time.LocalDate

data class Milestone(
    val id: String,
    val childId: String,
    val category: MilestoneCategory,
    val description: String,
    val achievementDate: LocalDate,
    val notes: String?,
    val photoUri: String?,
    val createdAt: Instant
)

enum class MilestoneCategory {
    PHYSICAL, COGNITIVE, SOCIAL, LANGUAGE, CUSTOM
}
