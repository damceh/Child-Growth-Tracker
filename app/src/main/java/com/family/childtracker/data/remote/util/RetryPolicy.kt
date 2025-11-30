package com.family.childtracker.data.remote.util

import kotlinx.coroutines.delay
import retrofit2.Response
import java.io.IOException

/**
 * Retry policy with exponential backoff for API calls
 */
class RetryPolicy(
    private val maxRetries: Int = 3,
    private val initialDelayMs: Long = 1000L,
    private val maxDelayMs: Long = 10000L,
    private val factor: Double = 2.0
) {
    
    /**
     * Execute an API call with retry logic
     */
    suspend fun <T> executeWithRetry(
        block: suspend () -> Response<T>
    ): Response<T> {
        var currentDelay = initialDelayMs
        var lastException: Exception? = null
        
        repeat(maxRetries) { attempt ->
            try {
                val response = block()
                
                // If successful or non-retryable error, return immediately
                if (response.isSuccessful || !isRetryableError(response.code())) {
                    return response
                }
                
                // If this is the last attempt, return the response
                if (attempt == maxRetries - 1) {
                    return response
                }
                
                // Wait before retrying
                delay(currentDelay)
                currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelayMs)
                
            } catch (e: IOException) {
                // Network error - retry
                lastException = e
                
                if (attempt == maxRetries - 1) {
                    throw e
                }
                
                delay(currentDelay)
                currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelayMs)
            }
        }
        
        // This should not be reached, but throw last exception if it happens
        throw lastException ?: IOException("Max retries exceeded")
    }
    
    /**
     * Determine if an HTTP status code is retryable
     */
    private fun isRetryableError(code: Int): Boolean {
        return when (code) {
            408, // Request Timeout
            429, // Too Many Requests
            500, // Internal Server Error
            502, // Bad Gateway
            503, // Service Unavailable
            504  // Gateway Timeout
            -> true
            else -> false
        }
    }
}
