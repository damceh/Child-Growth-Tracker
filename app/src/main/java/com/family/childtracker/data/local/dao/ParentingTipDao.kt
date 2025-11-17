package com.family.childtracker.data.local.dao

import androidx.room.*
import com.family.childtracker.data.local.entity.ParentingTipEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ParentingTipDao {
    @Query("SELECT * FROM parenting_tips ORDER BY createdAt DESC")
    fun getAllTips(): Flow<List<ParentingTipEntity>>

    @Query("SELECT * FROM parenting_tips WHERE category = :category ORDER BY createdAt DESC")
    fun getTipsByCategory(category: String): Flow<List<ParentingTipEntity>>

    @Query("SELECT * FROM parenting_tips WHERE ageRange = :ageRange ORDER BY createdAt DESC")
    fun getTipsByAgeRange(ageRange: String): Flow<List<ParentingTipEntity>>

    @Query("SELECT * FROM parenting_tips WHERE id = :id")
    suspend fun getTipById(id: String): ParentingTipEntity?

    @Query("SELECT * FROM parenting_tips ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomTip(): ParentingTipEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTip(tip: ParentingTipEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTips(tips: List<ParentingTipEntity>)

    @Update
    suspend fun updateTip(tip: ParentingTipEntity)

    @Delete
    suspend fun deleteTip(tip: ParentingTipEntity)

    @Query("DELETE FROM parenting_tips WHERE id = :id")
    suspend fun deleteTipById(id: String)
}
