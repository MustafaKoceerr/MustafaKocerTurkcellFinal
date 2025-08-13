package com.example.mustafakocer.data.network.util

import com.example.mustafakocer.domain.exception.AppException
import com.example.mustafakocer.domain.util.Resource
import com.example.mustafakocer.data.network.error.ErrorMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import kotlin.coroutines.cancellation.CancellationException

/**
 * Retrofit API çağrılarını güvenli bir şekilde yürüten ve bunları bir Resource akışına
 * dönüştüren merkezi bir yardımcı fonksiyon.
 */
fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Flow<Resource<T>> = flow {
    // 1. Anında Yüklenme durumunu yayınla
    emit(Resource.Loading)

    try {
        // 2. API çağrısını gerçekleştir
        val response = apiCall()

        // 3. Yanıtı kontrol et
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                // Başarılı ve gövde doluysa, veriyi yayınla
                emit(Resource.Success(body))
            } else {
                // Başarılı ama gövde boşsa, özel Data hatasını yayınla
                emit(Resource.Error(AppException.Data.EmptyResponse))
            }
        } else {
            // Başarısız bir HTTP yanıtıysa (4xx, 5xx), ErrorMapper'a çevirmesi için gönder
            emit(Resource.Error(ErrorMapper.map(response)))
        }
    } catch (e: Exception) {
        // Çağrı sırasında bir istisna fırlatılırsa (örn: internet yok),
        // bunu da ErrorMapper'a çevirmesi için gönder
        if (e is CancellationException) {
            throw e
        }
        emit(Resource.Error(ErrorMapper.map(e)))
    }
}