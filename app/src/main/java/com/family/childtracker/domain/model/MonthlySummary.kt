package com.family.childtracker.domain.model

import java.time.Instant
import java.time.LocalDate

data class MonthlySummary(
    val childId: String,
    val childName: String,
    val monthStart: LocalDate,
    val monthEnd: LocalDate,
    val growthSummary: GrowthSummary?,
    val milestones: List<Milestone>,
    val behaviorSummary: BehaviorSummary?,
    val generatedAt: Instant
)

data class GrowthSummary(
    val startHeight: Float?,
    val endHeight: Float?,
    val heightChange: Float?,
    val heightPercentile: Int?,
    val startWeight: Float?,
    val endWeight: Float?,
    val weightChange: Float?,
    val weightPercentile: Int?,
    val startHeadCircumference: Float?,
    val endHeadCircumference: Float?,
    val headCircumferenceChange: Float?,
    val headCircumferencePercentile: Int?,
    val hasSignificantChange: Boolean
)

data class BehaviorSummary(
    val totalEntries: Int,
    val averageSleepQuality: Float?,
    val dominantMood: Mood?,
    val dominantEatingHabits: EatingHabits?,
    val positiveNotes: List<String>
)
