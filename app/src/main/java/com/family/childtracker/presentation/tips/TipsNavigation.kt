package com.family.childtracker.presentation.tips

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.family.childtracker.data.local.database.DatabaseProvider
import com.family.childtracker.data.repository.ParentingTipRepositoryImpl
import com.family.childtracker.domain.usecase.*

const val TIPS_LIBRARY_ROUTE = "tips_library"

fun NavGraphBuilder.tipsNavigation(
    navController: NavHostController,
    databaseProvider: DatabaseProvider
) {
    composable(TIPS_LIBRARY_ROUTE) {
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

        TipsLibraryScreen(
            uiState = uiState,
            onSearchQueryChange = viewModel::searchTips,
            onCategorySelected = viewModel::filterByCategory,
            onAgeRangeSelected = viewModel::filterByAgeRange,
            onToggleFavorite = viewModel::toggleFavorite,
            onClearFilters = viewModel::clearFilters,
            onNavigateBack = { navController.navigateUp() }
        )
    }
}
