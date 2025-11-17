package com.family.childtracker.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.family.childtracker.domain.repository.ChildProfileRepository
import com.family.childtracker.domain.usecase.*

class ChildProfileViewModelFactory(
    private val repository: ChildProfileRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChildProfileViewModel::class.java)) {
            return ChildProfileViewModel(
                getAllChildProfilesUseCase = GetAllChildProfilesUseCase(repository),
                getChildProfileByIdUseCase = GetChildProfileByIdUseCase(repository),
                createChildProfileUseCase = CreateChildProfileUseCase(repository),
                updateChildProfileUseCase = UpdateChildProfileUseCase(repository),
                deleteChildProfileUseCase = DeleteChildProfileUseCase(repository)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
