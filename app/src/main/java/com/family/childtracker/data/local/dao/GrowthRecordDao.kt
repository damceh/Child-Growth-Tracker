package com.family.childtracker.data.local.dao

import androidx.room.*
import com.family.childtracker.data.local.entity.GrowthRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GrowthRecordDao {
    @Query("SELECT * FROM growth_records WHERE childId = :childId ORDER BY recordDate DESC")
    fun getRecordsByChildId(childId: String): Flow<List<GrowthRecordEntity>>

    @Query("SELECT * FROM growth_records WHERE id = :id")
    suspend fun getRecordById(id: String): GrowthRecordEntity?

    @Query("SELECT * FROM growth_records WHERE childId = :childId ORDER BY recordDate DESC LIMIT 1")
    suspend fun getLatestRecordByChildId(childId: String): GrowthRecordEntity?

    @Query("SELECT * FROM growth_records WHERE childId = :childId AND recordDate BETWEEN :startDate AND :endDate ORDER BY recordDate ASC")
    suspend fun getRecordsByDateRange(childId: String, startDate: Long, endDate: Long): List<GrowthRecordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: GrowthRecordEntity)

    @Update
    suspend fun updateRecord(record: GrowthRecordEntity)

    @Delete
    suspend fun deleteRecord(record: GrowthRecordEntity)

    @Query("DELETE FROM growth_records WHERE id = :id")
    suspend fun deleteRecordById(id: String)
}
