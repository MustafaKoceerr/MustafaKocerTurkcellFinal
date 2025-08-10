package com.example.mustafakocer.domain.usecase

import com.example.mustafakocer.domain.model.Category
import com.example.mustafakocer.domain.repository.ProductRepository
import com.example.mustafakocer.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    operator fun invoke(): Flow<Resource<List<Category>>> {
        return productRepository.getCategories()
    }
}