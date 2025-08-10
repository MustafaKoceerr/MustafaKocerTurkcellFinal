package com.example.mustafakocer.domain.usecase

import androidx.paging.PagingData
import com.example.mustafakocer.domain.model.Product
import com.example.mustafakocer.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Ürünlerin sayfalama destekli listesini getirme iş kuralını kapsüller.
 */
class GetProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    /**
     * MİMARİ NOT: Bu UseCase, Repository'den gelen PagingData akışını doğrudan
     * ViewModel'e iletir. PagingData, basit bir veri listesi değil, kendi içinde
     * sayfa yükleme mantığını barındıran karmaşık bir veri akışıdır.
     */
    operator fun invoke(): Flow<PagingData<Product>> {
        // Repository'deki yeni offline-first fonksiyonunu çağırıyoruz.
        return productRepository.getPaginatedProducts()
    }
}