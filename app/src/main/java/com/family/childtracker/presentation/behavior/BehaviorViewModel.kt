package com.family.childtracker.presentation.behavior

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.family.childtracker.domain.model.BehaviorEntry
import com.family.childtracker.domain.model.EatingHabits
import com.family.childtracker.domain.model.Mood
import com.family.childtracker.domain.usecase.CreateBehaviorEntryUseCase
import com.family.childtracker.domain.usecase.DeleteBehaviorEntryUseCase
import com.family.childtracker.domain.usecase.GetBehaviorEntriesUseCase
import com.family.childtracker.domain.usecase.UpdateBehaviorEntryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class BehaviorViewModel(
    private val createBehaviorEntryUseCase: CreateBehaviorEntryUseCase,
    private val getBehaviorEntriesUseCase: GetBehaviorEntriesUseCase,
    private val updateBehaviorEntryUseCase: UpdateBehaviorEntryUseCase,
    private val deleteBehaviorEntryUseCase: DeleteBehaviorEntryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<BehaviorUiState>(BehaviorUiState.Loading)
    val uiState: StateFlow<BehaviorUiState> = _uiState.asStateFlow()

    private val _entries = MutableStateFlow<List<BehaviorEntry>>(emptyList())
    val entries: StateFlow<List<BehaviorEntry>> = _entries.asStateFlow()

    fun loadBehaviorEntries(childId: String) {
        viewModelScope.launch {
            _uiState.value = BehaviorUiState.Loading
            try {
                getBehaviorEntriesUseCase(childId).collect { entries ->
                    _entries.value = entries.sortedByDescending { it.entryDate }
                    _uiState.value = BehaviorUiState.Success
                }
            } catch (e: Exception) {
                _uiState.value = BehaviorUiState.Error(e.message ?: "Failed to load behavior entries")
            }
        }
    }

    fun loadBehaviorEntriesByDateRange(
        childId: String,
        startDate: LocalDate,
        endDate: LocalDate
    ) {
        viewModelScope.launch {
            _uiState.value = BehaviorUiState.Loading
            try {
                getBehaviorEntriesUseCase.getByDateRange(childId, startDate, endDate).collect { entries ->
                    _entries.value = entries.sortedByDescending { it.entryDate }
                    _uiState.value = BehaviorUiState.Success
                }
            } catch (e: Exception) {
                _uiState.value = BehaviorUiState.Error(e.message ?: "Failed to load behavior entries")
            }
        }
    }

    fun createBehaviorEntry(
        childId: String,
        entryDate: LocalDate,
        mood: Mood?,
        sleepQuality: Int?,
        eatingHabits: EatingHabits?,
        notes: String?
    ) {
        viewModelScope.launch {
            _uiState.value = BehaviorUiState.Loading
            createBehaviorEntryUseCase(
                childId = childId,
                entryDate = entryDate,
                mood = mood,
                sleepQuality = sleepQuality,
                eatingHabits = eatingHabits,
                notes = notes
            ).fold(
                onSuccess = {
                    _uiState.value = BehaviorUiState.EntryCreated
                },
                onFailure = { error ->
                    _uiState.value = BehaviorUiState.Error(error.message ?: "Failed to create entry")
                }
            )
        }
    }

    fun updateBehaviorEntry(
        entry: BehaviorEntry,
        entryDate: LocalDate?,
        mood: Mood?,
        sleepQuality: Int?,
        eatingHabits: EatingHabits?,
        notes: String?
    ) {
        viewModelScope.launch {
            _uiState.value = BehaviorUiState.Loading
            updateBehaviorEntryUseCase(
                entry = entry,
                entryDate = entryDate,
                mood = mood,
                sleepQuality = sleepQuality,
                eatingHabits = eatingHabits,
                notes = notes
            ).fold(
                onSuccess = {
                    _uiState.value = BehaviorUiState.EntryUpdated
                },
                onFailure = { error ->
                    _uiState.value = BehaviorUiState.Error(error.message ?: "Failed to update entry")
                }
            )
        }
    }

    fun deleteBehaviorEntry(entry: BehaviorEntry) {
        viewModelScope.launch {
            _uiState.value = BehaviorUiState.Loading
            deleteBehaviorEntryUseCase(entry).fold(
                onSuccess = {
                    _uiState.value = BehaviorUiState.EntryDeleted
                },
                onFailure = { error ->
                    _uiState.value = BehaviorUiState.Error(error.message ?: "Failed to delete entry")
                }
            )
        }
    }

    fun resetState() {
        _uiState.value = BehaviorUiState.Success
    }
}

sealed class BehaviorUiState {
    object Loading : BehaviorUiState()
    object Success : BehaviorUiState()
    object EntryCreated : BehaviorUiState()
    object EntryUpdated : BehaviorUiState()
    object EntryDeleted : BehaviorUiState()
    data class Error(val message: String) : BehaviorUiState()
}
