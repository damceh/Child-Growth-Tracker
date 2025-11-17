package com.family.childtracker.domain.model

import java.time.Instant
import java.time.LocalDate

data class ChildProfile(
    val id: String,
    val name: String,
    val dateOfBirth: LocalDate,
    val gender: Gender,
    val createdAt: Instant,
    val updatedAt: Instant
)

enum class Gender {
    MALE, FEMALE, OTHER
}
