package com.family.childtracker.presentation.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.family.childtracker.domain.model.WeeklySummary
import com.family.childtracker.domain.repository.WeeklySummaryRepository
import com.family.childtracker.domain.usecase.GenerateWeeklySummaryUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class WeeklySummaryViewModel(
    private val weeklySummaryRepository: WeeklySummaryRepository,
    private val generateWeeklySummaryUseCase: GenerateWeeklySummaryUseCase
) : ViewModel() {
    
    private val _selectedChildId = MutableStateFlow<String?>(null)
    val selectedChildId: StateFlow<String?> = _selectedChildId.asStateFlow()
    
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    private val _generationState = MutableStateFlow<GenerationState>(GenerationState.Idle)
    val generationState: StateFlow<GenerationState> = _generationState.asStateFlow()
    
    init {
        observeSummaries()
    }
    
    fun setSelectedChild(childId: String) {
        _selectedChildId.value = childId
        observeSummaries()
    }
    
    private fun observeSummaries() {
        val childId = _selectedChildId.value
        if (childId == null) {
            _uiState.value = UiState.NoChildSelected
            return
        }
        
        viewModelScope.launch {
            weeklySummaryRepository.getSummariesByChildId(childId)
                .catch { error ->
                    _uiState.value = UiState.Error(error.message ?: "Failed to load summaries")
                }
                .collect { summaries ->
                    _uiState.value = if (summaries.isEmpty()) {
                        UiState.Empty
                    } else {
                        UiState.Success(summaries)
                    }
                }
        }
    }
    
    fun generateSummary(weekStartDate: LocalDate? = null) {
        val childId = _selectedChildId.value
        if (childId == null) {
            _generationState.value = GenerationState.Error("No child selected")
            return
        }
        
        viewModelScope.launch {
            _generationState.value = GenerationState.Generating
            
            when (val result = generateWeeklySummaryUseCase(childId, weekStartDate)) {
                is GenerateWeeklySummaryUseCase.Result.Success -> {
                    _generationState.value = GenerationState.Success(result.summary)
                }
                is GenerateWeeklySummaryUseCase.Result.AlreadyExists -> {
                    _generationState.value = GenerationState.AlreadyExists(result.summary)
                }
                is GenerateWeeklySummaryUseCase.Result.Error -> {
                    _generationState.value = GenerationState.Error(result.message)
                }
            }
        }
    }
    
    fun clearGenerationState() {
        _generationState.value = GenerationState.Idle
    }
    
    fun deleteSummary(summaryId: String) {
        viewModelScope.launch {
            try {
                weeklySummaryRepository.deleteSummaryById(summaryId)
            } catch (e: Exception) {
                _generationState.value = GenerationState.Error("Failed to delete summary: ${e.message}")
            }
        }
    }
    
    sealed class UiState {
        object Loading : UiState()
        object NoChildSelected : UiState()
        object Empty : UiState()
        data class Success(val summaries: List<WeeklySummary>) : UiState()
        data class Error(val message: String) : UiState()
    }
    
    sealed class GenerationState {
        object Idle : GenerationState()
        object Generating : GenerationState()
        data class Success(val summary: WeeklySummary) : GenerationState()
        data class AlreadyExists(val summary: WeeklySummary) : GenerationState()
        data class Error(val message: String) : GenerationState()
    }
}
