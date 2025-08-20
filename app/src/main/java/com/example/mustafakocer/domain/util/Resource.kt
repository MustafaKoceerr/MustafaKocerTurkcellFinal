package com.example.mustafakocer.domain.util

import com.example.mustafakocer.domain.exception.AppException

/**
 * A sealed wrapper class representing the state of a data request.
 *
 * ARCHITECTURAL NOTE: This class is fundamental for communicating the status of
 * asynchronous operations (like API calls) from the data/domain layers to the UI layer.
 * By explicitly handling loading, success, and error states, it makes state management
 * in ViewModels and UI components much cleaner and more predictable.
 */
sealed class Resource<out T> {
    /** Indicates the operation has not yet started or is awaiting an action. */
    data object Idle : Resource<Nothing>()

    /** Indicates the operation has started and the result is being awaited. */
    data object Loading : Resource<Nothing>()

    /** Indicates the operation completed successfully and contains data. */
    data class Success<out T>(val data: T) : Resource<T>()

    /** Indicates that an error occurred during the operation. */
    data class Error(val exception: AppException) : Resource<Nothing>()
}