package com.family.childtracker.domain.repository

import com.family.childtracker.domain.model.ChildProfile
import kotlinx.coroutines.flow.Flow

interface ChildProfileRepository {
    fun getAllProfiles(): Flow<List<ChildProfile>>
    fun getProfileById(id: String): Flow<ChildProfile?>
    suspend fun getProfileByIdOnce(id: String): ChildProfile?
    suspend fun insertProfile(profile: ChildProfile)
    suspend fun updateProfile(profile: ChildProfile)
    suspend fun deleteProfile(profile: ChildProfile)
    suspend fun deleteProfileById(id: String)
}
