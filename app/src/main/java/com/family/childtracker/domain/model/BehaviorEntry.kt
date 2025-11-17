package com.family.childtracker.domain.model

import java.time.Instant
import java.time.LocalDate

data class BehaviorEntry(
    val id: String,
    val childId: String,
    val entryDate: LocalDate,
    val mood: Mood?,
    val sleepQuality: Int?, // 1-5 scale
    val eatingHabits: EatingHabits?,
    val notes: String?,
    val createdAt: Instant
)

enum class Mood {
    HAPPY, CALM, FUSSY, CRANKY, ENERGETIC
}

enum class EatingHabits {
    EXCELLENT, GOOD, FAIR, POOR, REFUSED
}
