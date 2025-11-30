package com.family.childtracker.presentation.tips

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.family.childtracker.data.local.database.DatabaseProvider
import com.family.childtracker.data.repository.ParentingTipRepositoryImpl
import com.family.childtracker.domain.usecase.*

/**
 * Example composable showing how to integrate the DailyTipCard into a dashboard.
 * This can be used as a reference when implementing task 9 (Dashboard).
 */
@Composable
fun DailyTipExample(
    databaseProvider: DatabaseProvider,
    onNavigateToTipsLibrary: () -> Unit
) {
    val repository = ParentingTipRepositoryImpl(databaseProvider.database.parentingTipDao())
    
    val viewModel: ParentingTipsViewModel = viewModel(
        factory = ParentingTipsViewModelFactory(
            getAllTipsUseCase = GetAllParentingTipsUseCase(repository),
            getTipsByCategoryUseCase = GetTipsByCategoryUseCase(repository),
            getTipsByAgeRangeUseCase = GetTipsByAgeRangeUseCase(repository),
            getRandomTipUseCase = GetRandomParentingTipUseCase(repository),
            initializeTipsUseCase = InitializeParentingTipsUseCase(repository)
        )
    )

    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Dashboard Example",
            style = MaterialTheme.typography.headlineMedium
        )

        // Daily Tip Card - This is what should be included in the dashboard
        DailyTipCard(
            tip = uiState.dailyTip,
            onRefresh = { viewModel.refreshDailyTip() },
            onReadMore = onNavigateToTipsLibrary
        )

        // Other dashboard components would go here...
        Text(
            text = "Other dashboard cards (growth summary, milestones, etc.) would appear here",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
