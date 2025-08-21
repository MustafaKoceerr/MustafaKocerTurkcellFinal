package com.example.mustafakocer.domain.usecase

import androidx.paging.PagingData
import com.example.mustafakocer.domain.model.Product
import com.example.mustafakocer.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Encapsulates the business logic for retrieving a paginated list of products
 * filtered by a specific category.
 */
class GetProductsByCategoryUseCase @Inject constructor(
    private val productRepository: ProductRepository,
) {
    /**
     * Executes the use case.
     * @param categoryName The name of the category to filter by.
     * @return A [Flow] of [PagingData] containing the filtered products.
     */
    operator fun invoke(categoryName: String): Flow<PagingData<Product>> =
        productRepository.getPaginatedProductsByCategory(categoryName)
}