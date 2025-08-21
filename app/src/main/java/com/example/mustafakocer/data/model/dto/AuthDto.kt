package com.example.mustafakocer.data.model.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the request body sent to the authentication endpoint.
 */
@Serializable
data class LoginRequestDto(
    @SerialName("username") val username: String,
    @SerialName("password") val password: String,
    @SerialName("expiresInMins") val expiresInMins: Int = 60
)

/**
 * Represents the successful response received from the authentication endpoint.
 */
@Serializable
data class LoginResponseDto(
    @SerialName("id") val id: Int,
    @SerialName("username") val username: String? = null,
    @SerialName("email") val email: String? = null,
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("gender") val gender: String? = null,
    @SerialName("image") val image: String? = null,
    @SerialName("accessToken") val token: String,
    @SerialName("refreshToken") val refreshToken: String
)