package com.example.mustafakocer.domain.usecase

import com.example.mustafakocer.domain.model.Category
import com.example.mustafakocer.domain.repository.ProductRepository
import com.example.mustafakocer.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Encapsulates the business logic for fetching the list of product categories.
 *
 * Even though this is a simple pass-through to the repository, creating a use case
 * maintains a consistent architecture. It allows for adding business logic here in the
 * future (e.g., filtering, sorting) without modifying the ViewModel or Repository.
 */
class GetCategoriesUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    /**
     * Executes the use case.
     * @return A [Flow] of [Resource] containing the list of [Category] objects.
     */
    operator fun invoke(): Flow<Resource<List<Category>>> =
        productRepository.getCategories()
}
