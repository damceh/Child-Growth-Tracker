package com.family.childtracker.data.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.family.childtracker.R
import com.family.childtracker.data.local.database.DatabaseProvider
import com.family.childtracker.data.local.preferences.SecurePreferences
import com.family.childtracker.data.remote.api.OpenRouterClient
import com.family.childtracker.data.repository.*
import com.family.childtracker.domain.usecase.GenerateWeeklySummaryUseCase
import com.family.childtracker.presentation.MainActivity
import kotlinx.coroutines.flow.first

/**
 * WorkManager worker for generating weekly summaries automatically.
 * Runs every Sunday at 8 PM to generate summaries for all child profiles.
 */
class WeeklySummaryWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    companion object {
        const val WORK_NAME = "weekly_summary_generation"
        const val NOTIFICATION_CHANNEL_ID = "weekly_summary_channel"
        const val NOTIFICATION_CHANNEL_NAME = "Weekly Summaries"
        const val NOTIFICATION_ID = 1001
    }
    
    override suspend fun doWork(): Result {
        try {
            // Initialize dependencies
            val database = DatabaseProvider.getDatabase(applicationContext)
            val securePreferences = SecurePreferences(applicationContext)
            
            val appSettingsRepository = AppSettingsRepositoryImpl(
                database.appSettingsDao(),
                securePreferences
            )
            
            // Check if auto-generation is enabled
            val settings = appSettingsRepository.getSettingsOnce()
            if (settings?.autoGenerateWeeklySummary != true) {
                return Result.success()
            }
            
            // Check if API key is configured
            if (settings.openRouterApiKey.isNullOrBlank()) {
                return Result.success() // Skip silently if no API key
            }
            
            // Initialize repositories
            val childProfileRepository = ChildProfileRepositoryImpl(database.childProfileDao())
            val weeklySummaryRepository = WeeklySummaryRepositoryImpl(database.weeklySummaryDao())
            val growthRecordRepository = GrowthRecordRepositoryImpl(database.growthRecordDao())
            val milestoneRepository = MilestoneRepositoryImpl(database.milestoneDao())
            val behaviorEntryRepository = BehaviorEntryRepositoryImpl(database.behaviorEntryDao())
            
            val openRouterClient = OpenRouterClient.create(securePreferences)
            val openRouterRepository = OpenRouterRepositoryImpl(openRouterClient)
            
            // Initialize use case
            val generateSummaryUseCase = GenerateWeeklySummaryUseCase(
                weeklySummaryRepository = weeklySummaryRepository,
                openRouterRepository = openRouterRepository,
                appSettingsRepository = appSettingsRepository,
                childProfileRepository = childProfileRepository,
                growthRecordRepository = growthRecordRepository,
                milestoneRepository = milestoneRepository,
                behaviorEntryRepository = behaviorEntryRepository
            )
            
            // Get all child profiles
            val profiles = childProfileRepository.getAllProfiles().first()
            
            if (profiles.isEmpty()) {
                return Result.success() // No profiles to generate summaries for
            }
            
            var successCount = 0
            var errorCount = 0
            
            // Generate summary for each child
            profiles.forEach { profile ->
                when (val result = generateSummaryUseCase(profile.id)) {
                    is GenerateWeeklySummaryUseCase.Result.Success -> {
                        successCount++
                    }
                    is GenerateWeeklySummaryUseCase.Result.AlreadyExists -> {
                        // Summary already exists, count as success
                        successCount++
                    }
                    is GenerateWeeklySummaryUseCase.Result.Error -> {
                        errorCount++
                    }
                }
            }
            
            // Show notification if at least one summary was generated
            if (successCount > 0) {
                showNotification(successCount)
            }
            
            return if (errorCount == 0) {
                Result.success()
            } else {
                Result.failure()
            }
            
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure()
        }
    }
    
    private fun showNotification(summaryCount: Int) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for weekly summary generation"
            }
            notificationManager.createNotificationChannel(channel)
        }
        
        // Create intent to open the app
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        // Build notification
        val notification = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Weekly Summary Ready")
            .setContentText(
                if (summaryCount == 1) {
                    "Your child's weekly summary is ready to view"
                } else {
                    "Weekly summaries for $summaryCount children are ready"
                }
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
