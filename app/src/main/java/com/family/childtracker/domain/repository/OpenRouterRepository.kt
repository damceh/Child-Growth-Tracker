package com.family.childtracker.domain.repository

import com.family.childtracker.data.remote.model.Message
import com.family.childtracker.data.remote.model.OpenRouterModel
import com.family.childtracker.data.remote.util.ApiResult

/**
 * Repository interface for OpenRouter API interactions
 */
interface OpenRouterRepository {
    
    /**
     * Send a chat completion request
     * 
     * @param model The AI model to use
     * @param messages The conversation messages
     * @param temperature Sampling temperature (0.0 to 1.0)
     * @param maxTokens Maximum tokens in response (optional)
     * @return API result with the assistant's response message
     */
    suspend fun chatCompletion(
        model: String,
        messages: List<Message>,
        temperature: Float = 0.7f,
        maxTokens: Int? = null
    ): ApiResult<String>
    
    /**
     * Fetch available AI models from OpenRouter
     * 
     * @return API result with list of available models
     */
    suspend fun getAvailableModels(): ApiResult<List<OpenRouterModel>>
    
    /**
     * Test the API connection with the current API key
     * 
     * @return API result indicating success or failure
     */
    suspend fun testConnection(): ApiResult<Boolean>
}
