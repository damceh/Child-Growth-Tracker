package com.family.childtracker.data.remote.model

import com.google.gson.annotations.SerializedName

/**
 * Request model for OpenRouter API chat completions
 */
data class OpenRouterRequest(
    @SerializedName("model")
    val model: String,
    
    @SerializedName("messages")
    val messages: List<Message>,
    
    @SerializedName("temperature")
    val temperature: Float = 0.7f,
    
    @SerializedName("max_tokens")
    val maxTokens: Int? = null
)

/**
 * Message in a chat conversation
 */
data class Message(
    @SerializedName("role")
    val role: String, // "user", "assistant", "system"
    
    @SerializedName("content")
    val content: String
)
