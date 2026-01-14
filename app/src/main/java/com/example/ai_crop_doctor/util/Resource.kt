package com.example.ai_crop_doctor.util

/**
 * A generic wrapper class for API responses to handle success, error, and loading states consistently.
 *
 * @param T The type of data being wrapped
 * @property data The data returned from the API (null for Loading and Error states without data)
 * @property message Optional error or info message
 */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    /**
     * Represents a successful API response with data
     */
    class Success<T>(data: T) : Resource<T>(data)

    /**
     * Represents an error state with optional error message and partial data
     */
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)

    /**
     * Represents a loading state
     */
    class Loading<T> : Resource<T>()
}
