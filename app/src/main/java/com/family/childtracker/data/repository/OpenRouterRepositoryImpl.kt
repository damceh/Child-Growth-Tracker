package com.family.childtracker.data.repository

import com.family.childtracker.data.remote.api.OpenRouterApiService
import com.family.childtracker.data.remote.model.Message
import com.family.childtracker.data.remote.model.OpenRouterError
import com.family.childtracker.data.remote.model.OpenRouterErrorResponse
import com.family.childtracker.data.remote.model.OpenRouterModel
import com.family.childtracker.data.remote.model.OpenRouterRequest
import com.family.childtracker.data.remote.util.ApiException
import com.family.childtracker.data.remote.util.ApiResult
import com.family.childtracker.data.remote.util.RetryPolicy
import com.family.childtracker.domain.repository.OpenRouterRepository
import com.google.gson.Gson
import retrofit2.Response
import java.io.IOException

/**
 * Implementation of OpenRouterRepository
 */
class OpenRouterRepositoryImpl(
    private val apiService: OpenRouterApiService,
    private val retryPolicy: RetryPolicy = RetryPolicy()
) : OpenRouterRepository {
    
    private val gson = Gson()
    
    override suspend fun chatCompletion(
        model: String,
        messages: List<Message>,
        temperature: Float,
        maxTokens: Int?
    ): ApiResult<String> {
        return try {
            val request = OpenRouterRequest(
                model = model,
                messages = messages,
                temperature = temperature,
                maxTokens = maxTokens
            )
            
            val response = retryPolicy.executeWithRetry {
                apiService.chatCompletion(request)
            }
            
            handleResponse(response) { data ->
                // Extract the assistant's message content from the first choice
                data.choices.firstOrNull()?.message?.content
                    ?: throw IllegalStateException("No response content from API")
            }
            
        } catch (e: IOException) {
            ApiResult.Error(ApiException.NetworkError())
        } catch (e: Exception) {
            ApiResult.Error(ApiException.Unknown(e.message ?: "Unknown error occurred"))
        }
    }
    
    override suspend fun getAvailableModels(): ApiResult<List<OpenRouterModel>> {
        return try {
            val response = retryPolicy.executeWithRetry {
                apiService.getModels()
            }
            
            handleResponse(response) { data ->
                data.data
            }
            
        } catch (e: IOException) {
            ApiResult.Error(ApiException.NetworkError())
        } catch (e: Exception) {
            ApiResult.Error(ApiException.Unknown(e.message ?: "Unknown error occurred"))
        }
    }
    
    override suspend fun testConnection(): ApiResult<Boolean> {
        return try {
            // Test connection by fetching models list
            val response = apiService.getModels()
            
            if (response.isSuccessful) {
                ApiResult.Success(true)
            } else {
                val exception = parseErrorResponse(response)
                ApiResult.Error(exception)
            }
            
        } catch (e: IOException) {
            ApiResult.Error(ApiException.NetworkError())
        } catch (e: Exception) {
            ApiResult.Error(ApiException.Unknown(e.message ?: "Unknown error occurred"))
        }
    }
    
    /**
     * Handle API response and convert to ApiResult
     */
    private fun <T, R> handleResponse(
        response: Response<T>,
        transform: (T) -> R
    ): ApiResult<R> {
        return if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                try {
                    ApiResult.Success(transform(body))
                } catch (e: Exception) {
                    ApiResult.Error(ApiException.Unknown("Failed to parse response: ${e.message}"))
                }
            } else {
                ApiResult.Error(ApiException.Unknown("Empty response from server"))
            }
        } else {
            val exception = parseErrorResponse(response)
            ApiResult.Error(exception)
        }
    }
    
    /**
     * Parse error response and create appropriate ApiException
     */
    private fun <T> parseErrorResponse(response: Response<T>): ApiException {
        return when (response.code()) {
            401 -> {
                // Try to parse error message
                val errorMessage = tryParseErrorMessage(response)
                ApiException.Unauthorized(errorMessage ?: "Invalid API key. Please check your OpenRouter API key in Settings.")
            }
            429 -> {
                val errorMessage = tryParseErrorMessage(response)
                ApiException.RateLimitExceeded(errorMessage ?: "Rate limit exceeded. Please try again in a few moments.")
            }
            in 500..599 -> {
                val errorMessage = tryParseErrorMessage(response)
                ApiException.ServerError(errorMessage ?: "OpenRouter service is temporarily unavailable. Please try again later.")
            }
            else -> {
                val errorMessage = tryParseErrorMessage(response)
                ApiException.Unknown(errorMessage ?: "Request failed with code ${response.code()}")
            }
        }
    }
    
    /**
     * Try to parse error message from response body
     */
    private fun <T> tryParseErrorMessage(response: Response<T>): String? {
        return try {
            val errorBody = response.errorBody()?.string()
            if (errorBody != null) {
                val errorResponse = gson.fromJson(errorBody, OpenRouterErrorResponse::class.java)
                errorResponse.error.message
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
