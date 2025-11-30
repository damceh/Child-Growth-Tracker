package com.family.childtracker.presentation.settings

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

const val SETTINGS_ROUTE = "settings"

fun NavGraphBuilder.settingsNavigation(
    navController: NavHostController
) {
    composable(SETTINGS_ROUTE) {
        val viewModel: SettingsViewModel = viewModel(
            factory = SettingsViewModelFactory(navController.context)
        )
        
        SettingsScreen(
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() }
        )
    }
}
