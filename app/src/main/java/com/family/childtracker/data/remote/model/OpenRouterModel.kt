package com.family.childtracker.data.remote.model

import com.google.gson.annotations.SerializedName

/**
 * Response model for OpenRouter models list
 */
data class OpenRouterModelsResponse(
    @SerializedName("data")
    val data: List<OpenRouterModel>
)

/**
 * AI model information from OpenRouter
 */
data class OpenRouterModel(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("name")
    val name: String?,
    
    @SerializedName("description")
    val description: String?,
    
    @SerializedName("pricing")
    val pricing: ModelPricing?
)

data class ModelPricing(
    @SerializedName("prompt")
    val prompt: String?,
    
    @SerializedName("completion")
    val completion: String?
)
