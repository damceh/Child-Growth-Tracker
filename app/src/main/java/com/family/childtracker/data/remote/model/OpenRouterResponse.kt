package com.family.childtracker.data.remote.model

import com.google.gson.annotations.SerializedName

/**
 * Response model for OpenRouter API chat completions
 */
data class OpenRouterResponse(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("model")
    val model: String,
    
    @SerializedName("choices")
    val choices: List<Choice>,
    
    @SerializedName("usage")
    val usage: Usage?
)

data class Choice(
    @SerializedName("message")
    val message: Message,
    
    @SerializedName("finish_reason")
    val finishReason: String?
)

data class Usage(
    @SerializedName("prompt_tokens")
    val promptTokens: Int,
    
    @SerializedName("completion_tokens")
    val completionTokens: Int,
    
    @SerializedName("total_tokens")
    val totalTokens: Int
)
