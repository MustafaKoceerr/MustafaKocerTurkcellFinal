package com.example.mustafakocer.data.network.util

import com.example.mustafakocer.domain.exception.AppException
import com.example.mustafakocer.domain.mapper.ErrorMapper
import com.example.mustafakocer.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import retrofit2.Response
import kotlin.coroutines.cancellation.CancellationException

/**
 * Retrofit API calls safely and wraps them in a Resource Flow.
 * It now requires an ErrorMapper instance to handle error conversions, making it
 * decoupled and testable.
 *
 * @param errorMapper The mapper to convert any Throwable into a domain-specific AppException.
 * @param apiCall The suspend function that performs the network request.
 */
fun <T> safeApiCall(
    errorMapper: ErrorMapper, // DEĞİŞTİ: Artık parametre olarak alıyor.
    apiCall: suspend () -> Response<T>,
): Flow<Resource<T>> = flow {
    emit(Resource.Loading)
    try {
        val response = apiCall()
        if (response.isSuccessful) {
            // Başarılı yanıtı işle
            response.body()?.let { emit(Resource.Success(it)) }
                ?: emit(Resource.Error(AppException.Data.EmptyResponse()))
        } else {
            // Başarısız HTTP yanıtını bir exception'a çevirerek catch bloğunun yakalamasını sağla.
            throw HttpException(response)
        }
    } catch (e: Exception) {
        // Coroutine iptal edildiyse, hatayı yeniden fırlat.
        if (e is CancellationException) throw e
        // Diğer tüm hataları (IOException, HttpException, SerializationException vb.)
        // parametreden gelen errorMapper ile dönüştür.
        emit(Resource.Error(errorMapper.map(e)))
    }
}