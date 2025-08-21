package com.example.mustafakocer.domain.mapper

import com.example.mustafakocer.domain.exception.AppException

/**
 * A contract for classes that can map a generic [Throwable] into a domain-specific [AppException].
 * This interface lives in the domain layer to allow different layers (data, presentation)
 * to provide their own specific mapping implementations.
 */
interface ErrorMapper {
    fun map(throwable: Throwable): AppException
}