package com.family.childtracker.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.family.childtracker.data.local.database.DatabaseProvider
import com.family.childtracker.presentation.behavior.behaviorNavGraph
import com.family.childtracker.presentation.chat.chatNavigation
import com.family.childtracker.presentation.dashboard.DashboardScreen
import com.family.childtracker.presentation.dashboard.DashboardViewModel
import com.family.childtracker.presentation.growth.GrowthNavigation
import com.family.childtracker.presentation.milestone.milestoneNavigation
import com.family.childtracker.presentation.profile.ChildProfileViewModel
import com.family.childtracker.presentation.profile.profileNavGraph
import com.family.childtracker.presentation.reports.monthlySummaryScreen
import com.family.childtracker.presentation.settings.settingsNavigation
import com.family.childtracker.presentation.summary.weeklySummaryScreen
import com.family.childtracker.presentation.tips.tipsNavigation

// Route constants
const val DASHBOARD_ROUTE = "dashboard"
const val PROFILES_ROUTE = "profiles"
const val GROWTH_ROUTE = "growth"
const val MILESTONES_ROUTE = "milestones"
const val BEHAVIOR_ROUTE = "behavior"
const val TIPS_ROUTE = "tips"

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    object Dashboard : BottomNavItem(DASHBOARD_ROUTE, "Dashboard", Icons.Default.Home)
    object Growth : BottomNavItem(GROWTH_ROUTE, "Growth", Icons.Default.TrendingUp)
    object Milestones : BottomNavItem(MILESTONES_ROUTE, "Milestones", Icons.Default.EmojiEvents)
    object More : BottomNavItem("more", "More", Icons.Default.MoreHoriz)
}

@Composable
fun MainNavigation(
    profileViewModel: ChildProfileViewModel,
    dashboardViewModel: DashboardViewModel,
    databaseProvider: DatabaseProvider
) {
    val navController = rememberNavController()
    var showQuickEntryMenu by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        },
        floatingActionButton = {
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            // Show FAB only on main screens
            if (currentRoute in listOf(DASHBOARD_ROUTE, GROWTH_ROUTE, MILESTONES_ROUTE, BEHAVIOR_ROUTE)) {
                FloatingActionButton(
                    onClick = { showQuickEntryMenu = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Quick Entry"
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            NavHost(
                navController = navController,
                startDestination = DASHBOARD_ROUTE
            ) {
                // Dashboard
                composable(DASHBOARD_ROUTE) {
                    val uiState by dashboardViewModel.uiState.collectAsStateWithLifecycle()
                    
                    DashboardScreen(
                        selectedProfile = uiState.selectedProfile,
                        profiles = uiState.profiles,
                        latestGrowth = uiState.latestGrowth,
                        latestMilestone = uiState.latestMilestone,
                        recentBehaviorSummary = uiState.recentBehaviorSummary,
                        dailyTip = uiState.dailyTip,
                        onProfileSelected = { profile ->
                            dashboardViewModel.selectProfile(profile)
                        },
                        onNavigateToProfiles = {
                            navController.navigate("profile_list")
                        },
                        onNavigateToGrowth = {
                            uiState.selectedProfile?.let {
                                navController.navigate("growth_tracking/${it.id}")
                            }
                        },
                        onNavigateToMilestones = {
                            uiState.selectedProfile?.let {
                                navController.navigate("milestone_timeline/${it.id}")
                            }
                        },
                        onNavigateToBehavior = {
                            uiState.selectedProfile?.let {
                                navController.navigate("behavior_list/${it.id}")
                            }
                        },
                        onNavigateToTips = {
                            navController.navigate("tips_library")
                        },
                        onNavigateToChat = {
                            navController.navigate("chat")
                        },
                        onNavigateToSettings = {
                            navController.navigate("settings")
                        },
                        onNavigateToWeeklySummary = {
                            navController.navigate("weekly_summary")
                        },
                        onNavigateToMonthlySummary = {
                            uiState.selectedProfile?.let { profile ->
                                navController.navigate("monthly_summary/${profile.id}/${profile.name}")
                            }
                        }
                    )
                }

                // Profile management
                profileNavGraph(navController, profileViewModel, databaseProvider)

                // Behavior tracking
                behaviorNavGraph(navController, databaseProvider)

                // Milestone tracking
                milestoneNavigation(navController, databaseProvider)

                // Parenting tips
                tipsNavigation(navController, databaseProvider)
                
                // AI Chat
                chatNavigation(navController, databaseProvider, dashboardViewModel.uiState.value.selectedProfile?.id)
                
                // Settings
                settingsNavigation(navController)
                
                // Weekly Summary
                weeklySummaryScreen(navController, dashboardViewModel.uiState.value.selectedProfile?.id)
                
                // Monthly Summary Reports
                monthlySummaryScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }

        // Quick Entry Menu
        if (showQuickEntryMenu) {
            val uiState by dashboardViewModel.uiState.collectAsStateWithLifecycle()
            
            QuickEntryMenu(
                selectedProfile = uiState.selectedProfile,
                onDismiss = { showQuickEntryMenu = false },
                onGrowthEntry = {
                    showQuickEntryMenu = false
                    uiState.selectedProfile?.let {
                        navController.navigate("growth_tracking/${it.id}")
                    }
                },
                onMilestoneEntry = {
                    showQuickEntryMenu = false
                    uiState.selectedProfile?.let {
                        navController.navigate("milestone_entry/${it.id}")
                    }
                },
                onBehaviorEntry = {
                    showQuickEntryMenu = false
                    uiState.selectedProfile?.let {
                        navController.navigate("behavior_entry/${it.id}")
                    }
                }
            )
        }
    }
}

@Composable
private fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Dashboard,
        BottomNavItem.Growth,
        BottomNavItem.Milestones,
        BottomNavItem.More
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = { Text(item.title) },
                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                onClick = {
                    if (item.route == "more") {
                        // Navigate to profile list for "More" section
                        navController.navigate("profile_list") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    } else {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun QuickEntryMenu(
    selectedProfile: com.family.childtracker.domain.model.ChildProfile?,
    onDismiss: () -> Unit,
    onGrowthEntry: () -> Unit,
    onMilestoneEntry: () -> Unit,
    onBehaviorEntry: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Quick Entry") },
        text = {
            if (selectedProfile == null) {
                Text("Please select a child profile first")
            } else {
                Column {
                    Text(
                        text = "Add entry for ${selectedProfile.name}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    ListItem(
                        headlineContent = { Text("Growth Record") },
                        leadingContent = {
                            Icon(Icons.Default.TrendingUp, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Divider()
                    
                    ListItem(
                        headlineContent = { Text("Milestone") },
                        leadingContent = {
                            Icon(Icons.Default.EmojiEvents, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Divider()
                    
                    ListItem(
                        headlineContent = { Text("Behavior Entry") },
                        leadingContent = {
                            Icon(Icons.Default.Mood, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            if (selectedProfile != null) {
                Column {
                    TextButton(
                        onClick = onGrowthEntry,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Growth Record")
                    }
                    TextButton(
                        onClick = onMilestoneEntry,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Milestone")
                    }
                    TextButton(
                        onClick = onBehaviorEntry,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Behavior Entry")
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
