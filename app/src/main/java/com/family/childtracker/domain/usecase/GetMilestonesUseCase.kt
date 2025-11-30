package com.family.childtracker.domain.usecase

import com.family.childtracker.domain.model.Milestone
import com.family.childtracker.domain.model.MilestoneCategory
import com.family.childtracker.domain.repository.MilestoneRepository
import kotlinx.coroutines.flow.Flow

class GetMilestonesUseCase(
    private val milestoneRepository: MilestoneRepository
) {
    operator fun invoke(childId: String, category: MilestoneCategory?): Flow<List<Milestone>> {
        return if (category != null) {
            milestoneRepository.getMilestonesByCategory(childId, category)
        } else {
            milestoneRepository.getMilestonesByChildId(childId)
        }
    }
}
