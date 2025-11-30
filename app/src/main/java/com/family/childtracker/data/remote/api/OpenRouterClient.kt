package com.family.childtracker.data.remote.api

import com.family.childtracker.data.remote.interceptor.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Factory for creating OpenRouter API client
 */
object OpenRouterClient {
    
    private const val BASE_URL = "https://openrouter.ai/api/v1/"
    private const val TIMEOUT_SECONDS = 30L
    
    /**
     * Create an instance of OpenRouterApiService
     * 
     * @param apiKeyProvider Function that provides the current API key
     * @param enableLogging Enable HTTP request/response logging for debugging
     */
    fun create(
        apiKeyProvider: () -> String?,
        enableLogging: Boolean = false
    ): OpenRouterApiService {
        
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(apiKeyProvider))
            .apply {
                if (enableLogging) {
                    val loggingInterceptor = HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    }
                    addInterceptor(loggingInterceptor)
                }
            }
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
        
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        return retrofit.create(OpenRouterApiService::class.java)
    }
}
