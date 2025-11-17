package com.family.childtracker.domain.usecase

import com.family.childtracker.domain.model.ChildProfile
import com.family.childtracker.domain.model.Gender
import com.family.childtracker.domain.repository.ChildProfileRepository
import java.time.Instant
import java.time.LocalDate

class UpdateChildProfileUseCase(
    private val repository: ChildProfileRepository
) {
    suspend operator fun invoke(
        id: String,
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
            
            val profile = ChildProfile(
                id = id,
                name = name.trim(),
                dateOfBirth = dateOfBirth,
                gender = gender,
                createdAt = Instant.now(), // Will be preserved from original
                updatedAt = Instant.now()
            )
            
            repository.updateProfile(profile)
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
