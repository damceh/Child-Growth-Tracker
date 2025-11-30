package com.family.childtracker.domain.usecase

import com.family.childtracker.domain.repository.MilestoneRepository

class DeleteMilestoneUseCase(
    private val milestoneRepository: MilestoneRepository
) {
    suspend operator fun invoke(milestoneId: String) {
        milestoneRepository.deleteMilestoneById(milestoneId)
    }
}
