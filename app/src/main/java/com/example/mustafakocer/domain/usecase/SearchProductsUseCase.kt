package com.example.mustafakocer.domain.usecase

import androidx.paging.PagingData
import com.example.mustafakocer.domain.model.Product
import com.example.mustafakocer.domain.repository.ProductRepository
import com.example.mustafakocer.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class SearchProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository,
) {
    operator fun invoke(query: String): Flow<PagingData<Product>> {
        if (query.length < MIN_QUERY_LENGTH) {
            // Paging 3 için "boş durum", boş bir PagingData akışı döndürmektir.
            return flowOf(PagingData.empty())
        }

        // Repository'deki yeni sayfalama fonksiyonunu çağır.
        return productRepository.searchPaginatedProducts(query)
    }

    companion object {
        private const val MIN_QUERY_LENGTH = 3
    }
}