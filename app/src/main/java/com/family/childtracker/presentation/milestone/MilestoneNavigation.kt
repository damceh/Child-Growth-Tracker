package com.family.childtracker.presentation.milestone

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.family.childtracker.data.local.database.DatabaseProvider

const val MILESTONE_TIMELINE_ROUTE = "milestone_timeline/{childId}"
const val MILESTONE_ENTRY_ROUTE = "milestone_entry/{childId}?milestoneId={milestoneId}"

fun NavGraphBuilder.milestoneNavigation(
    navController: NavHostController,
    databaseProvider: DatabaseProvider
) {
    composable(
        route = MILESTONE_TIMELINE_ROUTE,
        arguments = listOf(navArgument("childId") { type = NavType.StringType })
    ) { backStackEntry ->
        val childId = backStackEntry.arguments?.getString("childId") ?: return@composable
        val viewModel: MilestoneViewModel = viewModel(
            factory = MilestoneViewModelFactory(databaseProvider)
        )

        LaunchedEffect(childId) {
            viewModel.loadMilestones(childId)
        }

        val uiState by viewModel.uiState.collectAsState()

        MilestoneTimelineScreen(
            milestones = uiState.milestones,
            isLoading = uiState.isLoading,
            selectedCategory = null,
            onCategorySelected = { category ->
                viewModel.filterByCategory(category)
            },
            onAddMilestone = {
                navController.navigate("milestone_entry/$childId")
            },
            onEditMilestone = { milestoneId ->
                navController.navigate("milestone_entry/$childId?milestoneId=$milestoneId")
            },
            onDeleteMilestone = { milestoneId ->
                viewModel.deleteMilestone(milestoneId)
            },
            onNavigateBack = {
                navController.popBackStack()
            }
        )
    }

    composable(
        route = MILESTONE_ENTRY_ROUTE,
        arguments = listOf(
            navArgument("childId") { type = NavType.StringType },
            navArgument("milestoneId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) { backStackEntry ->
        val childId = backStackEntry.arguments?.getString("childId") ?: return@composable
        val milestoneId = backStackEntry.arguments?.getString("milestoneId")
        
        val viewModel: MilestoneViewModel = viewModel(
            factory = MilestoneViewModelFactory(databaseProvider)
        )

        LaunchedEffect(milestoneId) {
            if (milestoneId != null) {
                viewModel.loadMilestoneForEdit(milestoneId)
            } else {
                viewModel.resetForm()
            }
        }

        val uiState by viewModel.uiState.collectAsState()

        LaunchedEffect(uiState.saveSuccess) {
            if (uiState.saveSuccess) {
                viewModel.clearSaveSuccess()
                navController.popBackStack()
            }
        }

        LaunchedEffect(uiState.error) {
            uiState.error?.let {
                // Error is displayed in the UI, just clear it after showing
                viewModel.clearError()
            }
        }

        MilestoneEntryScreen(
            childId = childId,
            milestoneId = milestoneId,
            formState = uiState.formState,
            isSaving = uiState.isSaving,
            onFormStateChange = { formState ->
                viewModel.updateFormState(formState)
            },
            onSave = {
                viewModel.saveMilestone(childId)
            },
            onNavigateBack = {
                navController.popBackStack()
            }
        )
    }
}
