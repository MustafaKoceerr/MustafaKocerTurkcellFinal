package com.example.mustafakocer.domain.usecase

import com.example.mustafakocer.domain.model.ProductDetail
import com.example.mustafakocer.domain.repository.ProductRepository
import com.example.mustafakocer.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Encapsulates the business logic for fetching the details of a specific product.
 */
class GetProductDetailUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    /**
     * ARCHITECTURAL NOTE: This UseCase abstracts the Presentation layer (ViewModel) from
     * the implementation details of the Data layer. The ViewModel simply says
     * "give me the product details," without knowing if the data comes from an API,
     * a database, or a cache.
     *
     * @param productId The ID of the product whose details are to be fetched.
     * @return A [Flow] of [Resource] containing the product details.
     */
    operator fun invoke(productId: Int): Flow<Resource<ProductDetail>> =
        productRepository.getProductDetail(productId)
}