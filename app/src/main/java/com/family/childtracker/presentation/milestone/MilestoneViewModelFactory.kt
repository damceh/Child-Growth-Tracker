package com.family.childtracker.presentation.milestone

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.family.childtracker.data.local.database.DatabaseProvider
import com.family.childtracker.data.repository.MilestoneRepositoryImpl
import com.family.childtracker.domain.usecase.*

class MilestoneViewModelFactory(
    private val databaseProvider: DatabaseProvider
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MilestoneViewModel::class.java)) {
            val database = databaseProvider.getDatabase()
            val repository = MilestoneRepositoryImpl(database.milestoneDao())
            
            return MilestoneViewModel(
                getMilestonesUseCase = GetMilestonesUseCase(repository),
                getMilestoneByIdUseCase = GetMilestoneByIdUseCase(repository),
                createMilestoneUseCase = CreateMilestoneUseCase(repository),
                updateMilestoneUseCase = UpdateMilestoneUseCase(repository),
                deleteMilestoneUseCase = DeleteMilestoneUseCase(repository)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
