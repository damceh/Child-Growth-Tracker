package com.family.childtracker.data.local.dao

import androidx.room.*
import com.family.childtracker.data.local.entity.ChildProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChildProfileDao {
    @Query("SELECT * FROM child_profiles ORDER BY createdAt DESC")
    fun getAllProfiles(): Flow<List<ChildProfileEntity>>
    
    @Query("SELECT * FROM child_profiles ORDER BY createdAt DESC")
    suspend fun getAllProfilesList(): List<ChildProfileEntity>

    @Query("SELECT * FROM child_profiles WHERE id = :id")
    suspend fun getProfileById(id: String): ChildProfileEntity?

    @Query("SELECT * FROM child_profiles WHERE id = :id")
    fun getProfileByIdFlow(id: String): Flow<ChildProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ChildProfileEntity)

    @Update
    suspend fun updateProfile(profile: ChildProfileEntity)

    @Delete
    suspend fun deleteProfile(profile: ChildProfileEntity)

    @Query("DELETE FROM child_profiles WHERE id = :id")
    suspend fun deleteProfileById(id: String)
}
