package com.example.mustafakocer.data.model.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrdersResponseDto(
    // DÜZELTME: Varsayılan değerler ve @SerialName eklendi.
    @SerialName("carts") val carts: List<OrderDto> = emptyList(),
    @SerialName("total") val total: Int = 0,
    @SerialName("skip") val skip: Int = 0,
    @SerialName("limit") val limit: Int = 0
)

@Serializable
data class OrderDto(
    // DÜZELTME: Varsayılan değerler ve @SerialName eklendi.
    @SerialName("id") val id: Int = 0,
    @SerialName("products") val products: List<OrderProductDto> = emptyList(),
    @SerialName("total") val total: Double = 0.0,
    @SerialName("discountedTotal") val discountedTotal: Double = 0.0,
    @SerialName("userId") val userId: Int = 0,
    @SerialName("totalProducts") val totalProducts: Int = 0,
    @SerialName("totalQuantity") val totalQuantity: Int = 0
)

@Serializable
data class OrderProductDto(
    // DÜZELTME: Varsayılan değerler ve @SerialName eklendi.
    @SerialName("id") val id: Int = 0,
    @SerialName("title") val title: String = "",
    @SerialName("price") val price: Double = 0.0,
    @SerialName("quantity") val quantity: Int = 0,
    @SerialName("total") val total: Double = 0.0,
    @SerialName("discountPercentage") val discountPercentage: Double = 0.0,
    @SerialName("discountedTotal") val discountedTotal: Double = 0.0,
    @SerialName("thumbnail") val thumbnail: String = ""
)