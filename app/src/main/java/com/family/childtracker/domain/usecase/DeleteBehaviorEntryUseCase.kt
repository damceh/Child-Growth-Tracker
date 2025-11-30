package com.family.childtracker.domain.usecase

import com.family.childtracker.domain.model.BehaviorEntry
import com.family.childtracker.domain.repository.BehaviorEntryRepository

class DeleteBehaviorEntryUseCase(
    private val repository: BehaviorEntryRepository
) {
    suspend operator fun invoke(entry: BehaviorEntry): Result<Unit> {
        return try {
            repository.deleteBehaviorEntry(entry)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
