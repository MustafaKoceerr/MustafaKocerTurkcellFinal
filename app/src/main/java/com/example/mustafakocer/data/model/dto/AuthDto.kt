package com.example.mustafakocer.data.model.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// API'ye gönderilecek olan istek gövdesi
@Serializable
data class LoginRequestDto(
    @SerialName("username") val username: String,
    @SerialName("password") val password: String
)

// Kullanıcı login olurken bize gelen cevap
@Serializable
data class LoginResponseDto(
    // DÜZELTME: Tüm nullable alanlara varsayılan 'null' değeri atandı.
    @SerialName("id") val id: Int,
    @SerialName("username") val username: String? = null,
    @SerialName("email") val email: String? = null,
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("gender") val gender: String? = null,
    @SerialName("image") val image: String? = null,
    @SerialName("accessToken") val token: String
)