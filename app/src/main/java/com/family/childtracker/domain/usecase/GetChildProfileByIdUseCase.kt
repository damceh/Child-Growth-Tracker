package com.family.childtracker.domain.usecase

import com.family.childtracker.domain.model.ChildProfile
import com.family.childtracker.domain.repository.ChildProfileRepository
import kotlinx.coroutines.flow.Flow

class GetChildProfileByIdUseCase(
    private val repository: ChildProfileRepository
) {
    operator fun invoke(id: String): Flow<ChildProfile?> {
        return repository.getProfileById(id)
    }
}
