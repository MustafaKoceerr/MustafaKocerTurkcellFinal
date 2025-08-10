package com.example.mustafakocer.domain.model

/**
 * UI katmanında bir ürünü temsil eden temiz veri sınıfı.
 */
data class Product(
    val id: Int,
    val title: String,
    val price: String,
    val discountedPrice: String,
    val thumbnailUrl: String,
    val rating: Float,
    val stock: Int
)