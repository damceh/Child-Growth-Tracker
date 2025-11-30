package com.family.childtracker.presentation.milestone

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.family.childtracker.domain.model.Milestone
import com.family.childtracker.domain.model.MilestoneCategory
import com.family.childtracker.domain.usecase.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

class MilestoneViewModel(
    private val getMilestonesUseCase: GetMilestonesUseCase,
    private val getMilestoneByIdUseCase: GetMilestoneByIdUseCase,
    private val createMilestoneUseCase: CreateMilestoneUseCase,
    private val updateMilestoneUseCase: UpdateMilestoneUseCase,
    private val deleteMilestoneUseCase: DeleteMilestoneUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MilestoneUiState())
    val uiState: StateFlow<MilestoneUiState> = _uiState.asStateFlow()

    private val _selectedCategory = MutableStateFlow<MilestoneCategory?>(null)

    fun loadMilestones(childId: String) {
        viewModelScope.launch {
            _selectedCategory.flatMapLatest { category ->
                getMilestonesUseCase(childId, category)
            }.catch { error ->
                _uiState.update { it.copy(error = error.message) }
            }.collect { milestones ->
                _uiState.update { it.copy(milestones = milestones, isLoading = false) }
            }
        }
    }

    fun filterByCategory(category: MilestoneCategory?) {
        _selectedCategory.value = category
    }

    fun loadMilestoneForEdit(milestoneId: String) {
        viewModelScope.launch {
            try {
                val milestone = getMilestoneByIdUseCase(milestoneId)
                if (milestone != null) {
                    _uiState.update {
                        it.copy(
                            editingMilestone = milestone,
                            formState = MilestoneFormState(
                                category = milestone.category,
                                description = milestone.description,
                                achievementDate = milestone.achievementDate,
                                notes = milestone.notes ?: "",
                                photoUri = milestone.photoUri
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun updateFormState(formState: MilestoneFormState) {
        _uiState.update { it.copy(formState = formState) }
    }

    fun saveMilestone(childId: String) {
        val formState = _uiState.value.formState
        val editingMilestone = _uiState.value.editingMilestone

        if (formState.description.isBlank()) {
            _uiState.update { it.copy(error = "Description is required") }
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isSaving = true) }

                if (editingMilestone != null) {
                    // Update existing milestone
                    val updatedMilestone = editingMilestone.copy(
                        category = formState.category,
                        description = formState.description,
                        achievementDate = formState.achievementDate,
                        notes = formState.notes.ifBlank { null },
                        photoUri = formState.photoUri
                    )
                    updateMilestoneUseCase(updatedMilestone)
                } else {
                    // Create new milestone
                    val newMilestone = Milestone(
                        id = UUID.randomUUID().toString(),
                        childId = childId,
                        category = formState.category,
                        description = formState.description,
                        achievementDate = formState.achievementDate,
                        notes = formState.notes.ifBlank { null },
                        photoUri = formState.photoUri,
                        createdAt = Instant.now()
                    )
                    createMilestoneUseCase(newMilestone)
                }

                _uiState.update {
                    it.copy(
                        isSaving = false,
                        saveSuccess = true,
                        formState = MilestoneFormState(),
                        editingMilestone = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        error = e.message
                    )
                }
            }
        }
    }

    fun deleteMilestone(milestoneId: String) {
        viewModelScope.launch {
            try {
                deleteMilestoneUseCase(milestoneId)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun clearSaveSuccess() {
        _uiState.update { it.copy(saveSuccess = false) }
    }

    fun resetForm() {
        _uiState.update {
            it.copy(
                formState = MilestoneFormState(),
                editingMilestone = null
            )
        }
    }
}

data class MilestoneUiState(
    val milestones: List<Milestone> = emptyList(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null,
    val formState: MilestoneFormState = MilestoneFormState(),
    val editingMilestone: Milestone? = null
)

data class MilestoneFormState(
    val category: MilestoneCategory = MilestoneCategory.PHYSICAL,
    val description: String = "",
    val achievementDate: LocalDate = LocalDate.now(),
    val notes: String = "",
    val photoUri: String? = null
)
