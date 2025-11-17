package com.family.childtracker.domain.usecase

import com.family.childtracker.domain.model.ChildProfile
import com.family.childtracker.domain.model.Gender
import com.family.childtracker.domain.repository.ChildProfileRepository
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

class CreateChildProfileUseCase(
    private val repository: ChildProfileRepository
) {
    suspend operator fun invoke(
        name: String,
        dateOfBirth: LocalDate,
        gender: Gender
    ): Result<ChildProfile> {
        return try {
            if (name.isBlank()) {
                return Result.failure(IllegalArgumentException("Name cannot be empty"))
            }
            
            if (dateOfBirth.isAfter(LocalDate.now())) {
                return Result.failure(IllegalArgumentException("Date of birth cannot be in the future"))
            }
            
            val now = Instant.now()
            val profile = ChildProfile(
                id = UUID.randomUUID().toString(),
                name = name.trim(),
                dateOfBirth = dateOfBirth,
                gender = gender,
                createdAt = now,
                updatedAt = now
            )
            
            repository.insertProfile(profile)
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
