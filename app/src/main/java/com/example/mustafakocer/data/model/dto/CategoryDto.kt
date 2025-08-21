package com.example.mustafakocer.data.model.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Type alias for the response from the categories endpoint, which is a list of [CategoryDto].
 */
typealias CategoriesResponseDto = List<CategoryDto>

/**
 * Represents a single category object as returned by the API.
 */
@Serializable
data class CategoryDto(
    @SerialName("slug") val slug: String,
    @SerialName("name") val name: String,
    @SerialName("url") val url: String? = null
)