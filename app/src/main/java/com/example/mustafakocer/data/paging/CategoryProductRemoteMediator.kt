package com.example.mustafakocer.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.mustafakocer.data.db.AppDatabase
import com.example.mustafakocer.data.mapper.toEntity
import com.example.mustafakocer.data.model.entity.CategoryRemoteKeyEntity
import com.example.mustafakocer.data.model.entity.ProductEntity
import com.example.mustafakocer.data.network.DummyApi
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import retrofit2.HttpException
import java.io.IOException

/**
 * A [RemoteMediator] for category-specific product lists.
 * It orchestrates loading data from the network ([DummyApi]) and saving it to the local
 * database ([AppDatabase]), serving as the single source of truth for paginated category data.
 *
 * @param categoryName The specific category to fetch products for. This is provided at runtime.
 * @param api The Retrofit API service, injected by Hilt.
 * @param db The Room database instance, injected by Hilt.
 */
@OptIn(ExperimentalPagingApi::class)
class CategoryProductRemoteMediator @AssistedInject constructor(
    @Assisted private val categoryName: String,
    private val api: DummyApi,
    private val db: AppDatabase
) : RemoteMediator<Int, ProductEntity>() {

    private val productDao = db.createProductDao()
    private val categoryRemoteKeyDao = db.createCategoryRemoteKeyDao()

    /**
     * A Hilt AssistedFactory for creating instances of [CategoryProductRemoteMediator].
     * This allows Hilt to provide the static dependencies (`api`, `db`) while allowing
     * the caller to provide the dynamic `categoryName` at creation time.
     */
    @AssistedFactory
    interface Factory {
        fun create(categoryName: String): CategoryProductRemoteMediator
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ProductEntity>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 0
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKeys = getLastRemoteKey(state)
                    remoteKeys?.nextKey ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            val response = api.getProductsByCategory(
                categoryName = categoryName,
                limit = state.config.pageSize,
                skip = page * state.config.pageSize
            )
            val productsDto = response.body()?.products ?: emptyList()
            val endOfPaginationReached = productsDto.isEmpty()

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    productDao.clearProductsByCategory(categoryName)
                    categoryRemoteKeyDao.clearRemoteKeysByCategory(categoryName)
                }

                val prevKey = if (page == 0) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1

                val keys = productsDto.map { productDto ->
                    CategoryRemoteKeyEntity(
                        category = categoryName,
                        productId = productDto.id!!,
                        prevKey = prevKey,
                        nextKey = nextKey
                    )
                }
                val entities = productsDto.map { it.toEntity() }

                categoryRemoteKeyDao.insertAll(keys)
                productDao.insertAll(entities)
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getLastRemoteKey(state: PagingState<Int, ProductEntity>): CategoryRemoteKeyEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { product ->
                categoryRemoteKeyDao.getRemoteKeyForProductId(product.id, categoryName)
            }
    }
}