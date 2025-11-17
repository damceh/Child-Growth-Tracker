package com.family.childtracker.presentation.profile

import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.family.childtracker.presentation.growth.GrowthNavigation

const val PROFILE_LIST_ROUTE = "profile_list"
const val PROFILE_ADD_ROUTE = "profile_add"
const val PROFILE_EDIT_ROUTE = "profile_edit/{profileId}"
const val GROWTH_TRACKING_ROUTE = "growth_tracking/{profileId}"

fun NavGraphBuilder.profileNavGraph(
    navController: NavHostController,
    viewModel: ChildProfileViewModel
) {
    composable(PROFILE_LIST_ROUTE) {
        val profiles by viewModel.profiles.collectAsStateWithLifecycle()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        LaunchedEffect(uiState) {
            when (uiState) {
                is ProfileUiState.ProfileCreated,
                is ProfileUiState.ProfileUpdated -> {
                    navController.popBackStack()
                    viewModel.resetUiState()
                }
                else -> {}
            }
        }

        ProfileListScreen(
            profiles = profiles,
            uiState = uiState,
            onProfileClick = { profile ->
                navController.navigate("profile_edit/${profile.id}")
            },
            onAddProfileClick = {
                navController.navigate(PROFILE_ADD_ROUTE)
            },
            onDeleteProfile = { profileId ->
                viewModel.deleteProfile(profileId)
            },
            onViewGrowth = { profile ->
                navController.navigate("growth_tracking/${profile.id}")
            }
        )
    }

    composable(PROFILE_ADD_ROUTE) {
        ProfileFormScreen(
            profile = null,
            onSave = { name, dateOfBirth, gender ->
                viewModel.createProfile(name, dateOfBirth, gender)
            },
            onNavigateBack = {
                navController.popBackStack()
            }
        )
    }

    composable(
        route = PROFILE_EDIT_ROUTE,
        arguments = listOf(navArgument("profileId") { type = NavType.StringType })
    ) { backStackEntry ->
        val profileId = backStackEntry.arguments?.getString("profileId") ?: return@composable
        val profiles by viewModel.profiles.collectAsStateWithLifecycle()
        val profile = profiles.find { it.id == profileId }

        if (profile != null) {
            ProfileFormScreen(
                profile = profile,
                onSave = { name, dateOfBirth, gender ->
                    viewModel.updateProfile(profileId, name, dateOfBirth, gender)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }

    composable(
        route = GROWTH_TRACKING_ROUTE,
        arguments = listOf(navArgument("profileId") { type = NavType.StringType })
    ) { backStackEntry ->
        val profileId = backStackEntry.arguments?.getString("profileId") ?: return@composable
        val profiles by viewModel.profiles.collectAsStateWithLifecycle()
        val profile = profiles.find { it.id == profileId }

        if (profile != null) {
            GrowthNavigation(
                childProfile = profile,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
