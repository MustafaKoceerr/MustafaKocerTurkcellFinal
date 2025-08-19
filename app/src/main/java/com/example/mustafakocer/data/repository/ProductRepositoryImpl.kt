package com.example.mustafakocer.data.repository

import androidx.paging.*
import com.example.mustafakocer.data.db.AppDatabase
import com.example.mustafakocer.data.mapper.toDomain
import com.example.mustafakocer.data.mapper.toDomainProduct
import com.example.mustafakocer.data.network.IDummyApi
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

// DEĞİŞİKLİK: CoroutineScope bağımlılığı kaldırıldı.
class ProductRepositoryImpl @Inject constructor(
    private val api: IDummyApi,
    private val db: AppDatabase,
    private val categoryMediatorFactory: CategoryProductRemoteMediator.Factory,
    private val errorMapper: ErrorMapper, // Enjekte edildi
) : ProductRepository {

    companion object {
        private const val PAGE_SIZE = 20
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getPaginatedProducts(): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            remoteMediator = ProductRemoteMediator(api, db),
            pagingSourceFactory = { db.createProductDao().getProductsPagingSource() }
        ).flow.map { pagingData ->
            pagingData.map { productEntity ->
                productEntity.toDomain()
            }
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getPaginatedProductsByCategory(categoryName: String): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = PagingConstants.PRODUCT_PAGE_SIZE,
                enablePlaceholders = false
            ),
            // DEĞİŞTİ: Artık 'new' ile yaratmıyoruz, factory'yi kullanıyoruz.
            remoteMediator = categoryMediatorFactory.create(categoryName),
            pagingSourceFactory = {
                db.createProductDao().getProductsByCategoryPagingSource(categoryName)
            }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    override fun searchPaginatedProducts(query: String): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { ProductPagingSource(api, query) }
        ).flow
    }

    override fun getCategories(): Flow<Resource<List<Category>>> {
        return safeApiCall(errorMapper) { api.getCategories() }.map { resource -> // Parametre olarak verildi
            resource.mapSuccess { dtoList -> dtoList.map { it.toDomain() } }
        }
    }

    override fun getSingleProduct(productId: Int): Flow<Resource<Product>> {
        return safeApiCall(errorMapper) { api.getProductById(productId) }.map { resource -> // Parametre olarak verildi
            resource.mapSuccess { it.toDomainProduct() }
        }
    }

    override fun getProductDetail(productId: Int): Flow<Resource<ProductDetail>> {
        return safeApiCall(errorMapper) { api.getProductById(productId) }.map { resource -> // Parametre olarak verildi
            resource.mapSuccess { it.toDomain() }
        }
    }

    override suspend fun getProduct(productId: Int): Product {
        // Mevcut Flow'u kullanıyoruz ama sonucunu burada çözümlüyoruz.
        val resource = safeApiCall(errorMapper) { api.getProductById(productId) }
            .map { res -> res.mapSuccess { it.toDomainProduct() } }
            // Akıştan ilk gelen ve Loading OLMAYAN sonucu al.
            .first { it !is Resource.Loading }

        return when (resource) {
            is Resource.Success -> resource.data
            is Resource.Error -> throw resource.exception // Hata durumunda exception fırlat.
            is Resource.Idle -> throw IllegalStateException("Idle state should not be possible here.")
            is Resource.Loading -> throw IllegalStateException("Loading state should have been filtered out.")
        }
    }
}