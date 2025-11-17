package com.family.childtracker.domain.usecase

import com.family.childtracker.domain.model.ChildProfile
import com.family.childtracker.domain.model.Gender
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.pow

/**
 * Calculates growth percentiles using WHO Child Growth Standards
 * Based on LMS (Lambda-Mu-Sigma) method
 * 
 * Note: This is a simplified implementation. For production use,
 * consider using official WHO tables or a medical-grade library.
 */
class CalculateGrowthPercentilesUseCase {

    data class PercentileResult(
        val heightPercentile: Int?,
        val weightPercentile: Int?,
        val headCircumferencePercentile: Int?
    )

    operator fun invoke(
        childProfile: ChildProfile,
        height: Float?,
        weight: Float?,
        headCircumference: Float?,
        measurementDate: LocalDate = LocalDate.now()
    ): PercentileResult {
        val ageInMonths = ChronoUnit.MONTHS.between(childProfile.dateOfBirth, measurementDate).toInt()
        
        return PercentileResult(
            heightPercentile = height?.let { calculateHeightPercentile(it, ageInMonths, childProfile.gender) },
            weightPercentile = weight?.let { calculateWeightPercentile(it, ageInMonths, childProfile.gender) },
            headCircumferencePercentile = headCircumference?.let { 
                calculateHeadCircumferencePercentile(it, ageInMonths, childProfile.gender) 
            }
        )
    }

    private fun calculateHeightPercentile(height: Float, ageInMonths: Int, gender: Gender): Int {
        // Simplified percentile calculation
        // In production, use WHO LMS tables
        val median = when (gender) {
            Gender.MALE -> 50.0 + (ageInMonths * 1.8)
            Gender.FEMALE -> 49.0 + (ageInMonths * 1.7)
            Gender.OTHER -> 49.5 + (ageInMonths * 1.75)
        }
        
        val sd = 3.5 + (ageInMonths * 0.05)
        return calculatePercentileFromZScore((height - median) / sd)
    }

    private fun calculateWeightPercentile(weight: Float, ageInMonths: Int, gender: Gender): Int {
        // Simplified percentile calculation
        val median = when (gender) {
            Gender.MALE -> 3.5 + (ageInMonths * 0.35)
            Gender.FEMALE -> 3.3 + (ageInMonths * 0.32)
            Gender.OTHER -> 3.4 + (ageInMonths * 0.335)
        }
        
        val sd = 0.5 + (ageInMonths * 0.02)
        return calculatePercentileFromZScore((weight - median) / sd)
    }

    private fun calculateHeadCircumferencePercentile(headCirc: Float, ageInMonths: Int, gender: Gender): Int {
        // Simplified percentile calculation
        val median = when (gender) {
            Gender.MALE -> 34.5 + (ageInMonths * 0.4)
            Gender.FEMALE -> 34.0 + (ageInMonths * 0.38)
            Gender.OTHER -> 34.25 + (ageInMonths * 0.39)
        }
        
        val sd = 1.5
        return calculatePercentileFromZScore((headCirc - median) / sd)
    }

    private fun calculatePercentileFromZScore(zScore: Double): Int {
        // Approximate percentile from z-score using cumulative distribution function
        val percentile = (0.5 * (1.0 + erf(zScore / kotlin.math.sqrt(2.0)))) * 100
        return percentile.toInt().coerceIn(1, 99)
    }

    // Approximation of error function for normal distribution
    private fun erf(x: Double): Double {
        val a1 = 0.254829592
        val a2 = -0.284496736
        val a3 = 1.421413741
        val a4 = -1.453152027
        val a5 = 1.061405429
        val p = 0.3275911

        val sign = if (x < 0) -1 else 1
        val absX = kotlin.math.abs(x)

        val t = 1.0 / (1.0 + p * absX)
        val y = 1.0 - (((((a5 * t + a4) * t) + a3) * t + a2) * t + a1) * t * kotlin.math.exp(-absX * absX)

        return sign * y
    }
}
