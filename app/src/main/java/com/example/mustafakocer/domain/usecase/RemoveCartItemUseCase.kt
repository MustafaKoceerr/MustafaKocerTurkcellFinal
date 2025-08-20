package com.example.mustafakocer.domain.usecase

import com.example.mustafakocer.domain.repository.CartRepository
import com.example.mustafakocer.domain.util.Resource
import javax.inject.Inject

/**
 * Encapsulates the business logic for completely removing a product from the cart,
 * regardless of its quantity.
 */
class RemoveCartItemUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    /**
     * Executes the use case.
     * @param productId The ID of the product to remove.
     * @return A [Resource] indicating the outcome of the operation.
     */
    suspend operator fun invoke(productId: Int): Resource<Unit> =
        cartRepository.removeCartItem(productId)
}