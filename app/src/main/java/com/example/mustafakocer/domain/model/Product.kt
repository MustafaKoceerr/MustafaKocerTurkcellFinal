package com.example.mustafakocer.domain.model

/**
 * A clean data class representing a product summary in the UI layer,
 * typically used in lists.
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