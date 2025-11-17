package com.family.childtracker.domain.usecase

import com.family.childtracker.domain.repository.GrowthRecordRepository

class DeleteGrowthRecordUseCase(
    private val repository: GrowthRecordRepository
) {
    suspend operator fun invoke(recordId: String): Result<Unit> {
        return try {
            repository.deleteRecordById(recordId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
