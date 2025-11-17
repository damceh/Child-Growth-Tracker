package com.family.childtracker.domain.model

import java.time.Instant
import java.time.LocalDate

data class GrowthRecord(
    val id: String,
    val childId: String,
    val recordDate: LocalDate,
    val height: Float?, // in cm
    val weight: Float?, // in kg
    val headCircumference: Float?, // in cm
    val notes: String?,
    val createdAt: Instant
)
