package com.example.mustafakocer.domain.usecase

import com.example.mustafakocer.domain.repository.CartRepository
import com.example.mustafakocer.domain.util.Resource
import javax.inject.Inject

/**
 * Belirtilen bir ürünü sepetten tamamen kaldırma iş kuralını kapsüller.
 */
class RemoveCartItemUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    /**
     * Bu UseCase'i bir fonksiyon gibi çağrılabilir hale getirir.
     */
    suspend operator fun invoke(userId: String, productId: Int): Resource<Unit> {
        return cartRepository.removeCartItem(userId, productId)
    }
}