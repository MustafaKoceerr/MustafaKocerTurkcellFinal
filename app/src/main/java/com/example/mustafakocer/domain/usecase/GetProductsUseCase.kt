package com.example.mustafakocer.domain.usecase

import androidx.paging.PagingData
import com.example.mustafakocer.domain.model.Product
import com.example.mustafakocer.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Encapsulates the business logic for retrieving the main paginated list of all products.
 */
class GetProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository,
) {
    /**
     * Executes the use case.
     * @return A [Flow] of [PagingData] containing the products.
     */
    operator fun invoke(): Flow<PagingData<Product>> =
        productRepository.getPaginatedProducts()
}