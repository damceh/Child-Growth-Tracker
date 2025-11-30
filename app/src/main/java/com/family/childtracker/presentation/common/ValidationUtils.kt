package com.family.childtracker.presentation.common

/**
 * Validation utilities for form inputs
 */
object ValidationUtils {
    
    /**
     * Validates that a string is not empty or blank
     */
    fun validateRequired(value: String, fieldName: String): String? {
        return if (value.isBlank()) {
            "$fieldName is required"
        } else {
            null
        }
    }
    
    /**
     * Validates a numeric value is within a range
     */
    fun validateNumberRange(
        value: String,
        fieldName: String,
        min: Float? = null,
        max: Float? = null
    ): String? {
        if (value.isBlank()) return null // Optional field
        
        val number = value.toFloatOrNull()
        if (number == null) {
            return "$fieldName must be a valid number"
        }
        
        if (min != null && number < min) {
            return "$fieldName must be at least $min"
        }
        
        if (max != null && number > max) {
            return "$fieldName must be at most $max"
        }
        
        return null
    }
    
    /**
     * Validates height measurement (in cm)
     */
    fun validateHeight(value: String): String? {
        return validateNumberRange(value, "Height", min = 20f, max = 250f)
    }
    
    /**
     * Validates weight measurement (in kg)
     */
    fun validateWeight(value: String): String? {
        return validateNumberRange(value, "Weight", min = 0.5f, max = 200f)
    }
    
    /**
     * Validates head circumference measurement (in cm)
     */
    fun validateHeadCircumference(value: String): String? {
        return validateNumberRange(value, "Head circumference", min = 20f, max = 100f)
    }
    
    /**
     * Validates that at least one measurement is provided
     */
    fun validateAtLeastOneMeasurement(
        height: String,
        weight: String,
        headCircumference: String
    ): String? {
        return if (height.isBlank() && weight.isBlank() && headCircumference.isBlank()) {
            "Please provide at least one measurement"
        } else {
            null
        }
    }
    
    /**
     * Validates email format (if needed for future features)
     */
    fun validateEmail(email: String): String? {
        if (email.isBlank()) return "Email is required"
        
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return if (!email.matches(emailRegex)) {
            "Please enter a valid email address"
        } else {
            null
        }
    }
    
    /**
     * Validates API key format
     */
    fun validateApiKey(apiKey: String): String? {
        if (apiKey.isBlank()) return "API key is required"
        
        return if (apiKey.length < 20) {
            "API key appears to be invalid (too short)"
        } else {
            null
        }
    }
    
    /**
     * Validates text length
     */
    fun validateMaxLength(value: String, fieldName: String, maxLength: Int): String? {
        return if (value.length > maxLength) {
            "$fieldName must be at most $maxLength characters"
        } else {
            null
        }
    }
    
    /**
     * Validates sleep quality rating (1-5)
     */
    fun validateSleepQuality(value: Int?): String? {
        if (value == null) return null
        return if (value !in 1..5) {
            "Sleep quality must be between 1 and 5"
        } else {
            null
        }
    }
}
