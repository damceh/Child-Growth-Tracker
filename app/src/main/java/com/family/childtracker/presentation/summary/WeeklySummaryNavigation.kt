package com.family.childtracker.presentation.summary

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.family.childtracker.data.local.database.DatabaseProvider
import com.family.childtracker.data.local.preferences.SecurePreferences

const val WEEKLY_SUMMARY_ROUTE = "weekly_summary"

fun NavGraphBuilder.weeklySummaryScreen(
    navController: NavHostController,
    selectedChildId: String? = null
) {
    composable(WEEKLY_SUMMARY_ROUTE) {
        WeeklySummaryScreenRoute(
            onNavigateBack = { navController.popBackStack() },
            selectedChildId = selectedChildId
        )
    }
}

@Composable
private fun WeeklySummaryScreenRoute(
    onNavigateBack: () -> Unit,
    selectedChildId: String? = null
) {
    val context = LocalContext.current
    val databaseProvider = DatabaseProvider.getInstance(context)
    val securePreferences = SecurePreferences(context)
    
    val viewModel: WeeklySummaryViewModel = viewModel(
        factory = WeeklySummaryViewModelFactory(databaseProvider, securePreferences)
    )
    
    // Set selected child if provided
    LaunchedEffect(selectedChildId) {
        selectedChildId?.let { viewModel.setSelectedChild(it) }
    }
    
    WeeklySummaryScreen(
        viewModel = viewModel,
        onNavigateBack = onNavigateBack
    )
}
