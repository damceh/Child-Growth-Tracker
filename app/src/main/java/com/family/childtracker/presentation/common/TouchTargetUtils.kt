package com.family.childtracker.presentation.common

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Utilities for ensuring proper touch target sizes
 * Following Material Design guidelines: minimum 48dp touch targets
 */
object TouchTargetUtils {
    
    /**
     * Minimum touch target size as per Material Design guidelines
     */
    val MIN_TOUCH_TARGET_SIZE = 48.dp
    
    /**
     * Recommended touch target size for comfortable interaction
     */
    val RECOMMENDED_TOUCH_TARGET_SIZE = 56.dp
}

/**
 * Modifier to ensure minimum touch target size (48dp)
 */
fun Modifier.minTouchTarget(): Modifier = this.defaultMinSize(
    minWidth = TouchTargetUtils.MIN_TOUCH_TARGET_SIZE,
    minHeight = TouchTargetUtils.MIN_TOUCH_TARGET_SIZE
)

/**
 * Modifier to ensure recommended touch target size (56dp)
 */
fun Modifier.recommendedTouchTarget(): Modifier = this.defaultMinSize(
    minWidth = TouchTargetUtils.RECOMMENDED_TOUCH_TARGET_SIZE,
    minHeight = TouchTargetUtils.RECOMMENDED_TOUCH_TARGET_SIZE
)
