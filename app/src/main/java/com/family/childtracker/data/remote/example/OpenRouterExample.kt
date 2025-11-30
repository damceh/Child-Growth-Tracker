package com.family.childtracker.data.remote.example

import com.family.childtracker.data.remote.api.OpenRouterClient
import com.family.childtracker.data.remote.model.Message
import com.family.childtracker.data.remote.util.ApiException
import com.family.childtracker.data.remote.util.ApiResult
import com.family.childtracker.data.repository.OpenRouterRepositoryImpl

/**
 * Example usage of OpenRouter API client
 * 
 * This file demonstrates how to use the OpenRouter API client in the app.
 * It is not meant to be executed directly, but serves as documentation.
 */
object OpenRouterExample {
    
    /**
     * Example: Initialize the API client and repository
     */
    fun initializeClient(getApiKey: () -> String?): OpenRouterRepositoryImpl {
        // Create the API service with authentication
        val apiService = OpenRouterClient.create(
            apiKeyProvider = getApiKey,
            enableLogging = true // Set to false in production
        )
        
        // Create the repository
        return OpenRouterRepositoryImpl(apiService)
    }
    
    /**
     * Example: Send a chat message and handle the response
     */
    suspend fun sendChatMessage(
        repository: OpenRouterRepositoryImpl,
        userMessage: String
    ): String? {
        // Prepare messages with system prompt and user message
        val messages = listOf(
            Message(
                role = "system",
                content = "You are a helpful parenting assistant for the Child Growth Tracker app."
            ),
            Message(
                role = "user",
                content = userMessage
            )
        )
        
        // Send the request
        return when (val result = repository.chatCompletion(
            model = "openai/gpt-4-turbo",
            messages = messages,
            temperature = 0.7f,
            maxTokens = 500
        )) {
            is ApiResult.Success -> {
                // Return the AI's response
                result.data
            }
            is ApiResult.Error -> {
                // Handle different error types
                handleError(result.exception)
                null
            }
        }
    }
    
    /**
     * Example: Fetch available models
     */
    suspend fun fetchModels(repository: OpenRouterRepositoryImpl): List<String>? {
        return when (val result = repository.getAvailableModels()) {
            is ApiResult.Success -> {
                // Extract model IDs
                result.data.map { it.id }
            }
            is ApiResult.Error -> {
                handleError(result.exception)
                null
            }
        }
    }
    
    /**
     * Example: Test API connection
     */
    suspend fun testConnection(repository: OpenRouterRepositoryImpl): Boolean {
        return when (val result = repository.testConnection()) {
            is ApiResult.Success -> {
                println("✓ Connection successful!")
                true
            }
            is ApiResult.Error -> {
                handleError(result.exception)
                false
            }
        }
    }
    
    /**
     * Example: Handle different error types
     */
    private fun handleError(exception: ApiException) {
        when (exception) {
            is ApiException.Unauthorized -> {
                println("❌ ${exception.message}")
                println("Action: Navigate to Settings and configure API key")
            }
            is ApiException.RateLimitExceeded -> {
                println("⏱️ ${exception.message}")
                println("Action: Show retry button with delay")
            }
            is ApiException.ServerError -> {
                println("🔧 ${exception.message}")
                println("Action: Show 'Try again later' message")
            }
            is ApiException.NetworkError -> {
                println("📡 ${exception.message}")
                println("Action: Check internet connection")
            }
            is ApiException.Unknown -> {
                println("⚠️ ${exception.message}")
                println("Action: Show generic error message")
            }
        }
    }
    
    /**
     * Example: Generate a weekly summary
     */
    suspend fun generateWeeklySummary(
        repository: OpenRouterRepositoryImpl,
        childName: String,
        childAge: String,
        growthData: String,
        milestones: String,
        behaviors: String
    ): String? {
        val prompt = """
            Generate a warm, encouraging weekly summary for $childName ($childAge).
            
            This week's data:
            
            Growth Records:
            $growthData
            
            New Milestones:
            $milestones
            
            Positive Behaviors:
            $behaviors
            
            Create a summary that:
            1. Highlights key developments
            2. Celebrates achievements
            3. Provides 1-2 actionable parenting tips
            4. Maintains an encouraging, supportive tone
            
            Keep it concise (200-300 words).
        """.trimIndent()
        
        val messages = listOf(
            Message(role = "user", content = prompt)
        )
        
        return when (val result = repository.chatCompletion(
            model = "openai/gpt-4-turbo",
            messages = messages,
            temperature = 0.7f,
            maxTokens = 400
        )) {
            is ApiResult.Success -> result.data
            is ApiResult.Error -> {
                handleError(result.exception)
                null
            }
        }
    }
}
