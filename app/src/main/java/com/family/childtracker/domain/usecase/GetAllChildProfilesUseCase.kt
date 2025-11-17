package com.family.childtracker.domain.usecase

import com.family.childtracker.domain.model.ChildProfile
import com.family.childtracker.domain.repository.ChildProfileRepository
import kotlinx.coroutines.flow.Flow

class GetAllChildProfilesUseCase(
    private val repository: ChildProfileRepository
) {
    operator fun invoke(): Flow<List<ChildProfile>> {
        return repository.getAllProfiles()
    }
}
