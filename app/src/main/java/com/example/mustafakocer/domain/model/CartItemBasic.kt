package com.example.mustafakocer.domain.model

/**
 * Represents the fundamental data of a cart item without the full product details.
 * Used for lightweight operations within the domain and data layers.
 */
data class CartItemBasic(
    val productId: Int,
    val quantity: Int
)