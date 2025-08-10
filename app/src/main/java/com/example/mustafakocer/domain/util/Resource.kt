package com.example.mustafakocer.domain.util

import com.example.mustafakocer.domain.exception.AppException

/**
 * Bir veri isteğinin durumunu temsil eden sealed bir sarmalayıcı sınıf.
 *
 * MİMARİ NOT: Bu sınıf, asenkron işlemlerin (API çağrıları gibi) durumunu
 * data/domain katmanlarından UI katmanına iletmek için temeldir. Yüklenme, başarı
 * ve hata durumlarını açıkça ele alarak, ViewModel ve UI'daki durum yönetimini
 * çok daha temiz ve öngörülebilir hale getirir.
 */
sealed class Resource<out T> {
    /** İşlemin henüz başlamadığını veya bir eylem beklediğini belirtir. */
    data object Idle : Resource<Nothing>()

    /** İşlemin başladığını ve sonucun beklendiğini belirtir. */
    data object Loading : Resource<Nothing>()

    /** İşlemin başarıyla tamamlandığını ve veri içerdiğini belirtir. */
    data class Success<out T>(val data: T) : Resource<T>()

    /** İşlem sırasında bir hata oluştuğunu belirtir. */
    data class Error(val exception: AppException) : Resource<Nothing>()
}