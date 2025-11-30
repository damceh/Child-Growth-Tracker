package com.family.childtracker.data.remote.model

import com.google.gson.annotations.SerializedName

/**
 * Error response from OpenRouter API
 */
data class OpenRouterErrorResponse(
    @SerializedName("error")
    val error: OpenRouterError
)

data class OpenRouterError(
    @SerializedName("message")
    val message: String,
    
    @SerializedName("type")
    val type: String?,
    
    @SerializedName("code")
    val code: String?
)
