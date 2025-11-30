package com.family.childtracker.domain.usecase

import com.family.childtracker.domain.model.Milestone
import com.family.childtracker.domain.repository.MilestoneRepository

class CreateMilestoneUseCase(
    private val milestoneRepository: MilestoneRepository
) {
    suspend operator fun invoke(milestone: Milestone) {
        milestoneRepository.insertMilestone(milestone)
    }
}
