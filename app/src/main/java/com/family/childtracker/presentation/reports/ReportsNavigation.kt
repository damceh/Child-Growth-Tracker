package com.family.childtracker.presentation.reports

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val MONTHLY_SUMMARY_ROUTE = "monthly_summary/{childId}/{childName}"

fun NavController.navigateToMonthlySummary(childId: String, childName: String) {
    navigate("monthly_summary/$childId/$childName")
}

fun NavGraphBuilder.monthlySummaryScreen(
    onNavigateBack: () -> Unit
) {
    composable(
        route = MONTHLY_SUMMARY_ROUTE,
        arguments = listOf(
            navArgument("childId") { type = NavType.StringType },
            navArgument("childName") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val childId = backStackEntry.arguments?.getString("childId") ?: return@composable
        val childName = backStackEntry.arguments?.getString("childName") ?: return@composable

        MonthlySummaryScreen(
            childId = childId,
            childName = childName,
            onNavigateBack = onNavigateBack
        )
    }
}
