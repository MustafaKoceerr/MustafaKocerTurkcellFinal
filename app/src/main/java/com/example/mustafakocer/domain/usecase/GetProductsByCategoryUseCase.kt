package com.example.mustafakocer.domain.usecase

import androidx.paging.PagingData
import com.example.mustafakocer.domain.model.Product
import com.example.mustafakocer.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductsByCategoryUseCase @Inject constructor(
    private val productRepository: ProductRepository,
) {

    operator fun invoke(categoryName: String): Flow<PagingData<Product>> {
        // Repository'deki YENİ, sayfalama destekli fonksiyonu çağırıyoruz.
        return productRepository.getPaginatedProductsByCategory(categoryName)
    }
}