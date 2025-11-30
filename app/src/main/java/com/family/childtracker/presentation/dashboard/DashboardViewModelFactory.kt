package com.family.childtracker.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.family.childtracker.domain.repository.*
import com.family.childtracker.domain.usecase.GetRandomParentingTipUseCase

class DashboardViewModelFactory(
    private val childProfileRepository: ChildProfileRepository,
    private val growthRecordRepository: GrowthRecordRepository,
    private val milestoneRepository: MilestoneRepository,
    private val behaviorEntryRepository: BehaviorEntryRepository,
    private val getRandomParentingTipUseCase: GetRandomParentingTipUseCase
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            return DashboardViewModel(
                childProfileRepository,
                growthRecordRepository,
                milestoneRepository,
                behaviorEntryRepository,
                getRandomParentingTipUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
