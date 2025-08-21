package com.example.mustafakocer.domain.repository

import com.example.mustafakocer.domain.model.CartItemBasic
import com.example.mustafakocer.domain.util.Resource
import kotlinx.coroutines.flow.Flow

/**
 * A contract for the data layer to handle all shopping cart operations.
 */
interface CartRepository {
    /**
     * Listens for real-time updates to the user's cart and provides the basic
     * cart data (product ID and quantity).
     * @return A flow emitting the resource state of the raw cart items.
     */
    fun getRawCartItems(): Flow<Resource<List<CartItemBasic>>>

    /**
     * Adds a product to the cart or increases its quantity if it already exists.
     * @return A resource indicating the success or failure of the operation.
     */
    suspend fun addOrIncreaseCartItem(productId: Int): Resource<Unit>

    /**
     * Decreases a product's quantity in the cart or removes it if the quantity is one.
     * @return A resource indicating the success or failure of the operation.
     */
    suspend fun decreaseOrRemoveCartItem(productId: Int): Resource<Unit>

    /**
     * Removes all items from the user's cart.
     * @return A resource indicating the success or failure of the operation.
     */
    suspend fun clearCart(): Resource<Unit>

    /**
     * Completely removes a specific product from the cart, regardless of its quantity.
     * @return A resource indicating the success or failure of the operation.
     */
    suspend fun removeCartItem(productId: Int): Resource<Unit>
}