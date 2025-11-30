package com.family.childtracker.domain.usecase

import com.family.childtracker.domain.model.Milestone
import com.family.childtracker.domain.repository.MilestoneRepository

class GetMilestoneByIdUseCase(
    private val milestoneRepository: MilestoneRepository
) {
    suspend operator fun invoke(milestoneId: String): Milestone? {
        return milestoneRepository.getMilestoneById(milestoneId)
    }
}
