package com.example.mustafakocer.domain.usecase

import androidx.paging.PagingData
import com.example.mustafakocer.domain.model.Product
import com.example.mustafakocer.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * Encapsulates the business logic for searching products.
 * It enforces a minimum query length to prevent unnecessary or overly broad API calls.
 */
class SearchProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository,
) {
    /**
     * Executes the search use case.
     * @param query The search term entered by the user.
     * @return A [Flow] of [PagingData] containing the search results. If the query is
     * shorter than [MIN_QUERY_LENGTH], it returns a flow with empty data.
     */
    operator fun invoke(query: String): Flow<PagingData<Product>> {
        if (query.length < MIN_QUERY_LENGTH) {
            return flowOf(PagingData.empty())
        }
        return productRepository.searchPaginatedProducts(query)
    }

    companion object {
        /**
         * The minimum number of characters required to trigger a search.
         */
        const val MIN_QUERY_LENGTH = 3
    }
}