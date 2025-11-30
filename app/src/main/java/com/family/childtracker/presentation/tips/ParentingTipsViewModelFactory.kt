package com.family.childtracker.presentation.tips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.family.childtracker.domain.usecase.*

class ParentingTipsViewModelFactory(
    private val getAllTipsUseCase: GetAllParentingTipsUseCase,
    private val getTipsByCategoryUseCase: GetTipsByCategoryUseCase,
    private val getTipsByAgeRangeUseCase: GetTipsByAgeRangeUseCase,
    private val getRandomTipUseCase: GetRandomParentingTipUseCase,
    private val initializeTipsUseCase: InitializeParentingTipsUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ParentingTipsViewModel::class.java)) {
            return ParentingTipsViewModel(
                getAllTipsUseCase,
                getTipsByCategoryUseCase,
                getTipsByAgeRangeUseCase,
                getRandomTipUseCase,
                initializeTipsUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
