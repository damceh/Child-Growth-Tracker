package com.family.childtracker

import android.app.Application
import com.family.childtracker.data.local.database.DatabaseProvider
import com.family.childtracker.data.local.preferences.SecurePreferences
import com.family.childtracker.data.repository.AppSettingsRepositoryImpl
import com.family.childtracker.data.repository.ParentingTipRepositoryImpl
import com.family.childtracker.data.worker.WeeklySummaryScheduler
import com.family.childtracker.domain.usecase.InitializeParentingTipsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Application class for Child Growth Tracker.
 * This will be used for dependency injection setup in future tasks.
 */
class ChildTrackerApplication : Application() {
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    override fun onCreate() {
        super.onCreate()
        // Initialize parenting tips if database is empty
        initializeParentingTips()
        // Initialize weekly summary scheduling
        initializeWeeklySummaryScheduling()
        // TODO: Initialize dependency injection container in future tasks
    }
    
    private fun initializeParentingTips() {
        applicationScope.launch {
            val database = DatabaseProvider.getDatabase(applicationContext)
            val repository = ParentingTipRepositoryImpl(database.parentingTipDao())
            val initializeUseCase = InitializeParentingTipsUseCase(repository)
            
            // Check if tips already exist
            val existingTips = repository.getAllTips().first()
            if (existingTips.isEmpty()) {
                initializeUseCase()
            }
        }
    }
    
    private fun initializeWeeklySummaryScheduling() {
        applicationScope.launch {
            val database = DatabaseProvider.getDatabase(applicationContext)
            val securePreferences = SecurePreferences(applicationContext)
            val appSettingsRepository = AppSettingsRepositoryImpl(
                database.appSettingsDao(),
                securePreferences
            )
            
            // Check if auto-generation is enabled
            val settings = appSettingsRepository.getSettingsOnce()
            if (settings?.autoGenerateWeeklySummary == true) {
                WeeklySummaryScheduler.scheduleWeeklySummary(applicationContext)
            }
        }
    }
}
