package com.family.childtracker.domain.usecase

import com.family.childtracker.domain.model.GrowthRecord
import com.family.childtracker.domain.repository.GrowthRecordRepository
import kotlinx.coroutines.flow.Flow

class GetGrowthRecordsUseCase(
    private val repository: GrowthRecordRepository
) {
    operator fun invoke(childId: String): Flow<List<GrowthRecord>> {
        return repository.getRecordsByChildId(childId)
    }
}
