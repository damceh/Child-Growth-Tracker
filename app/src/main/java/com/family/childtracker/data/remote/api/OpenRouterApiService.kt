package com.family.childtracker.data.remote.api

import com.family.childtracker.data.remote.model.OpenRouterModelsResponse
import com.family.childtracker.data.remote.model.OpenRouterRequest
import com.family.childtracker.data.remote.model.OpenRouterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Retrofit service interface for OpenRouter API
 */
interface OpenRouterApiService {
    
    /**
     * Send a chat completion request to OpenRouter API
     */
    @POST("chat/completions")
    suspend fun chatCompletion(
        @Body request: OpenRouterRequest
    ): Response<OpenRouterResponse>
    
    /**
     * Fetch available AI models from OpenRouter
     */
    @GET("models")
    suspend fun getModels(): Response<OpenRouterModelsResponse>
}
