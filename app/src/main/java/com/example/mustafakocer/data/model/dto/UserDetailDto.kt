package com.example.mustafakocer.data.model.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

/**
 * Represents the detailed user profile data as returned by the API.
 */
@Serializable
data class UserDetailDto(
    @SerialName("id") val id: Int,
    @SerialName("firstName") val firstName: String,
    @SerialName("lastName") val lastName: String,
    @SerialName("email") val email: String,
    @SerialName("username") val username: String,
    @SerialName("age") val age: Int? = null,
    @SerialName("gender") val gender: String? = null,
    @SerialName("phone") val phone: String? = null,
    @SerialName("birthDate") val birthDate: String? = null,
    @SerialName("image") val image: String? = null
)