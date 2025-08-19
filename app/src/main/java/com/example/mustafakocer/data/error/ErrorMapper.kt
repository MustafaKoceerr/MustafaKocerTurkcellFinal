package com.example.mustafakocer.data.error

import com.example.mustafakocer.domain.exception.AppException
import com.example.mustafakocer.domain.mapper.ErrorMapper
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Maps raw [Throwable] instances to domain-specific [AppException] types.
 * This is the central component for standardizing error handling across the application.
 */
@Singleton
class ErrorMapperImpl @Inject constructor() : ErrorMapper {

    override fun map(throwable: Throwable): AppException {
        return when (throwable) {
            is AppException -> throwable
            is HttpException -> mapHttpException(throwable)
            is SocketTimeoutException -> AppException.Network.Timeout(throwable)
            is IOException -> AppException.Network.NoInternet(throwable)
            is SerializationException -> AppException.Data.Parsing(throwable)
            else -> AppException.Unknown(throwable)
        }
    }

    private fun mapHttpException(exception: HttpException): AppException {
        val code = exception.code()
        return when (code) {
            401 -> AppException.Server.Unauthorized(exception)
            404 -> AppException.Server.NotFound(exception)
            503 -> AppException.Server.ServiceUnavailable(exception)
            else -> AppException.Server.Unexpected(code, exception)
        }
    }
}

