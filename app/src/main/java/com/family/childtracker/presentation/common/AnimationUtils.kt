package com.family.childtracker.presentation.common

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Animation utilities for consistent transitions throughout the app
 */
object AnimationUtils {
    
    /**
     * Standard duration for screen transitions
     */
    const val SCREEN_TRANSITION_DURATION = 300
    
    /**
     * Standard duration for component animations
     */
    const val COMPONENT_ANIMATION_DURATION = 200
    
    /**
     * Standard easing for enter animations
     */
    val ENTER_EASING = FastOutSlowInEasing
    
    /**
     * Standard easing for exit animations
     */
    val EXIT_EASING = FastOutLinearInEasing
    
    /**
     * Fade in animation spec
     */
    fun <T> fadeInSpec(): FiniteAnimationSpec<T> = tween(
        durationMillis = COMPONENT_ANIMATION_DURATION,
        easing = ENTER_EASING
    )
    
    /**
     * Fade out animation spec
     */
    fun <T> fadeOutSpec(): FiniteAnimationSpec<T> = tween(
        durationMillis = COMPONENT_ANIMATION_DURATION,
        easing = EXIT_EASING
    )
    
    /**
     * Slide in from bottom animation
     */
    fun slideInFromBottom(): EnterTransition = slideInVertically(
        initialOffsetY = { it },
        animationSpec = tween(
            durationMillis = SCREEN_TRANSITION_DURATION,
            easing = ENTER_EASING
        )
    ) + fadeIn(animationSpec = fadeInSpec())
    
    /**
     * Slide out to bottom animation
     */
    fun slideOutToBottom(): ExitTransition = slideOutVertically(
        targetOffsetY = { it },
        animationSpec = tween(
            durationMillis = SCREEN_TRANSITION_DURATION,
            easing = EXIT_EASING
        )
    ) + fadeOut(animationSpec = fadeOutSpec())
    
    /**
     * Slide in from right animation
     */
    fun slideInFromRight(): EnterTransition = slideInHorizontally(
        initialOffsetX = { it },
        animationSpec = tween(
            durationMillis = SCREEN_TRANSITION_DURATION,
            easing = ENTER_EASING
        )
    ) + fadeIn(animationSpec = fadeInSpec())
    
    /**
     * Slide out to left animation
     */
    fun slideOutToLeft(): ExitTransition = slideOutHorizontally(
        targetOffsetX = { -it },
        animationSpec = tween(
            durationMillis = SCREEN_TRANSITION_DURATION,
            easing = EXIT_EASING
        )
    ) + fadeOut(animationSpec = fadeOutSpec())
    
    /**
     * Scale in animation
     */
    fun scaleIn(): EnterTransition = scaleIn(
        initialScale = 0.8f,
        animationSpec = tween(
            durationMillis = COMPONENT_ANIMATION_DURATION,
            easing = ENTER_EASING
        )
    ) + fadeIn(animationSpec = fadeInSpec())
    
    /**
     * Scale out animation
     */
    fun scaleOut(): ExitTransition = scaleOut(
        targetScale = 0.8f,
        animationSpec = tween(
            durationMillis = COMPONENT_ANIMATION_DURATION,
            easing = EXIT_EASING
        )
    ) + fadeOut(animationSpec = fadeOutSpec())
}

/**
 * Modifier for shake animation (useful for error states)
 */
@Composable
fun Modifier.shake(enabled: Boolean): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "shake")
    val offsetX = if (enabled) {
        infiniteTransition.animateFloat(
            initialValue = -5f,
            targetValue = 5f,
            animationSpec = infiniteRepeatable(
                animation = tween(50, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "shakeOffset"
        ).value
    } else {
        0f
    }
    
    return this.graphicsLayer {
        translationX = offsetX
    }
}

/**
 * Modifier for pulse animation (useful for highlighting)
 */
@Composable
fun Modifier.pulse(enabled: Boolean): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale = if (enabled) {
        infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulseScale"
        ).value
    } else {
        1f
    }
    
    return this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}
