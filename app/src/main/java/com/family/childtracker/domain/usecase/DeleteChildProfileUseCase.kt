package com.family.childtracker.domain.usecase

import com.family.childtracker.domain.repository.ChildProfileRepository

class DeleteChildProfileUseCase(
    private val repository: ChildProfileRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return try {
            repository.deleteProfile(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
