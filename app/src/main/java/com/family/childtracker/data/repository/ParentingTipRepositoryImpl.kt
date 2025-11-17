package com.family.childtracker.data.repository

import com.family.childtracker.data.local.dao.ParentingTipDao
import com.family.childtracker.data.local.mapper.toDomain
import com.family.childtracker.data.local.mapper.toEntity
import com.family.childtracker.domain.model.AgeRange
import com.family.childtracker.domain.model.ParentingTip
import com.family.childtracker.domain.model.TipCategory
import com.family.childtracker.domain.repository.ParentingTipRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ParentingTipRepositoryImpl(
    private val parentingTipDao: ParentingTipDao
) : ParentingTipRepository {

    override fun getAllTips(): Flow<List<ParentingTip>> {
        return parentingTipDao.getAllTips().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTipsByCategory(category: TipCategory): Flow<List<ParentingTip>> {
        return parentingTipDao.getTipsByCategory(category.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTipsByAgeRange(ageRange: AgeRange): Flow<List<ParentingTip>> {
        return parentingTipDao.getTipsByAgeRange(ageRange.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getTipById(id: String): ParentingTip? {
        return parentingTipDao.getTipById(id)?.toDomain()
    }

    override suspend fun getRandomTip(): ParentingTip? {
        return parentingTipDao.getRandomTip()?.toDomain()
    }

    override suspend fun insertTip(tip: ParentingTip) {
        parentingTipDao.insertTip(tip.toEntity())
    }

    override suspend fun insertTips(tips: List<ParentingTip>) {
        parentingTipDao.insertTips(tips.map { it.toEntity() })
    }

    override suspend fun updateTip(tip: ParentingTip) {
        parentingTipDao.updateTip(tip.toEntity())
    }

    override suspend fun deleteTip(tip: ParentingTip) {
        parentingTipDao.deleteTip(tip.toEntity())
    }

    override suspend fun deleteTipById(id: String) {
        parentingTipDao.deleteTipById(id)
    }
}
