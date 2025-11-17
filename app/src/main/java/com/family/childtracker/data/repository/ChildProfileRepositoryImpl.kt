package com.family.childtracker.data.repository

import com.family.childtracker.data.local.dao.ChildProfileDao
import com.family.childtracker.data.local.mapper.toDomain
import com.family.childtracker.data.local.mapper.toEntity
import com.family.childtracker.domain.model.ChildProfile
import com.family.childtracker.domain.repository.ChildProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChildProfileRepositoryImpl(
    private val childProfileDao: ChildProfileDao
) : ChildProfileRepository {

    override fun getAllProfiles(): Flow<List<ChildProfile>> {
        return childProfileDao.getAllProfiles().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getProfileById(id: String): Flow<ChildProfile?> {
        return childProfileDao.getProfileByIdFlow(id).map { it?.toDomain() }
    }

    override suspend fun getProfileByIdOnce(id: String): ChildProfile? {
        return childProfileDao.getProfileById(id)?.toDomain()
    }

    override suspend fun insertProfile(profile: ChildProfile) {
        childProfileDao.insertProfile(profile.toEntity())
    }

    override suspend fun updateProfile(profile: ChildProfile) {
        childProfileDao.updateProfile(profile.toEntity())
    }

    override suspend fun deleteProfile(profile: ChildProfile) {
        childProfileDao.deleteProfile(profile.toEntity())
    }

    override suspend fun deleteProfileById(id: String) {
        childProfileDao.deleteProfileById(id)
    }
}
