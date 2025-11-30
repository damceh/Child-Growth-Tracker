package com.family.childtracker.data.remote.util

/**
 * Sealed class representing the result of an API call
 */
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val exception: ApiException) : ApiResult<Nothing>()
}

/**
 * Custom exception for API errors
 */
sealed class ApiException(message: String) : Exception(message) {
    
    /**
     * Invalid or missing API key (401)
     */
    data class Unauthorized(override val message: String = "Invalid API key. Please check your OpenRouter API key in Settings.") : 
        ApiException(message)
    
    /**
     * Rate limit exceeded (429)
     */
    data class RateLimitExceeded(override val message: String = "Rate limit exceeded. Please try again in a few moments.") : 
        ApiException(message)
    
    /**
     * Server error (500+)
     */
    data class ServerError(override val message: String = "OpenRouter service is temporarily unavailable. Please try again later.") : 
        ApiException(message)
    
    /**
     * Network connectivity error
     */
    data class NetworkError(override val message: String = "No internet connection. Please check your network and try again.") : 
        ApiException(message)
    
    /**
     * Unknown or unexpected error
     */
    data class Unknown(override val message: String = "An unexpected error occurred. Please try again.") : 
        ApiException(message)
}
