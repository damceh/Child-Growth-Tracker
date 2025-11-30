package com.family.childtracker.presentation.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.family.childtracker.data.local.database.DatabaseProvider

const val CHAT_ROUTE = "chat"

fun NavGraphBuilder.chatNavigation(
    navController: NavHostController,
    databaseProvider: DatabaseProvider,
    selectedChildId: String?
) {
    composable(CHAT_ROUTE) {
        val viewModel: ChatViewModel = viewModel(
            factory = ChatViewModelFactory(navController.context)
        )
        
        ChatScreen(
            viewModel = viewModel,
            selectedChildId = selectedChildId,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToSettings = { navController.navigate("settings") }
        )
    }
}
