package com.example.mustafakocer.domain.usecase

import com.example.mustafakocer.domain.repository.CartRepository
import com.example.mustafakocer.domain.util.Resource
import javax.inject.Inject

class AddOrIncreaseCartItemUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke( productId: Int): Resource<Unit> {
        return cartRepository.addOrIncreaseCartItem( productId)
    }
}