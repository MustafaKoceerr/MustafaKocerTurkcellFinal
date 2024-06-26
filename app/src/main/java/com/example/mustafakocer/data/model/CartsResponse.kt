package com.example.mustafakocer.data.model

import com.google.gson.annotations.SerializedName


data class OrderResponse(
    @SerializedName("carts") val carts: List<CartResponse>,
    @SerializedName("total") val total: Int? = null,
    @SerializedName("skip") val skip: Int? = null,
    @SerializedName("limit") val limit: Int? = null
)


data class CartResponse(

    @SerializedName("id") val id: Int? = null,
    @SerializedName("products") val products: List<ProductsInCart>? = null,
    @SerializedName("total") val total: Double? = null,
    @SerializedName("discountedTotal") val discountedTotal: Double? = null,
    @SerializedName("userId") val userId: Int? = null,
    @SerializedName("totalProducts") val totalProducts: Int? = null,
    @SerializedName("totalQuantity") val totalQuantity: Int? = null
)


data class ProductsInCart(

    @SerializedName("id") val id: Int? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("price") val price: Double? = null,
    @SerializedName("quantity") val quantity: Int? = null,
    @SerializedName("total") val total: Double? = null,
    @SerializedName("discountPercentage") val discountPercentage: Double? = null,
    @SerializedName("discountedTotal") val discountedTotal: Double? = null,
    @SerializedName("thumbnail") val thumbnail: String? = null
)