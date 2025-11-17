package com.family.childtracker.data.local.dao

import androidx.room.*
import com.family.childtracker.data.local.entity.BehaviorEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BehaviorEntryDao {
    @Query("SELECT * FROM behavior_entries WHERE childId = :childId ORDER BY entryDate DESC")
    fun getEntriesByChildId(childId: String): Flow<List<BehaviorEntryEntity>>

    @Query("SELECT * FROM behavior_entries WHERE id = :id")
    suspend fun getEntryById(id: String): BehaviorEntryEntity?

    @Query("SELECT * FROM behavior_entries WHERE childId = :childId AND entryDate = :date")
    suspend fun getEntryByDate(childId: String, date: Long): BehaviorEntryEntity?

    @Query("SELECT * FROM behavior_entries WHERE childId = :childId AND entryDate BETWEEN :startDate AND :endDate ORDER BY entryDate ASC")
    suspend fun getEntriesByDateRange(childId: String, startDate: Long, endDate: Long): List<BehaviorEntryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: BehaviorEntryEntity)

    @Update
    suspend fun updateEntry(entry: BehaviorEntryEntity)

    @Delete
    suspend fun deleteEntry(entry: BehaviorEntryEntity)

    @Query("DELETE FROM behavior_entries WHERE id = :id")
    suspend fun deleteEntryById(id: String)
}
