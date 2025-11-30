package com.family.childtracker.presentation.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.family.childtracker.data.local.preferences.SecurePreferences
import com.family.childtracker.data.remote.model.OpenRouterModel
import com.family.childtracker.data.worker.WeeklySummaryScheduler
import com.family.childtracker.domain.model.AppSettings
import com.family.childtracker.domain.repository.AppSettingsRepository
import com.family.childtracker.domain.usecase.ExportDataUseCase
import com.family.childtracker.domain.usecase.GetAvailableModelsUseCase
import com.family.childtracker.domain.usecase.GetBackupFilesUseCase
import com.family.childtracker.domain.usecase.ImportDataUseCase
import com.family.childtracker.domain.usecase.ValidateApiKeyUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class SettingsViewModel(
    private val appSettingsRepository: AppSettingsRepository,
    private val securePreferences: SecurePreferences,
    private val validateApiKeyUseCase: ValidateApiKeyUseCase,
    private val getAvailableModelsUseCase: GetAvailableModelsUseCase,
    private val exportDataUseCase: ExportDataUseCase,
    private val importDataUseCase: ImportDataUseCase,
    private val getBackupFilesUseCase: GetBackupFilesUseCase,
    private val context: Context
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    init {
        loadSettings()
        loadAvailableModels()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            val settings = appSettingsRepository.getSettingsOnce() ?: AppSettings(
                id = "default",
                openRouterApiKey = null,
                selectedModel = null,
                autoGenerateWeeklySummary = true,
                biometricAuthEnabled = false,
                appLockEnabled = false,
                pinCode = null,
                autoLockTimeoutMinutes = 5
            )
            
            _uiState.value = _uiState.value.copy(
                apiKey = securePreferences.getApiKey() ?: "",
                maskedApiKey = securePreferences.getMaskedApiKey(),
                selectedModel = settings.selectedModel ?: "",
                autoGenerateWeeklySummary = settings.autoGenerateWeeklySummary,
                biometricAuthEnabled = settings.biometricAuthEnabled,
                appLockEnabled = settings.appLockEnabled,
                pinCode = settings.pinCode ?: "",
                autoLockTimeoutMinutes = settings.autoLockTimeoutMinutes,
                isLoading = false
            )
        }
    }
    
    fun onApiKeyChanged(apiKey: String) {
        _uiState.value = _uiState.value.copy(
            apiKey = apiKey,
            validationMessage = null,
            validationSuccess = null
        )
    }
    
    fun onSelectedModelChanged(model: String) {
        _uiState.value = _uiState.value.copy(selectedModel = model)
    }
    
    fun onAutoGenerateWeeklySummaryChanged(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(autoGenerateWeeklySummary = enabled)
    }
    
    fun onBiometricAuthChanged(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(biometricAuthEnabled = enabled)
    }
    
    fun onAppLockChanged(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(appLockEnabled = enabled)
    }
    
    fun onPinCodeChanged(pinCode: String) {
        _uiState.value = _uiState.value.copy(pinCode = pinCode)
    }
    
    fun onAutoLockTimeoutChanged(minutes: Int) {
        _uiState.value = _uiState.value.copy(autoLockTimeoutMinutes = minutes)
    }
    
    fun saveSettings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            
            try {
                // Validate PIN if app lock is enabled
                if (_uiState.value.appLockEnabled && _uiState.value.pinCode.length < 4) {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        validationMessage = "PIN must be at least 4 digits",
                        validationSuccess = false
                    )
                    return@launch
                }
                
                val settings = AppSettings(
                    id = "default",
                    openRouterApiKey = _uiState.value.apiKey.takeIf { it.isNotBlank() },
                    selectedModel = _uiState.value.selectedModel.takeIf { it.isNotBlank() },
                    autoGenerateWeeklySummary = _uiState.value.autoGenerateWeeklySummary,
                    biometricAuthEnabled = _uiState.value.biometricAuthEnabled,
                    appLockEnabled = _uiState.value.appLockEnabled,
                    pinCode = _uiState.value.pinCode.takeIf { _uiState.value.appLockEnabled && it.isNotBlank() },
                    autoLockTimeoutMinutes = _uiState.value.autoLockTimeoutMinutes
                )
                
                appSettingsRepository.saveSettings(settings)
                
                // Schedule or cancel weekly summary worker based on setting
                if (_uiState.value.autoGenerateWeeklySummary) {
                    WeeklySummaryScheduler.scheduleWeeklySummary(context)
                } else {
                    WeeklySummaryScheduler.cancelWeeklySummary(context)
                }
                
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    maskedApiKey = securePreferences.getMaskedApiKey(),
                    validationMessage = "Settings saved successfully",
                    validationSuccess = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    validationMessage = "Failed to save settings: ${e.message}",
                    validationSuccess = false
                )
            }
        }
    }
    
    fun testConnection() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isTesting = true,
                validationMessage = null,
                validationSuccess = null
            )
            
            // Save API key first if it's been changed
            if (_uiState.value.apiKey.isNotBlank()) {
                securePreferences.saveApiKey(_uiState.value.apiKey)
            }
            
            when (val result = validateApiKeyUseCase()) {
                is ValidateApiKeyUseCase.ValidationResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isTesting = false,
                        validationMessage = result.message,
                        validationSuccess = true
                    )
                }
                is ValidateApiKeyUseCase.ValidationResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isTesting = false,
                        validationMessage = result.message,
                        validationSuccess = false
                    )
                }
            }
        }
    }
    
    fun clearValidationMessage() {
        _uiState.value = _uiState.value.copy(
            validationMessage = null,
            validationSuccess = null
        )
    }
    
    fun loadAvailableModels(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingModels = true)
            
            when (val result = getAvailableModelsUseCase(forceRefresh)) {
                is GetAvailableModelsUseCase.Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        availableModels = result.models,
                        isLoadingModels = false,
                        modelsLoadError = null,
                        modelsAreStale = result.isStale
                    )
                }
                is GetAvailableModelsUseCase.Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoadingModels = false,
                        modelsLoadError = result.message
                    )
                }
            }
        }
    }
    
    fun refreshModels() {
        loadAvailableModels(forceRefresh = true)
    }
    
    fun exportData(encrypt: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isExporting = true,
                backupMessage = null,
                backupSuccess = null
            )
            
            val result = exportDataUseCase(encrypt)
            
            if (result.isSuccess) {
                val file = result.getOrNull()
                _uiState.value = _uiState.value.copy(
                    isExporting = false,
                    backupMessage = "Backup created: ${file?.name}",
                    backupSuccess = true,
                    lastExportedFile = file
                )
                loadBackupFiles()
            } else {
                _uiState.value = _uiState.value.copy(
                    isExporting = false,
                    backupMessage = "Export failed: ${result.exceptionOrNull()?.message}",
                    backupSuccess = false
                )
            }
        }
    }
    
    fun importData(file: File, clearExisting: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isImporting = true,
                backupMessage = null,
                backupSuccess = null
            )
            
            val result = importDataUseCase(file, clearExisting)
            
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isImporting = false,
                    backupMessage = "Data imported successfully from ${file.name}",
                    backupSuccess = true
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isImporting = false,
                    backupMessage = "Import failed: ${result.exceptionOrNull()?.message}",
                    backupSuccess = false
                )
            }
        }
    }
    
    fun loadBackupFiles() {
        viewModelScope.launch {
            val files = getBackupFilesUseCase()
            _uiState.value = _uiState.value.copy(backupFiles = files)
        }
    }
    
    fun clearBackupMessage() {
        _uiState.value = _uiState.value.copy(
            backupMessage = null,
            backupSuccess = null
        )
    }
}

data class SettingsUiState(
    val apiKey: String = "",
    val maskedApiKey: String? = null,
    val selectedModel: String = "",
    val autoGenerateWeeklySummary: Boolean = true,
    val biometricAuthEnabled: Boolean = false,
    val appLockEnabled: Boolean = false,
    val pinCode: String = "",
    val autoLockTimeoutMinutes: Int = 5,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isTesting: Boolean = false,
    val validationMessage: String? = null,
    val validationSuccess: Boolean? = null,
    val availableModels: List<OpenRouterModel> = emptyList(),
    val isLoadingModels: Boolean = false,
    val modelsLoadError: String? = null,
    val modelsAreStale: Boolean = false,
    val isExporting: Boolean = false,
    val isImporting: Boolean = false,
    val backupMessage: String? = null,
    val backupSuccess: Boolean? = null,
    val backupFiles: List<File> = emptyList(),
    val lastExportedFile: File? = null
)
