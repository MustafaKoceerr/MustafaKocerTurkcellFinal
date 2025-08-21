package com.example.mustafakocer.domain.repository

import androidx.paging.PagingData
import com.example.mustafakocer.domain.model.Category
import com.example.mustafakocer.domain.model.Product
import com.example.mustafakocer.domain.model.ProductDetail
import com.example.mustafakocer.domain.util.Resource
import kotlinx.coroutines.flow.Flow

/**
 * A contract for the data layer to handle all data operations related to products and categories.
 * This interface abstracts the data source details (network, database, etc.).
 */
interface ProductRepository {

    /**
     * Retrieves a paginated stream of all products, typically for the home screen.
     * This is expected to be implemented with an offline-first strategy.
     */
    fun getPaginatedProducts(): Flow<PagingData<Product>>

    /**
     * Fetches the list of all available product categories.
     */
    fun getCategories(): Flow<Resource<List<Category>>>

    /**
     * Retrieves a paginated stream of products filtered by a specific category.
     */
    fun getPaginatedProductsByCategory(categoryName: String): Flow<PagingData<Product>>

    /**
     * Retrieves a paginated stream of products matching a search query.
     * This is expected to be a network-only operation.
     */
    fun searchPaginatedProducts(query: String): Flow<PagingData<Product>>

    /**
     * Fetches a single product's summary data as a [Resource] flow.
     */
    fun getSingleProduct(productId: Int): Flow<Resource<Product>>

    /**
     * Fetches the full details of a single product as a [Resource] flow.
     */
    fun getProductDetail(productId: Int): Flow<Resource<ProductDetail>>

    /**
     * Fetches a single product's summary data directly.
     * This suspend function will either return the [Product] on success or throw an
     * [AppException] on failure.
     */
    suspend fun getProduct(productId: Int): Product
}