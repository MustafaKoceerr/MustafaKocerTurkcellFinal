package com.example.mustafakocer.domain.usecase

import com.example.mustafakocer.domain.repository.CartRepository
import com.example.mustafakocer.domain.util.Resource
import javax.inject.Inject

/**
 * Encapsulates the business logic for clearing all items from the user's shopping cart.
 */
class ClearCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    /**
     * Executes the use case.
     * @return A [Resource] indicating the outcome of the operation.
     */
    suspend operator fun invoke(): Resource<Unit> =
        cartRepository.clearCart()
}