package com.example.mustafakocer.data.model.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the top-level response for a paginated list of products from the API.
 */
@Serializable
data class ProductsResponseDto(
    @SerialName("products") val products: List<ProductDto> = emptyList(),
    @SerialName("total") val total: Int? = null,
    @SerialName("skip") val skip: Int? = null,
    @SerialName("limit") val limit: Int? = null
)

/**
 * Represents a single product summary in a list.
 */
@Serializable
data class ProductDto(
    @SerialName("id") val id: Int?,
    @SerialName("title") val title: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("category") val category: String? = null,
    @SerialName("price") val price: Double? = null,
    @SerialName("discountPercentage") val discountPercentage: Double? = null,
    @SerialName("rating") val rating: Double? = null,
    @SerialName("stock") val stock: Int? = null,
    @SerialName("brand") val brand: String? = null,
    @SerialName("thumbnail") val thumbnail: String? = null,
    @SerialName("images") val images: List<String>? = emptyList()
)