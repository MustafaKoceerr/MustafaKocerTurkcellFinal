package com.example.mustafakocer.domain.usecase

import com.example.mustafakocer.domain.repository.CartRepository
import com.example.mustafakocer.domain.util.Resource
import javax.inject.Inject

/**
 * Encapsulates the business logic for decreasing a product's quantity in the cart
 * or removing it entirely if the quantity is one.
 */
class DecreaseOrRemoveCartItemUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    /**
     * Executes the use case.
     * @param productId The ID of the product to decrease or remove.
     * @return A [Resource] indicating the outcome of the operation.
     */
    suspend operator fun invoke(productId: Int): Resource<Unit> =
        cartRepository.decreaseOrRemoveCartItem(productId)
}