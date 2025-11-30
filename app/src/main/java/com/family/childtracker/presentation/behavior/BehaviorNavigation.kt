package com.family.childtracker.presentation.behavior

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.family.childtracker.data.local.database.DatabaseProvider
import com.family.childtracker.data.repository.BehaviorEntryRepositoryImpl
import com.family.childtracker.domain.usecase.CreateBehaviorEntryUseCase
import com.family.childtracker.domain.usecase.DeleteBehaviorEntryUseCase
import com.family.childtracker.domain.usecase.GetBehaviorEntriesUseCase
import com.family.childtracker.domain.usecase.UpdateBehaviorEntryUseCase

fun NavGraphBuilder.behaviorNavGraph(
    navController: NavHostController,
    databaseProvider: DatabaseProvider
) {
    composable(
        route = "behavior_list/{childId}",
        arguments = listOf(navArgument("childId") { type = NavType.StringType })
    ) { backStackEntry ->
        val childId = backStackEntry.arguments?.getString("childId") ?: return@composable

        val repository = BehaviorEntryRepositoryImpl(databaseProvider.database.behaviorEntryDao())
        val viewModel: BehaviorViewModel = viewModel(
            factory = BehaviorViewModelFactory(
                createBehaviorEntryUseCase = CreateBehaviorEntryUseCase(repository),
                getBehaviorEntriesUseCase = GetBehaviorEntriesUseCase(repository),
                updateBehaviorEntryUseCase = UpdateBehaviorEntryUseCase(repository),
                deleteBehaviorEntryUseCase = DeleteBehaviorEntryUseCase(repository)
            )
        )

        LaunchedEffect(childId) {
            viewModel.loadBehaviorEntries(childId)
        }

        val entries by viewModel.entries.collectAsState()
        val uiState by viewModel.uiState.collectAsState()

        LaunchedEffect(uiState) {
            when (uiState) {
                is BehaviorUiState.EntryCreated,
                is BehaviorUiState.EntryUpdated,
                is BehaviorUiState.EntryDeleted -> {
                    navController.popBackStack()
                    viewModel.resetState()
                }
                else -> {}
            }
        }

        BehaviorListScreen(
            entries = entries,
            onNavigateBack = { navController.popBackStack() },
            onAddEntry = { navController.navigate("behavior_entry/$childId") },
            onEditEntry = { entry ->
                navController.navigate("behavior_edit/$childId/${entry.id}")
            },
            onDeleteEntry = { entry ->
                viewModel.deleteBehaviorEntry(entry)
            },
            onViewCalendar = { navController.navigate("behavior_calendar/$childId") }
        )
    }

    composable(
        route = "behavior_entry/{childId}",
        arguments = listOf(navArgument("childId") { type = NavType.StringType })
    ) { backStackEntry ->
        val childId = backStackEntry.arguments?.getString("childId") ?: return@composable

        val repository = BehaviorEntryRepositoryImpl(databaseProvider.database.behaviorEntryDao())
        val viewModel: BehaviorViewModel = viewModel(
            factory = BehaviorViewModelFactory(
                createBehaviorEntryUseCase = CreateBehaviorEntryUseCase(repository),
                getBehaviorEntriesUseCase = GetBehaviorEntriesUseCase(repository),
                updateBehaviorEntryUseCase = UpdateBehaviorEntryUseCase(repository),
                deleteBehaviorEntryUseCase = DeleteBehaviorEntryUseCase(repository)
            )
        )

        val uiState by viewModel.uiState.collectAsState()

        LaunchedEffect(uiState) {
            when (uiState) {
                is BehaviorUiState.EntryCreated -> {
                    navController.popBackStack()
                    viewModel.resetState()
                }
                else -> {}
            }
        }

        BehaviorEntryScreen(
            childId = childId,
            existingEntry = null,
            onNavigateBack = { navController.popBackStack() },
            onSave = { date, mood, sleepQuality, eatingHabits, notes ->
                viewModel.createBehaviorEntry(
                    childId = childId,
                    entryDate = date,
                    mood = mood,
                    sleepQuality = sleepQuality,
                    eatingHabits = eatingHabits,
                    notes = notes
                )
            }
        )
    }

    composable(
        route = "behavior_edit/{childId}/{entryId}",
        arguments = listOf(
            navArgument("childId") { type = NavType.StringType },
            navArgument("entryId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val childId = backStackEntry.arguments?.getString("childId") ?: return@composable
        val entryId = backStackEntry.arguments?.getString("entryId") ?: return@composable

        val repository = BehaviorEntryRepositoryImpl(databaseProvider.database.behaviorEntryDao())
        val viewModel: BehaviorViewModel = viewModel(
            factory = BehaviorViewModelFactory(
                createBehaviorEntryUseCase = CreateBehaviorEntryUseCase(repository),
                getBehaviorEntriesUseCase = GetBehaviorEntriesUseCase(repository),
                updateBehaviorEntryUseCase = UpdateBehaviorEntryUseCase(repository),
                deleteBehaviorEntryUseCase = DeleteBehaviorEntryUseCase(repository)
            )
        )

        LaunchedEffect(childId) {
            viewModel.loadBehaviorEntries(childId)
        }

        val entries by viewModel.entries.collectAsState()
        val entry = entries.find { it.id == entryId }
        val uiState by viewModel.uiState.collectAsState()

        LaunchedEffect(uiState) {
            when (uiState) {
                is BehaviorUiState.EntryUpdated -> {
                    navController.popBackStack()
                    viewModel.resetState()
                }
                else -> {}
            }
        }

        if (entry != null) {
            BehaviorEntryScreen(
                childId = childId,
                existingEntry = entry,
                onNavigateBack = { navController.popBackStack() },
                onSave = { date, mood, sleepQuality, eatingHabits, notes ->
                    viewModel.updateBehaviorEntry(
                        entry = entry,
                        entryDate = date,
                        mood = mood,
                        sleepQuality = sleepQuality,
                        eatingHabits = eatingHabits,
                        notes = notes
                    )
                }
            )
        }
    }

    composable(
        route = "behavior_calendar/{childId}",
        arguments = listOf(navArgument("childId") { type = NavType.StringType })
    ) { backStackEntry ->
        val childId = backStackEntry.arguments?.getString("childId") ?: return@composable

        val repository = BehaviorEntryRepositoryImpl(databaseProvider.database.behaviorEntryDao())
        val viewModel: BehaviorViewModel = viewModel(
            factory = BehaviorViewModelFactory(
                createBehaviorEntryUseCase = CreateBehaviorEntryUseCase(repository),
                getBehaviorEntriesUseCase = GetBehaviorEntriesUseCase(repository),
                updateBehaviorEntryUseCase = UpdateBehaviorEntryUseCase(repository),
                deleteBehaviorEntryUseCase = DeleteBehaviorEntryUseCase(repository)
            )
        )

        LaunchedEffect(childId) {
            viewModel.loadBehaviorEntries(childId)
        }

        val entries by viewModel.entries.collectAsState()

        BehaviorCalendarScreen(
            entries = entries,
            onNavigateBack = { navController.popBackStack() },
            onAddEntry = { navController.navigate("behavior_entry/$childId") },
            onEntryClick = { entry ->
                navController.navigate("behavior_edit/$childId/${entry.id}")
            }
        )
    }
}
