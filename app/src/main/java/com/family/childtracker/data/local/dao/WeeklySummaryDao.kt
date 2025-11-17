package com.family.childtracker.data.local.dao

import androidx.room.*
import com.family.childtracker.data.local.entity.WeeklySummaryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeeklySummaryDao {
    @Query("SELECT * FROM weekly_summaries WHERE childId = :childId ORDER BY weekStartDate DESC")
    fun getSummariesByChildId(childId: String): Flow<List<WeeklySummaryEntity>>

    @Query("SELECT * FROM weekly_summaries WHERE id = :id")
    suspend fun getSummaryById(id: String): WeeklySummaryEntity?

    @Query("SELECT * FROM weekly_summaries WHERE childId = :childId ORDER BY weekStartDate DESC LIMIT 1")
    suspend fun getLatestSummaryByChildId(childId: String): WeeklySummaryEntity?

    @Query("SELECT * FROM weekly_summaries WHERE childId = :childId AND weekStartDate = :weekStartDate")
    suspend fun getSummaryByWeek(childId: String, weekStartDate: Long): WeeklySummaryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSummary(summary: WeeklySummaryEntity)

    @Update
    suspend fun updateSummary(summary: WeeklySummaryEntity)

    @Delete
    suspend fun deleteSummary(summary: WeeklySummaryEntity)

    @Query("DELETE FROM weekly_summaries WHERE id = :id")
    suspend fun deleteSummaryById(id: String)
}
