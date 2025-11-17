package com.family.childtracker.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.family.childtracker.data.local.database.DatabaseProvider
import com.family.childtracker.data.repository.ChildProfileRepositoryImpl
import com.family.childtracker.presentation.profile.ChildProfileViewModel
import com.family.childtracker.presentation.profile.ChildProfileViewModelFactory
import com.family.childtracker.presentation.profile.PROFILE_LIST_ROUTE
import com.family.childtracker.presentation.profile.profileNavGraph
import com.family.childtracker.presentation.theme.ChildGrowthTrackerTheme

/**
 * Main activity for the Child Growth Tracker application.
 * Entry point for the Jetpack Compose UI.
 */
class MainActivity : ComponentActivity() {
    
    private val profileViewModel: ChildProfileViewModel by viewModels {
        val database = DatabaseProvider.getDatabase(applicationContext)
        val repository = ChildProfileRepositoryImpl(database.childProfileDao())
        ChildProfileViewModelFactory(repository)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChildGrowthTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    NavHost(
                        navController = navController,
                        startDestination = PROFILE_LIST_ROUTE
                    ) {
                        profileNavGraph(navController, profileViewModel)
                    }
                }
            }
        }
    }
}
