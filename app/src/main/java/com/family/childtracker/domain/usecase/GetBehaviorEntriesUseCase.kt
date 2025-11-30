package com.family.childtracker.domain.usecase

import com.family.childtracker.domain.model.BehaviorEntry
import com.family.childtracker.domain.repository.BehaviorEntryRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class GetBehaviorEntriesUseCase(
    private val repository: BehaviorEntryRepository
) {
    operator fun invoke(childId: String): Flow<List<BehaviorEntry>> {
        return repository.getBehaviorEntriesByChildId(childId)
    }

    fun getByDateRange(
        childId: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<BehaviorEntry>> {
        return repository.getBehaviorEntriesByDateRange(childId, startDate, endDate)
    }
}
