package com.family.childtracker.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Utilities for responsive design based on screen size
 */
object ScreenSizeUtils {
    
    /**
     * Screen size categories
     */
    enum class ScreenSize {
        SMALL,      // < 360dp width
        MEDIUM,     // 360-600dp width
        LARGE,      // 600-840dp width
        EXTRA_LARGE // > 840dp width
    }
    
    /**
     * Get current screen size category
     */
    @Composable
    fun getScreenSize(): ScreenSize {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp
        
        return when {
            screenWidth < 360.dp -> ScreenSize.SMALL
            screenWidth < 600.dp -> ScreenSize.MEDIUM
            screenWidth < 840.dp -> ScreenSize.LARGE
            else -> ScreenSize.EXTRA_LARGE
        }
    }
    
    /**
     * Get screen width in dp
     */
    @Composable
    fun getScreenWidth(): Dp {
        val configuration = LocalConfiguration.current
        return configuration.screenWidthDp.dp
    }
    
    /**
     * Get screen height in dp
     */
    @Composable
    fun getScreenHeight(): Dp {
        val configuration = LocalConfiguration.current
        return configuration.screenHeightDp.dp
    }
    
    /**
     * Check if screen is in landscape orientation
     */
    @Composable
    fun isLandscape(): Boolean {
        val configuration = LocalConfiguration.current
        return configuration.screenWidthDp > configuration.screenHeightDp
    }
    
    /**
     * Get adaptive padding based on screen size
     */
    @Composable
    fun getAdaptivePadding(): Dp {
        return when (getScreenSize()) {
            ScreenSize.SMALL -> 12.dp
            ScreenSize.MEDIUM -> 16.dp
            ScreenSize.LARGE -> 24.dp
            ScreenSize.EXTRA_LARGE -> 32.dp
        }
    }
    
    /**
     * Get adaptive column count for grids
     */
    @Composable
    fun getAdaptiveColumnCount(): Int {
        return when (getScreenSize()) {
            ScreenSize.SMALL -> 1
            ScreenSize.MEDIUM -> 2
            ScreenSize.LARGE -> 3
            ScreenSize.EXTRA_LARGE -> 4
        }
    }
}
