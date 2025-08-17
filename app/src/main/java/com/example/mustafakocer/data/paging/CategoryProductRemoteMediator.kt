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
import com.example.mustafakocer.data.network.IDummyApi
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class CategoryProductRemoteMediator @AssistedInject constructor(
    @Assisted private val categoryName: String, // Dinamik parametre
    private val api: IDummyApi, // Hilt tarafından sağlanacak
    private val db: AppDatabase // Hilt tarafından sağlanacak
) : RemoteMediator<Int, ProductEntity>() {

    private val productDao = db.createProductDao()
    private val categoryRemoteKeyDao = db.createCategoryRemoteKeyDao()
    // Factory arayüzü, Hilt'e bu sınıfı nasıl yaratacağını söyler.
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

            // **DÜZELTME:** API çağrısına artık 'limit' ve 'skip' parametrelerini ekliyoruz.
            val response = api.getProductsByCategory(
                categoryName = categoryName,
                limit = state.config.pageSize,
                skip = page * state.config.pageSize
            )
            val productsDto = response.body()?.products ?: emptyList()
            // **DÜZELTME:** Sayfa sonuna gelip gelmediğimizi API'den dönen listeye göre belirliyoruz.
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
