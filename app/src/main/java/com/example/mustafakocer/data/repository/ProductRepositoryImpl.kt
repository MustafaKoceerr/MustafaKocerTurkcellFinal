package com.example.mustafakocer.data.repository

import androidx.paging.*
import com.example.mustafakocer.data.db.AppDatabase
import com.example.mustafakocer.data.mapper.toDomain
import com.example.mustafakocer.data.mapper.toDomainProduct
import com.example.mustafakocer.data.network.DummyApi
import com.example.mustafakocer.data.network.util.safeApiCall
import com.example.mustafakocer.data.paging.CategoryProductRemoteMediator
import com.example.mustafakocer.data.paging.ProductPagingSource
import com.example.mustafakocer.data.paging.ProductRemoteMediator
import com.example.mustafakocer.domain.mapper.ErrorMapper
import com.example.mustafakocer.domain.model.Category
import com.example.mustafakocer.domain.model.Product
import com.example.mustafakocer.domain.model.ProductDetail
import com.example.mustafakocer.domain.repository.ProductRepository
import com.example.mustafakocer.domain.util.Resource
import com.example.mustafakocer.domain.util.mapSuccess
import com.example.mustafakocer.util.PagingConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Provider

/**
 * Implements the [ProductRepository], acting as the single source of truth for all product-related data.
 * It intelligently orchestrates data retrieval by combining different strategies:
 * - Offline-first pagination with [ProductRemoteMediator].
 * - Dynamic, category-specific pagination with [CategoryProductRemoteMediator].
 * - Network-only pagination for search results with [ProductPagingSource].
 * - Standard network calls for single items and categories.
 */
class ProductRepositoryImpl @Inject constructor(
    private val api: DummyApi,
    private val db: AppDatabase,
    private val categoryMediatorFactory: CategoryProductRemoteMediator.Factory,
    private val mediatorProvider: Provider<ProductRemoteMediator>,
    private val errorMapper: ErrorMapper,
) : ProductRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getPaginatedProducts(): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = PagingConstants.PRODUCT_PAGE_SIZE,
                enablePlaceholders = false
            ),
            remoteMediator = mediatorProvider.get(),
            pagingSourceFactory = { db.createProductDao().getProductsPagingSource() }
        ).flow.map { pagingData -> pagingData.map { it.toDomain() } }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getPaginatedProductsByCategory(categoryName: String): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = PagingConstants.PRODUCT_PAGE_SIZE,
                enablePlaceholders = false
            ),
            remoteMediator = categoryMediatorFactory.create(categoryName),
            pagingSourceFactory = { db.createProductDao().getProductsByCategoryPagingSource(categoryName) }
        ).flow.map { pagingData -> pagingData.map { it.toDomain() } }
    }

    override fun searchPaginatedProducts(query: String): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = PagingConstants.PRODUCT_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { ProductPagingSource(api, query) }
        ).flow
    }

    override fun getCategories(): Flow<Resource<List<Category>>> {
        return safeApiCall(errorMapper) { api.getCategories() }
            .map { resource -> resource.mapSuccess { dtoList -> dtoList.map { it.toDomain() } } }
    }

    override fun getSingleProduct(productId: Int): Flow<Resource<Product>> {
        return safeApiCall(errorMapper) { api.getProductById(productId) }
            .map { resource -> resource.mapSuccess { it.toDomainProduct() } }
    }

    override fun getProductDetail(productId: Int): Flow<Resource<ProductDetail>> {
        return safeApiCall(errorMapper) { api.getProductById(productId) }
            .map { resource -> resource.mapSuccess { it.toDomain() } }
    }

    override suspend fun getProduct(productId: Int): Product {
        return getSingleProduct(productId).awaitSuccess()
    }
}

/**
 * A private extension function to await the first non-loading result from a [Resource] flow.
 * It simplifies converting a reactive flow into a single result within a suspend context.
 *
 * @return The data from the [Resource.Success] state.
 * @throws The exception from the [Resource.Error] state.
 */
private suspend fun <T> Flow<Resource<T>>.awaitSuccess(): T {
    val resource = this.first { it !is Resource.Loading }
    return when (resource) {
        is Resource.Success -> resource.data
        is Resource.Error -> throw resource.exception
        else -> throw IllegalStateException("Flow should have emitted Success or Error")
    }
}