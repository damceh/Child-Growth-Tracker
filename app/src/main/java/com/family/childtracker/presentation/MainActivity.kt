package com.family.childtracker.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.family.childtracker.data.local.database.DatabaseProvider
import com.family.childtracker.data.local.security.LockStateManager
import com.family.childtracker.data.repository.*
import com.family.childtracker.domain.usecase.GetRandomParentingTipUseCase
import com.family.childtracker.presentation.dashboard.DashboardViewModel
import com.family.childtracker.presentation.dashboard.DashboardViewModelFactory
import com.family.childtracker.presentation.lock.AppLockScreen
import com.family.childtracker.presentation.navigation.MainNavigation
import com.family.childtracker.presentation.profile.ChildProfileViewModel
import com.family.childtracker.presentation.profile.ChildProfileViewModelFactory
import com.family.childtracker.presentation.theme.ChildGrowthTrackerTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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
    
    private val dashboardViewModel: DashboardViewModel by viewModels {
        val database = DatabaseProvider.getDatabase(applicationContext)
        val childProfileRepository = ChildProfileRepositoryImpl(database.childProfileDao())
        val growthRecordRepository = GrowthRecordRepositoryImpl(database.growthRecordDao())
        val milestoneRepository = MilestoneRepositoryImpl(database.milestoneDao())
        val behaviorEntryRepository = BehaviorEntryRepositoryImpl(database.behaviorEntryDao())
        val parentingTipRepository = ParentingTipRepositoryImpl(database.parentingTipDao())
        val getRandomParentingTipUseCase = GetRandomParentingTipUseCase(parentingTipRepository)
        
        DashboardViewModelFactory(
            childProfileRepository,
            growthRecordRepository,
            milestoneRepository,
            behaviorEntryRepository,
            getRandomParentingTipUseCase
        )
    }
    
    private val lockStateManager by lazy { LockStateManager.getInstance(applicationContext) }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check if app should be locked on startup
        lifecycleScope.launch {
            val database = DatabaseProvider.getDatabase(applicationContext)
            val settingsDao = database.appSettingsDao()
            val settings = settingsDao.getSettings().first()
            
            if (settings?.appLockEnabled == true) {
                lockStateManager.lock()
                lockStateManager.setAutoLockTimeout(settings.autoLockTimeoutMinutes)
            }
        }
        
        setContent {
            ChildGrowthTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val databaseProvider = DatabaseProvider.getInstance(applicationContext)
                    val database = DatabaseProvider.getDatabase(applicationContext)
                    val settingsDao = database.appSettingsDao()
                    
                    val settings by settingsDao.getSettings().collectAsState(initial = null)
                    val isLocked by lockStateManager.isLocked.collectAsState()
                    
                    if (isLocked && settings?.appLockEnabled == true) {
                        AppLockScreen(
                            biometricEnabled = settings?.biometricAuthEnabled ?: false,
                            pinCode = settings?.pinCode,
                            onUnlock = {
                                lockStateManager.unlock()
                            }
                        )
                    } else {
                        MainNavigation(
                            profileViewModel = profileViewModel,
                            dashboardViewModel = dashboardViewModel,
                            databaseProvider = databaseProvider
                        )
                    }
                }
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Check for auto-lock when app resumes
        lifecycleScope.launch {
            val database = DatabaseProvider.getDatabase(applicationContext)
            val settingsDao = database.appSettingsDao()
            val settings = settingsDao.getSettings().first()
            
            if (settings?.appLockEnabled == true) {
                lockStateManager.checkAndApplyAutoLock()
            }
        }
    }
    
    override fun onPause() {
        super.onPause()
        // Update last active time when app goes to background
        lockStateManager.updateLastActiveTime()
    }
}
