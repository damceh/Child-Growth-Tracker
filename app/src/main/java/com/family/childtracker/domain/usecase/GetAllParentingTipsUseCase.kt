package com.family.childtracker.domain.usecase

import com.family.childtracker.domain.model.ParentingTip
import com.family.childtracker.domain.repository.ParentingTipRepository
import kotlinx.coroutines.flow.Flow

class GetAllParentingTipsUseCase(
    private val repository: ParentingTipRepository
) {
    operator fun invoke(): Flow<List<ParentingTip>> {
        return repository.getAllTips()
    }
}
