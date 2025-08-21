package com.example.mustafakocer.domain.util

/**
 * Transforms the data within a [Resource.Success] while preserving other states
 * ([Error], [Loading], [Idle]).
 *
 * This is a crucial utility for mapping data models between layers (e.g., DTO to Domain)
 * without repetitive `when` blocks.
 *
 * The `inline` keyword improves performance by avoiding the creation of a function object.
 *
 * @param transform The function to apply to the data if the resource is [Resource.Success].
 * @return A new [Resource] instance with the transformed data or the original state.
 */
inline fun <T, R> Resource<T>.mapSuccess(transform: (T) -> R): Resource<R> {
    return when (this) {
        is Resource.Success -> Resource.Success(transform(data))
        is Resource.Error -> this
        is Resource.Loading -> this
        is Resource.Idle -> this
    }
}