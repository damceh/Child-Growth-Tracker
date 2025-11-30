package com.family.childtracker.domain.usecase

import com.family.childtracker.data.remote.util.ApiException
import com.family.childtracker.data.remote.util.ApiResult
import com.family.childtracker.domain.repository.OpenRouterRepository

/**
 * Use case for validating OpenRouter API key
 */
class ValidateApiKeyUseCase(
    private val openRouterRepository: OpenRouterRepository
) {
    
    /**
     * Test the API connection with the current API key
     * 
     * @return Result with success message or specific error
     */
    suspend operator fun invoke(): ValidationResult {
        return when (val result = openRouterRepository.testConnection()) {
            is ApiResult.Success -> {
                ValidationResult.Success("API key is valid and connection successful!")
            }
            is ApiResult.Error -> {
                when (result.exception) {
                    is ApiException.Unauthorized -> {
                        ValidationResult.Error("Invalid API key. Please check your OpenRouter API key.")
                    }
                    is ApiException.NetworkError -> {
                        ValidationResult.Error("No internet connection. Please check your network and try again.")
                    }
                    is ApiException.ServerError -> {
                        ValidationResult.Error("OpenRouter service is temporarily unavailable. Please try again later.")
                    }
                    else -> {
                        ValidationResult.Error(result.exception.message)
                    }
                }
            }
        }
    }
    
    /**
     * Result of API key validation
     */
    sealed class ValidationResult {
        data class Success(val message: String) : ValidationResult()
        data class Error(val message: String) : ValidationResult()
    }
}
