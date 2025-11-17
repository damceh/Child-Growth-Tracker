package com.family.childtracker.domain.model

import java.time.Instant
import java.time.LocalDate

data class WeeklySummary(
    val id: String,
    val childId: String,
    val weekStartDate: LocalDate,
    val weekEndDate: LocalDate,
    val summaryContent: String,
    val generatedAt: Instant
)
