package com.example.mustafakocer.domain.usecase

import com.example.mustafakocer.domain.repository.CartRepository
import com.example.mustafakocer.domain.util.Resource
import javax.inject.Inject

/**
 * Encapsulates the business logic for adding a product to the cart or increasing its quantity.
 * This use case acts as an intermediary between the ViewModel and the [CartRepository].
 */
class AddOrIncreaseCartItemUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    /**
     * Executes the use case.
     * @param productId The ID of the product to add or increase.
     * @return A [Resource] indicating the outcome of the operation.
     */
    suspend operator fun invoke(productId: Int): Resource<Unit> =
        cartRepository.addOrIncreaseCartItem(productId)
}