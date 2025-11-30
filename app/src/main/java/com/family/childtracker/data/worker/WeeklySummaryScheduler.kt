package com.family.childtracker.data.worker

import android.content.Context
import androidx.work.*
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters
import java.util.concurrent.TimeUnit

/**
 * Scheduler for weekly summary generation worker.
 * Schedules the worker to run every Sunday at 8 PM.
 */
object WeeklySummaryScheduler {
    
    private const val SUMMARY_HOUR = 20 // 8 PM
    private const val SUMMARY_MINUTE = 0
    
    /**
     * Schedule the weekly summary worker to run every Sunday at 8 PM.
     */
    fun scheduleWeeklySummary(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED) // Require internet connection
            .build()
        
        // Calculate initial delay until next Sunday at 8 PM
        val initialDelay = calculateInitialDelay()
        
        // Create periodic work request (runs every 7 days)
        val workRequest = PeriodicWorkRequestBuilder<WeeklySummaryWorker>(
            repeatInterval = 7,
            repeatIntervalTimeUnit = TimeUnit.DAYS
        )
            .setConstraints(constraints)
            .setInitialDelay(initialDelay.toMillis(), TimeUnit.MILLISECONDS)
            .addTag(WeeklySummaryWorker.WORK_NAME)
            .build()
        
        // Enqueue the work (replace existing if already scheduled)
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WeeklySummaryWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP, // Keep existing schedule if already running
            workRequest
        )
    }
    
    /**
     * Cancel the scheduled weekly summary worker.
     */
    fun cancelWeeklySummary(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WeeklySummaryWorker.WORK_NAME)
    }
    
    /**
     * Check if the weekly summary worker is currently scheduled.
     */
    suspend fun isScheduled(context: Context): Boolean {
        val workInfos = WorkManager.getInstance(context)
            .getWorkInfosForUniqueWork(WeeklySummaryWorker.WORK_NAME)
            .await()
        
        return workInfos.any { workInfo ->
            workInfo.state == WorkInfo.State.ENQUEUED || workInfo.state == WorkInfo.State.RUNNING
        }
    }
    
    /**
     * Calculate the initial delay until the next Sunday at 8 PM.
     */
    private fun calculateInitialDelay(): Duration {
        val now = LocalDateTime.now()
        
        // Find next Sunday at 8 PM
        var nextSunday = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
            .withHour(SUMMARY_HOUR)
            .withMinute(SUMMARY_MINUTE)
            .withSecond(0)
            .withNano(0)
        
        // If we're past 8 PM on Sunday, schedule for next Sunday
        if (nextSunday.isBefore(now) || nextSunday.isEqual(now)) {
            nextSunday = nextSunday.plusWeeks(1)
        }
        
        return Duration.between(now, nextSunday)
    }
    
    /**
     * Manually trigger the weekly summary generation (for testing or manual generation).
     */
    fun triggerManualGeneration(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        
        val workRequest = OneTimeWorkRequestBuilder<WeeklySummaryWorker>()
            .setConstraints(constraints)
            .addTag("manual_summary_generation")
            .build()
        
        WorkManager.getInstance(context).enqueue(workRequest)
    }
}
