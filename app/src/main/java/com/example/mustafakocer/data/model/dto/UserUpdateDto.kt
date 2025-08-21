package com.example.mustafakocer.data.model.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the request body for updating a user's profile.
 * Fields are nullable, so only non-null values will be sent in the JSON payload.
 */
@Serializable
data class UserUpdateDto(
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("email") val email: String? = null,
    @SerialName("phone") val phone: String? = null,
    @SerialName("age") val age: Int? = null
)