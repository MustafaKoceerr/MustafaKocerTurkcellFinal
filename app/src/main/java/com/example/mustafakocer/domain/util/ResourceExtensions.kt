package com.example.mustafakocer.domain.util

/**
 * Resource<T> nesnesini, içindeki veri türünü değiştirmeden Resource<R>'ye dönüştürür.
 * Sadece Resource.Success durumunda 'transform' fonksiyonunu uygular.
 * Hata ve Yüklenme durumlarını olduğu gibi aktarır.
 */
inline fun <T, R> Resource<T>.mapSuccess(transform: (T) -> R): Resource<R> {
    return when (this) {
        is Resource.Success -> Resource.Success(transform(this.data))
        is Resource.Error -> this
        is Resource.Loading -> this
        is Resource.Idle -> this
    }
}