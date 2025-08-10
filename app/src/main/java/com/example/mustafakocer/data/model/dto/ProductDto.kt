package com.example.mustafakocer.data.model.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductsResponseDto(
    @SerialName("products") val products: List<ProductDto> = listOf(),
    @SerialName("total") val total: Int? = null,
    @SerialName("skip") val skip: Int? = null,
    @SerialName("limit") val limit: Int? = null
)

@Serializable
data class ProductDto(
    // DÜZELTME: Tüm nullable alanlara varsayılan 'null' değeri atandı.
    // Bu, 'MissingFieldException' hatasını önler.
    // Ayrıca tutarlılık için tüm alanlara @SerialName eklendi.
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
    @SerialName("images") val images: List<String>? = listOf()
)