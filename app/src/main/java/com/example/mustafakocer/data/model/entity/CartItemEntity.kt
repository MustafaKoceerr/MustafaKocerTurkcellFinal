package com.example.mustafakocer.data.model.entity

import com.google.firebase.database.IgnoreExtraProperties

/**
 * Represents a single item in the user's shopping cart stored in Firebase Realtime Database.
 * @property productId The unique ID of the product.
 * @property quantity The number of units of the product in the cart.
 */
@IgnoreExtraProperties
data class CartItemEntity(
    val productId: Int? = null,
    val quantity: Int? = null
) {
    /**
     * A no-argument constructor is required by Firebase for data deserialization.
     */
    constructor() : this(0, 0)
}