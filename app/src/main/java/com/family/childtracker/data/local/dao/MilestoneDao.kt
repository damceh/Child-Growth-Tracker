package com.family.childtracker.data.local.dao

import androidx.room.*
import com.family.childtracker.data.local.entity.MilestoneEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MilestoneDao {
    @Query("SELECT * FROM milestones WHERE childId = :childId ORDER BY achievementDate DESC")
    fun getMilestonesByChildId(childId: String): Flow<List<MilestoneEntity>>

    @Query("SELECT * FROM milestones WHERE childId = :childId AND category = :category ORDER BY achievementDate DESC")
    fun getMilestonesByCategory(childId: String, category: String): Flow<List<MilestoneEntity>>

    @Query("SELECT * FROM milestones WHERE id = :id")
    suspend fun getMilestoneById(id: String): MilestoneEntity?

    @Query("SELECT * FROM milestones WHERE childId = :childId ORDER BY achievementDate DESC LIMIT 1")
    suspend fun getLatestMilestoneByChildId(childId: String): MilestoneEntity?

    @Query("SELECT * FROM milestones WHERE childId = :childId AND achievementDate BETWEEN :startDate AND :endDate ORDER BY achievementDate ASC")
    suspend fun getMilestonesByDateRange(childId: String, startDate: Long, endDate: Long): List<MilestoneEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestone(milestone: MilestoneEntity)

    @Update
    suspend fun updateMilestone(milestone: MilestoneEntity)

    @Delete
    suspend fun deleteMilestone(milestone: MilestoneEntity)

    @Query("DELETE FROM milestones WHERE id = :id")
    suspend fun deleteMilestoneById(id: String)
}
