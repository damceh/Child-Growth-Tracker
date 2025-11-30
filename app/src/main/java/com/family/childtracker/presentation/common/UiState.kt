package com.family.childtracker.presentation.common

/**
 * Sealed class representing different UI states
 */
sealed class UiState<out T> {
    /**
     * Initial state before any data is loaded
     */
    object Idle : UiState<Nothing>()
    
    /**
     * Loading state while data is being fetched
     */
    object Loading : UiState<Nothing>()
    
    /**
     * Success state with data
     */
    data class Success<T>(val data: T) : UiState<T>()
    
    /**
     * Error state with error message
     */
    data class Error(val message: String, val throwable: Throwable? = null) : UiState<Nothing>()
    
    /**
     * Check if state is loading
     */
    val isLoading: Boolean
        get() = this is Loading
    
    /**
     * Check if state is success
     */
    val isSuccess: Boolean
        get() = this is Success
    
    /**
     * Check if state is error
     */
    val isError: Boolean
        get() = this is Error
    
    /**
     * Get data if success, null otherwise
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }
    
    /**
     * Get error message if error, null otherwise
     */
    fun getErrorOrNull(): String? = when (this) {
        is Error -> message
        else -> null
    }
}

/**
 * Extension function to map success data
 */
fun <T, R> UiState<T>.map(transform: (T) -> R): UiState<R> {
    return when (this) {
        is UiState.Success -> UiState.Success(transform(data))
        is UiState.Error -> UiState.Error(message, throwable)
        is UiState.Loading -> UiState.Loading
        is UiState.Idle -> UiState.Idle
    }
}

/**
 * Extension function to handle different states
 */
inline fun <T> UiState<T>.onSuccess(action: (T) -> Unit): UiState<T> {
    if (this is UiState.Success) {
        action(data)
    }
    return this
}

inline fun <T> UiState<T>.onError(action: (String) -> Unit): UiState<T> {
    if (this is UiState.Error) {
        action(message)
    }
    return this
}

inline fun <T> UiState<T>.onLoading(action: () -> Unit): UiState<T> {
    if (this is UiState.Loading) {
        action()
    }
    return this
}
