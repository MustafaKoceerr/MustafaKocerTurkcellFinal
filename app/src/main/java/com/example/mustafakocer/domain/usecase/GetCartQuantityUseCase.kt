package com.example.mustafakocer.domain.usecase

import com.example.mustafakocer.domain.repository.CartRepository
import com.example.mustafakocer.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Belirtilen bir ürünün sepetteki miktarını dinleyen iş kuralı.
 */
class GetCartQuantityUseCase @Inject constructor(
    private val cartRepository: CartRepository,
) {
    operator fun invoke(productId: Int): Flow<Int> {
        // Ham sepet verisini dinle
        return cartRepository.getRawCartItems().map { resource ->
            when (resource) {
                is Resource.Success -> {
                    // Başarılı durumda, listede bizim ürünümüzü bul ve miktarını döndür.
                    // Bulamazsan 0 döndür.
                    resource.data.find { (pId, _) -> pId == productId }?.second ?: 0
                }
                // Hata veya yüklenme durumunda miktar 0'dır.
                else -> 0
            }
        }
    }
}