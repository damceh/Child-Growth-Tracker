package com.family.childtracker.domain.repository

import com.family.childtracker.domain.model.AgeRange
import com.family.childtracker.domain.model.ParentingTip
import com.family.childtracker.domain.model.TipCategory
import kotlinx.coroutines.flow.Flow

interface ParentingTipRepository {
    fun getAllTips(): Flow<List<ParentingTip>>
    fun getTipsByCategory(category: TipCategory): Flow<List<ParentingTip>>
    fun getTipsByAgeRange(ageRange: AgeRange): Flow<List<ParentingTip>>
    suspend fun getTipById(id: String): ParentingTip?
    suspend fun getRandomTip(): ParentingTip?
    suspend fun insertTip(tip: ParentingTip)
    suspend fun insertTips(tips: List<ParentingTip>)
    suspend fun updateTip(tip: ParentingTip)
    suspend fun deleteTip(tip: ParentingTip)
    suspend fun deleteTipById(id: String)
}
