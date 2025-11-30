package com.family.childtracker.domain.usecase

import com.family.childtracker.domain.model.AgeRange
import com.family.childtracker.domain.model.ParentingTip
import com.family.childtracker.domain.repository.ParentingTipRepository
import kotlinx.coroutines.flow.Flow

class GetTipsByAgeRangeUseCase(
    private val repository: ParentingTipRepository
) {
    operator fun invoke(ageRange: AgeRange): Flow<List<ParentingTip>> {
        return repository.getTipsByAgeRange(ageRange)
    }
}
