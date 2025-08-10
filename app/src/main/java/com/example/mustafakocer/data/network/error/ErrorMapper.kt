package com.example.mustafakocer.data.network.error

import com.example.mustafakocer.domain.exception.AppException
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

object ErrorMapper {

    /**
     * Fırlatılan bir istisnayı (Throwable) özel AppException hiyerarşimize eşler.
     * Bu, genellikle bir ağ isteği sırasında bağlantı kopması gibi durumlarda kullanılır.
     */
    fun map(throwable: Throwable): AppException {
        return when (throwable) {
            is AppException -> throwable
            is HttpException -> mapHttpException(throwable)
            is IOException -> AppException.Network.NoInternet(throwable)
            is SerializationException -> AppException.Data.Parse(throwable)
            else -> AppException.Unknown(throwable)
        }
    }

    /**
     * Başarısız bir Retrofit Response nesnesini AppException'a eşler.
     * Bu, sunucuya başarıyla bağlanıldığını ancak sunucunun 4xx veya 5xx gibi bir
     * hata kodu döndürdüğü durumlar için kullanılır. Bu durumda bir 'Throwable' yoktur.
     */
    fun map(response: Response<*>): AppException {
        return mapHttpCode(
            response.code(),
            response.message(),
            null
        ) // 'cause' burada kasıtlı olarak null'dır.
    }

    private fun mapHttpException(exception: HttpException): AppException {
        return mapHttpCode(exception.code(), exception.message(), exception)
    }

    private fun mapHttpCode(code: Int, message: String?, cause: Throwable?): AppException.Api {
        val safeMessage = message?.takeIf { it.isNotBlank() } ?: "HTTP Hatası: $code"
        return when (code) {
            401 -> AppException.Api.Unauthorized(cause)
            404 -> AppException.Api.NotFound(cause)
            in 500..599 -> AppException.Api.ServerError(code, cause)
            else -> AppException.Api.HttpError(code, safeMessage, cause)
        }
    }
}