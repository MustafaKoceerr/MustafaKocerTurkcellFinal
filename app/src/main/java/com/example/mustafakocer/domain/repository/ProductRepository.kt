package com.example.mustafakocer.domain.repository

import androidx.paging.PagingData
import com.example.mustafakocer.domain.model.Category
import com.example.mustafakocer.domain.model.Product
import com.example.mustafakocer.domain.model.ProductDetail
import com.example.mustafakocer.domain.util.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Contract for all data operations related to products and categories.
 * This interface abstracts the data source details (network, database, etc.).
 */
interface ProductRepository {

    fun getPaginatedProducts(): Flow<PagingData<Product>>

    fun getCategories(): Flow<Resource<List<Category>>>

    fun getPaginatedProductsByCategory(categoryName: String): Flow<PagingData<Product>>

    fun searchPaginatedProducts(query: String): Flow<PagingData<Product>>

    fun getSingleProduct(productId: Int): Flow<Resource<Product>>

    fun getProductDetail(productId: Int): Flow<Resource<ProductDetail>>

    suspend fun getProduct(productId: Int): Product

}