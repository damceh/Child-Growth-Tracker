package com.family.childtracker.domain.usecase

import com.family.childtracker.domain.model.ParentingTip
import com.family.childtracker.domain.model.TipCategory
import com.family.childtracker.domain.repository.ParentingTipRepository
import kotlinx.coroutines.flow.Flow

class GetTipsByCategoryUseCase(
    private val repository: ParentingTipRepository
) {
    operator fun invoke(category: TipCategory): Flow<List<ParentingTip>> {
        return repository.getTipsByCategory(category)
    }
}
