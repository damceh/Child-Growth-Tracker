package com.family.childtracker.domain.usecase

import com.family.childtracker.domain.model.GrowthRecord
import com.family.childtracker.domain.repository.GrowthRecordRepository

class UpdateGrowthRecordUseCase(
    private val repository: GrowthRecordRepository
) {
    suspend operator fun invoke(
        record: GrowthRecord,
        height: Float?,
        weight: Float?,
        headCircumference: Float?,
        notes: String?
    ): Result<GrowthRecord> {
        return try {
            // Validation
            if (height != null && height <= 0) {
                return Result.failure(IllegalArgumentException("Height must be positive"))
            }
            if (weight != null && weight <= 0) {
                return Result.failure(IllegalArgumentException("Weight must be positive"))
            }
            if (headCircumference != null && headCircumference <= 0) {
                return Result.failure(IllegalArgumentException("Head circumference must be positive"))
            }
            if (height == null && weight == null && headCircumference == null) {
                return Result.failure(IllegalArgumentException("At least one measurement is required"))
            }

            val updatedRecord = record.copy(
                height = height,
                weight = weight,
                headCircumference = headCircumference,
                notes = notes?.trim()?.takeIf { it.isNotEmpty() }
            )

            repository.updateRecord(updatedRecord)
            Result.success(updatedRecord)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
