package com.example.mustafakocer.data.model.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Bu, API'den gelen tek bir ürünün tüm verisini temsil eder.
@Serializable
data class ProductDetailDto(
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
    @SerialName("images") val images: List<String>? = null,
    @SerialName("reviews") val reviews: List<ReviewDto>? = null
)

@Serializable
data class ReviewDto(
    @SerialName("rating") val rating: Int? = null,
    @SerialName("comment") val comment: String? = null,
    @SerialName("date") val date: String? = null,
    @SerialName("reviewerName") val reviewerName: String? = null
)