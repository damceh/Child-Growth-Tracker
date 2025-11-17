package com.family.childtracker.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.family.childtracker.domain.model.ChildProfile
import com.family.childtracker.domain.model.Gender
import com.family.childtracker.domain.usecase.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class ChildProfileViewModel(
    private val getAllChildProfilesUseCase: GetAllChildProfilesUseCase,
    private val getChildProfileByIdUseCase: GetChildProfileByIdUseCase,
    private val createChildProfileUseCase: CreateChildProfileUseCase,
    private val updateChildProfileUseCase: UpdateChildProfileUseCase,
    private val deleteChildProfileUseCase: DeleteChildProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _profiles = MutableStateFlow<List<ChildProfile>>(emptyList())
    val profiles: StateFlow<List<ChildProfile>> = _profiles.asStateFlow()

    init {
        loadProfiles()
    }

    private fun loadProfiles() {
        viewModelScope.launch {
            getAllChildProfilesUseCase()
                .catch { e ->
                    _uiState.value = ProfileUiState.Error(e.message ?: "Failed to load profiles")
                }
                .collect { profileList ->
                    _profiles.value = profileList
                    _uiState.value = if (profileList.isEmpty()) {
                        ProfileUiState.Empty
                    } else {
                        ProfileUiState.Success
                    }
                }
        }
    }

    fun createProfile(name: String, dateOfBirth: LocalDate, gender: Gender) {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            val result = createChildProfileUseCase(name, dateOfBirth, gender)
            result.fold(
                onSuccess = {
                    _uiState.value = ProfileUiState.ProfileCreated
                },
                onFailure = { e ->
                    _uiState.value = ProfileUiState.Error(e.message ?: "Failed to create profile")
                }
            )
        }
    }

    fun updateProfile(id: String, name: String, dateOfBirth: LocalDate, gender: Gender) {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            val result = updateChildProfileUseCase(id, name, dateOfBirth, gender)
            result.fold(
                onSuccess = {
                    _uiState.value = ProfileUiState.ProfileUpdated
                },
                onFailure = { e ->
                    _uiState.value = ProfileUiState.Error(e.message ?: "Failed to update profile")
                }
            )
        }
    }

    fun deleteProfile(id: String) {
        viewModelScope.launch {
            val result = deleteChildProfileUseCase(id)
            result.fold(
                onSuccess = {
                    // Profile list will update automatically via Flow
                },
                onFailure = { e ->
                    _uiState.value = ProfileUiState.Error(e.message ?: "Failed to delete profile")
                }
            )
        }
    }

    fun resetUiState() {
        _uiState.value = if (_profiles.value.isEmpty()) {
            ProfileUiState.Empty
        } else {
            ProfileUiState.Success
        }
    }
}

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    object Success : ProfileUiState()
    object Empty : ProfileUiState()
    object ProfileCreated : ProfileUiState()
    object ProfileUpdated : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}
