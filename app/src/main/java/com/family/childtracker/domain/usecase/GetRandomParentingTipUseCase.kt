package com.family.childtracker.domain.usecase

import com.family.childtracker.domain.model.ParentingTip
import com.family.childtracker.domain.repository.ParentingTipRepository

class GetRandomParentingTipUseCase(
    private val repository: ParentingTipRepository
) {
    suspend operator fun invoke(): ParentingTip? {
        return repository.getRandomTip()
    }
}
