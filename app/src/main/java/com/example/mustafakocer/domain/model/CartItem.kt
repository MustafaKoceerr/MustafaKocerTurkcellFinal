package com.example.mustafakocer.domain.model

/**
 * Represents a single item within the user's shopping cart.
 * It composes a [Product] with its corresponding quantity, demonstrating the
 * "Composition" principle.
 */
data class CartItem(
    val product: Product,
    val quantity: Int
)