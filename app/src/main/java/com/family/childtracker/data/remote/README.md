# OpenRouter API Client Implementation

This package contains the implementation of the OpenRouter API client for AI-powered features in the Child Growth Tracker app.

## Architecture

### Package Structure

```
data/remote/
├── api/
│   ├── OpenRouterApiService.kt      # Retrofit service interface
│   └── OpenRouterClient.kt          # Retrofit client factory
├── interceptor/
│   └── AuthInterceptor.kt           # Authentication header injection
├── model/
│   ├── OpenRouterModel.kt           # Model list response models
│   ├── OpenRouterRequest.kt         # Chat completion request models
│   ├── OpenRouterResponse.kt        # Chat completion response models
│   └── OpenRouterError.kt           # Error response models
└── util/
    ├── ApiResult.kt                 # Result wrapper for API calls
    └── RetryPolicy.kt               # Exponential backoff retry logic
```

### Components

#### 1. API Service (`OpenRouterApiService`)
Retrofit interface defining API endpoints:
- `POST /chat/completions` - Send chat messages and get AI responses
- `GET /models` - Fetch available AI models

#### 2. Authentication (`AuthInterceptor`)
OkHttp interceptor that adds required headers to all requests:
- `Authorization: Bearer {apiKey}` - API authentication
- `HTTP-Referer: com.family.childtracker` - App identification
- `X-Title: Child Growth Tracker` - App name

#### 3. Error Handling (`ApiResult` & `ApiException`)
Sealed classes for type-safe error handling:
- `ApiException.Unauthorized` (401) - Invalid API key
- `ApiException.RateLimitExceeded` (429) - Rate limit hit
- `ApiException.ServerError` (500+) - OpenRouter service issues
- `ApiException.NetworkError` - No internet connection
- `ApiException.Unknown` - Unexpected errors

#### 4. Retry Logic (`RetryPolicy`)
Implements exponential backoff for transient failures:
- Default: 3 retries with 1s initial delay
- Exponential backoff: delay doubles each retry (1s, 2s, 4s)
- Max delay: 10 seconds
- Retries on: 408, 429, 500, 502, 503, 504, network errors
- No retry on: 401, 400, 404 (client errors)

#### 5. Repository (`OpenRouterRepository`)
Domain layer interface with three main operations:
- `chatCompletion()` - Send messages and get AI response
- `getAvailableModels()` - Fetch model list
- `testConnection()` - Validate API key

#### 6. Repository Implementation (`OpenRouterRepositoryImpl`)
Concrete implementation that:
- Uses RetryPolicy for resilient API calls
- Parses responses and errors
- Converts to domain-friendly ApiResult
- Handles all error scenarios gracefully

## Usage

### Creating the API Client

```kotlin
// In your dependency injection setup or factory
val apiKeyProvider = { 
    // Get API key from encrypted SharedPreferences
    appSettingsRepository.getApiKey()
}

val apiService = OpenRouterClient.create(
    apiKeyProvider = apiKeyProvider,
    enableLogging = BuildConfig.DEBUG
)

val repository = OpenRouterRepositoryImpl(
    apiService = apiService,
    retryPolicy = RetryPolicy()
)
```

### Sending a Chat Message

```kotlin
val messages = listOf(
    Message(role = "system", content = "You are a helpful parenting assistant."),
    Message(role = "user", content = "How can I help my toddler sleep better?")
)

when (val result = repository.chatCompletion(
    model = "openai/gpt-4-turbo",
    messages = messages,
    temperature = 0.7f,
    maxTokens = 500
)) {
    is ApiResult.Success -> {
        val response = result.data
        // Display response to user
    }
    is ApiResult.Error -> {
        when (result.exception) {
            is ApiException.Unauthorized -> {
                // Prompt user to configure API key
            }
            is ApiException.NetworkError -> {
                // Show "check internet connection" message
            }
            is ApiException.RateLimitExceeded -> {
                // Show "try again later" message
            }
            else -> {
                // Show generic error message
            }
        }
    }
}
```

### Fetching Available Models

```kotlin
when (val result = repository.getAvailableModels()) {
    is ApiResult.Success -> {
        val models = result.data
        // Display models in settings UI
    }
    is ApiResult.Error -> {
        // Handle error
    }
}
```

### Testing API Connection

```kotlin
when (val result = repository.testConnection()) {
    is ApiResult.Success -> {
        // Show "Connection successful" message
    }
    is ApiResult.Error -> {
        // Show specific error message
        showError(result.exception.message)
    }
}
```

## Error Handling Best Practices

1. **Always handle both Success and Error cases** when calling repository methods
2. **Provide user-friendly error messages** based on exception type
3. **Prompt for API key configuration** when Unauthorized error occurs
4. **Show retry options** for NetworkError and RateLimitExceeded
5. **Log technical details** for debugging while showing simple messages to users

## Security Considerations

1. **API Key Storage**: The API key should be stored using `EncryptedSharedPreferences`
2. **No Logging in Production**: Disable HTTP logging in release builds
3. **API Key Provider**: Use a lambda to fetch the key dynamically, never hardcode it
4. **Header Masking**: The logging interceptor automatically masks sensitive headers

## Testing

The implementation is designed to be testable:
- Mock `OpenRouterApiService` for unit tests
- Use in-memory or fake implementations for integration tests
- Test retry logic with simulated failures
- Verify error handling for all HTTP status codes

## Requirements Satisfied

This implementation satisfies the following requirements:
- **9.2**: API communication with OpenRouter using user's API key
- **9.6**: Error handling for API responses (401, 429, 500, network errors)
- **11.6**: API key validation and connection testing

## Next Steps

To complete the AI integration:
1. Implement API key configuration UI (Task 11)
2. Implement model selection UI (Task 12)
3. Build chat interface (Task 13)
4. Implement weekly summary generation (Task 14)
5. Set up background worker for auto-summaries (Task 15)
