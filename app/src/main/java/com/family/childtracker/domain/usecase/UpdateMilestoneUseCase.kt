package com.family.childtracker.domain.usecase

import com.family.childtracker.domain.model.Milestone
import com.family.childtracker.domain.repository.MilestoneRepository

class UpdateMilestoneUseCase(
    private val milestoneRepository: MilestoneRepository
) {
    suspend operator fun invoke(milestone: Milestone) {
        milestoneRepository.updateMilestone(milestone)
    }
}
