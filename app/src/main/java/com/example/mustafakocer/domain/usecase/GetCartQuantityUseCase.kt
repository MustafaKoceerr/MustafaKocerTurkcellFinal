package com.example.mustafakocer.domain.usecase

import com.example.mustafakocer.domain.repository.CartRepository
import com.example.mustafakocer.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * A business rule that listens to the cart and provides the quantity
 * for a specific product.
 */
class GetCartQuantityUseCase @Inject constructor(
    private val cartRepository: CartRepository,
) {
    operator fun invoke(productId: Int): Flow<Int> {
        return cartRepository.getRawCartItems().map { resource ->
            when (resource) {
                is Resource.Success -> {
                    // Find the specific item in the list and return its quantity, or 0 if not found.
                    resource.data.find { it.productId == productId }?.quantity ?: 0
                }
                // During loading or on error, the quantity is considered 0.
                else -> 0
            }
        }
    }
}