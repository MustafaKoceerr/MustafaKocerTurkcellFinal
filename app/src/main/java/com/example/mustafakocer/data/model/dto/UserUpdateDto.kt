package com.example.mustafakocer.data.model.dto

import kotlinx.serialization.Serializable

/**
 * Kullanıcı profilini güncellemek için API'ye gönderilecek olan istek gövdesi.
 * Sadece güncellenebilecek alanları içerir ve tüm alanlar opsiyoneldir.
 * `kotlinx.serialization`, null olan alanları JSON'a dahil etmeyecektir.
 */
@Serializable
data class UserUpdateDto(
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val age: Int? = null
)