package com.family.childtracker.presentation.growth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.family.childtracker.domain.model.ChildProfile
import com.family.childtracker.domain.model.GrowthRecord
import com.family.childtracker.domain.usecase.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class GrowthViewModel(
    private val getGrowthRecordsUseCase: GetGrowthRecordsUseCase,
    private val createGrowthRecordUseCase: CreateGrowthRecordUseCase,
    private val updateGrowthRecordUseCase: UpdateGrowthRecordUseCase,
    private val deleteGrowthRecordUseCase: DeleteGrowthRecordUseCase,
    private val calculateGrowthPercentilesUseCase: CalculateGrowthPercentilesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<GrowthUiState>(GrowthUiState.Loading)
    val uiState: StateFlow<GrowthUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(GrowthFormState())
    val formState: StateFlow<GrowthFormState> = _formState.asStateFlow()

    private var currentChildProfile: ChildProfile? = null

    fun loadGrowthRecords(childProfile: ChildProfile) {
        currentChildProfile = childProfile
        viewModelScope.launch {
            getGrowthRecordsUseCase(childProfile.id)
                .catch { e ->
                    _uiState.value = GrowthUiState.Error(e.message ?: "Failed to load growth records")
                }
                .collect { records ->
                    _uiState.value = GrowthUiState.Success(records, childProfile)
                }
        }
    }

    fun onRecordDateChanged(date: LocalDate) {
        _formState.update { it.copy(recordDate = date) }
    }

    fun onHeightChanged(height: String) {
        _formState.update { 
            it.copy(
                height = height,
                heightError = null
            ) 
        }
    }

    fun onWeightChanged(weight: String) {
        _formState.update { 
            it.copy(
                weight = weight,
                weightError = null
            ) 
        }
    }

    fun onHeadCircumferenceChanged(headCirc: String) {
        _formState.update { 
            it.copy(
                headCircumference = headCirc,
                headCircumferenceError = null
            ) 
        }
    }

    fun onNotesChanged(notes: String) {
        _formState.update { it.copy(notes = notes) }
    }

    fun saveGrowthRecord() {
        val childProfile = currentChildProfile ?: return
        val state = _formState.value

        viewModelScope.launch {
            _formState.update { it.copy(isSaving = true, generalError = null) }

            val heightValue = state.height.toFloatOrNull()
            val weightValue = state.weight.toFloatOrNull()
            val headCircValue = state.headCircumference.toFloatOrNull()

            // Validate inputs
            var hasError = false
            if (state.height.isNotEmpty() && heightValue == null) {
                _formState.update { it.copy(heightError = "Invalid height") }
                hasError = true
            }
            if (state.weight.isNotEmpty() && weightValue == null) {
                _formState.update { it.copy(weightError = "Invalid weight") }
                hasError = true
            }
            if (state.headCircumference.isNotEmpty() && headCircValue == null) {
                _formState.update { it.copy(headCircumferenceError = "Invalid head circumference") }
                hasError = true
            }

            if (hasError) {
                _formState.update { it.copy(isSaving = false) }
                return@launch
            }

            val result = if (state.editingRecordId != null) {
                // Update existing record
                val existingRecord = (_uiState.value as? GrowthUiState.Success)
                    ?.records
                    ?.find { it.id == state.editingRecordId }
                
                if (existingRecord != null) {
                    updateGrowthRecordUseCase(
                        record = existingRecord,
                        height = heightValue,
                        weight = weightValue,
                        headCircumference = headCircValue,
                        notes = state.notes
                    )
                } else {
                    Result.failure(IllegalStateException("Record not found"))
                }
            } else {
                // Create new record
                createGrowthRecordUseCase(
                    childId = childProfile.id,
                    recordDate = state.recordDate,
                    height = heightValue,
                    weight = weightValue,
                    headCircumference = headCircValue,
                    notes = state.notes
                )
            }

            result.fold(
                onSuccess = {
                    _formState.update { GrowthFormState() }
                },
                onFailure = { e ->
                    _formState.update { 
                        it.copy(
                            isSaving = false,
                            generalError = e.message ?: "Failed to save record"
                        ) 
                    }
                }
            )
        }
    }

    fun startEditingRecord(record: GrowthRecord) {
        _formState.value = GrowthFormState(
            editingRecordId = record.id,
            recordDate = record.recordDate,
            height = record.height?.toString() ?: "",
            weight = record.weight?.toString() ?: "",
            headCircumference = record.headCircumference?.toString() ?: "",
            notes = record.notes ?: ""
        )
    }

    fun cancelEditing() {
        _formState.value = GrowthFormState()
    }

    fun deleteRecord(recordId: String) {
        viewModelScope.launch {
            deleteGrowthRecordUseCase(recordId).fold(
                onSuccess = { /* Record deleted successfully */ },
                onFailure = { e ->
                    _uiState.value = GrowthUiState.Error(e.message ?: "Failed to delete record")
                }
            )
        }
    }

    fun calculatePercentiles(record: GrowthRecord): CalculateGrowthPercentilesUseCase.PercentileResult? {
        val childProfile = currentChildProfile ?: return null
        return calculateGrowthPercentilesUseCase(
            childProfile = childProfile,
            height = record.height,
            weight = record.weight,
            headCircumference = record.headCircumference,
            measurementDate = record.recordDate
        )
    }
}

sealed class GrowthUiState {
    object Loading : GrowthUiState()
    data class Success(
        val records: List<GrowthRecord>,
        val childProfile: ChildProfile
    ) : GrowthUiState()
    data class Error(val message: String) : GrowthUiState()
}

data class GrowthFormState(
    val editingRecordId: String? = null,
    val recordDate: LocalDate = LocalDate.now(),
    val height: String = "",
    val weight: String = "",
    val headCircumference: String = "",
    val notes: String = "",
    val heightError: String? = null,
    val weightError: String? = null,
    val headCircumferenceError: String? = null,
    val generalError: String? = null,
    val isSaving: Boolean = false
)
