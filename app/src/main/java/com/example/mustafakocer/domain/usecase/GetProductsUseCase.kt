package com.example.mustafakocer.domain.usecase

import androidx.paging.PagingData
import com.example.mustafakocer.domain.model.Product
import com.example.mustafakocer.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository,
) {
    operator fun invoke(): Flow<PagingData<Product>> {
        return productRepository.getPaginatedProducts()
    }
}