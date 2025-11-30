package com.family.childtracker.presentation.behavior

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.family.childtracker.domain.usecase.CreateBehaviorEntryUseCase
import com.family.childtracker.domain.usecase.DeleteBehaviorEntryUseCase
import com.family.childtracker.domain.usecase.GetBehaviorEntriesUseCase
import com.family.childtracker.domain.usecase.UpdateBehaviorEntryUseCase

class BehaviorViewModelFactory(
    private val createBehaviorEntryUseCase: CreateBehaviorEntryUseCase,
    private val getBehaviorEntriesUseCase: GetBehaviorEntriesUseCase,
    private val updateBehaviorEntryUseCase: UpdateBehaviorEntryUseCase,
    private val deleteBehaviorEntryUseCase: DeleteBehaviorEntryUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BehaviorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BehaviorViewModel(
                createBehaviorEntryUseCase,
                getBehaviorEntriesUseCase,
                updateBehaviorEntryUseCase,
                deleteBehaviorEntryUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
