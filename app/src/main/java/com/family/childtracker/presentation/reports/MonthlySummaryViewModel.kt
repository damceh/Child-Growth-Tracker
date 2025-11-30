package com.family.childtracker.presentation.reports

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.family.childtracker.domain.model.MonthlySummary
import com.family.childtracker.domain.usecase.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.time.YearMonth

class MonthlySummaryViewModel(
    private val context: Context,
    private val generateMonthlySummaryUseCase: GenerateMonthlySummaryUseCase,
    private val exportSummaryAsPdfUseCase: ExportSummaryAsPdfUseCase,
    private val exportSummaryAsImageUseCase: ExportSummaryAsImageUseCase,
    private val shareSummaryUseCase: ShareSummaryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<MonthlySummaryUiState>(MonthlySummaryUiState.Initial)
    val uiState: StateFlow<MonthlySummaryUiState> = _uiState.asStateFlow()

    private var currentSummary: MonthlySummary? = null

    fun generateSummary(childId: String, yearMonth: YearMonth) {
        viewModelScope.launch {
            _uiState.value = MonthlySummaryUiState.Loading

            generateMonthlySummaryUseCase(childId, yearMonth)
                .onSuccess { summary ->
                    currentSummary = summary
                    _uiState.value = MonthlySummaryUiState.Success(summary)
                }
                .onFailure { error ->
                    _uiState.value = MonthlySummaryUiState.Error(
                        error.message ?: "Failed to generate summary"
                    )
                }
        }
    }

    fun exportAsPdf() {
        val summary = currentSummary ?: return

        viewModelScope.launch {
            _uiState.value = MonthlySummaryUiState.Exporting

            exportSummaryAsPdfUseCase(summary)
                .onSuccess { file ->
                    _uiState.value = MonthlySummaryUiState.ExportSuccess(
                        file = file,
                        summary = summary,
                        exportType = ExportType.PDF
                    )
                }
                .onFailure { error ->
                    _uiState.value = MonthlySummaryUiState.Error(
                        error.message ?: "Failed to export PDF"
                    )
                }
        }
    }

    fun exportAsImage() {
        val summary = currentSummary ?: return

        viewModelScope.launch {
            _uiState.value = MonthlySummaryUiState.Exporting

            exportSummaryAsImageUseCase(summary)
                .onSuccess { file ->
                    _uiState.value = MonthlySummaryUiState.ExportSuccess(
                        file = file,
                        summary = summary,
                        exportType = ExportType.IMAGE
                    )
                }
                .onFailure { error ->
                    _uiState.value = MonthlySummaryUiState.Error(
                        error.message ?: "Failed to export image"
                    )
                }
        }
    }

    fun shareSummary(file: File, exportType: ExportType) {
        viewModelScope.launch {
            val mimeType = when (exportType) {
                ExportType.PDF -> "application/pdf"
                ExportType.IMAGE -> "image/png"
            }

            shareSummaryUseCase(file, mimeType)
                .onSuccess { intent ->
                    _uiState.value = MonthlySummaryUiState.ReadyToShare(intent)
                }
                .onFailure { error ->
                    _uiState.value = MonthlySummaryUiState.Error(
                        error.message ?: "Failed to share summary"
                    )
                }
        }
    }

    fun resetToSuccess() {
        currentSummary?.let { summary ->
            _uiState.value = MonthlySummaryUiState.Success(summary)
        }
    }
}

sealed class MonthlySummaryUiState {
    object Initial : MonthlySummaryUiState()
    object Loading : MonthlySummaryUiState()
    object Exporting : MonthlySummaryUiState()
    data class Success(val summary: MonthlySummary) : MonthlySummaryUiState()
    data class ExportSuccess(
        val file: File,
        val summary: MonthlySummary,
        val exportType: ExportType
    ) : MonthlySummaryUiState()
    data class ReadyToShare(val intent: android.content.Intent) : MonthlySummaryUiState()
    data class Error(val message: String) : MonthlySummaryUiState()
}

enum class ExportType {
    PDF, IMAGE
}
