package com.family.childtracker.presentation.growth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.family.childtracker.domain.usecase.*

class GrowthViewModelFactory(
    private val getGrowthRecordsUseCase: GetGrowthRecordsUseCase,
    private val createGrowthRecordUseCase: CreateGrowthRecordUseCase,
    private val updateGrowthRecordUseCase: UpdateGrowthRecordUseCase,
    private val deleteGrowthRecordUseCase: DeleteGrowthRecordUseCase,
    private val calculateGrowthPercentilesUseCase: CalculateGrowthPercentilesUseCase
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GrowthViewModel::class.java)) {
            return GrowthViewModel(
                getGrowthRecordsUseCase,
                createGrowthRecordUseCase,
                updateGrowthRecordUseCase,
                deleteGrowthRecordUseCase,
                calculateGrowthPercentilesUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
