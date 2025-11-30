package com.family.childtracker.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor to add authentication headers to OpenRouter API requests
 */
class AuthInterceptor(
    private val apiKeyProvider: () -> String?
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        val apiKey = apiKeyProvider()
        
        // If no API key is available, proceed without auth header
        if (apiKey.isNullOrBlank()) {
            return chain.proceed(originalRequest)
        }
        
        // Add authentication and identification headers
        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $apiKey")
            .header("HTTP-Referer", "com.family.childtracker")
            .header("X-Title", "Child Growth Tracker")
            .build()
        
        return chain.proceed(authenticatedRequest)
    }
}
