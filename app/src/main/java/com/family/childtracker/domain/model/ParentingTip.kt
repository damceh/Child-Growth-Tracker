package com.family.childtracker.domain.model

import java.time.Instant

data class ParentingTip(
    val id: String,
    val title: String,
    val content: String,
    val category: TipCategory,
    val ageRange: AgeRange,
    val createdAt: Instant
)

enum class TipCategory {
    NUTRITION, SLEEP, DEVELOPMENT, BEHAVIOR, SAFETY, HEALTH
}

enum class AgeRange {
    NEWBORN, INFANT, TODDLER, PRESCHOOL, SCHOOL_AGE
}
