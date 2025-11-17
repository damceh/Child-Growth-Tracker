package com.family.childtracker.presentation.growth

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.family.childtracker.data.local.database.DatabaseProvider
import com.family.childtracker.data.repository.GrowthRecordRepositoryImpl
import com.family.childtracker.domain.model.ChildProfile
import com.family.childtracker.domain.usecase.*

@Composable
fun GrowthNavigation(
    childProfile: ChildProfile,
    onNavigateBack: () -> Unit
) {
    val database = DatabaseProvider.getDatabase()
    val repository = remember { GrowthRecordRepositoryImpl(database.growthRecordDao()) }
    
    val viewModel: GrowthViewModel = viewModel(
        factory = GrowthViewModelFactory(
            getGrowthRecordsUseCase = GetGrowthRecordsUseCase(repository),
            createGrowthRecordUseCase = CreateGrowthRecordUseCase(repository),
            updateGrowthRecordUseCase = UpdateGrowthRecordUseCase(repository),
            deleteGrowthRecordUseCase = DeleteGrowthRecordUseCase(repository),
            calculateGrowthPercentilesUseCase = CalculateGrowthPercentilesUseCase()
        )
    )

    LaunchedEffect(childProfile) {
        viewModel.loadGrowthRecords(childProfile)
    }

    val uiState by viewModel.uiState.collectAsState()
    val formState by viewModel.formState.collectAsState()

    var showEntryScreen by remember { mutableStateOf(false) }

    if (showEntryScreen) {
        GrowthEntryScreen(
            formState = formState,
            onRecordDateChanged = viewModel::onRecordDateChanged,
            onHeightChanged = viewModel::onHeightChanged,
            onWeightChanged = viewModel::onWeightChanged,
            onHeadCircumferenceChanged = viewModel::onHeadCircumferenceChanged,
            onNotesChanged = viewModel::onNotesChanged,
            onSave = {
                viewModel.saveGrowthRecord()
                showEntryScreen = false
            },
            onCancel = {
                viewModel.cancelEditing()
                showEntryScreen = false
            }
        )
    } else {
        GrowthListScreen(
            uiState = uiState,
            onAddRecord = {
                viewModel.cancelEditing()
                showEntryScreen = true
            },
            onEditRecord = { record ->
                viewModel.startEditingRecord(record)
                showEntryScreen = true
            },
            onDeleteRecord = viewModel::deleteRecord,
            calculatePercentiles = viewModel::calculatePercentiles,
            onNavigateBack = onNavigateBack
        )
    }
}
