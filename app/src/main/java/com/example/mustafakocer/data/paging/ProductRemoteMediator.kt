package com.example.mustafakocer.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.mustafakocer.data.db.AppDatabase
import com.example.mustafakocer.data.mapper.toEntity
import com.example.mustafakocer.data.model.entity.ProductEntity
import com.example.mustafakocer.data.model.entity.HomeRemoteKeyEntity
import com.example.mustafakocer.data.network.IDummyApi
import retrofit2.HttpException
import java.io.IOException

/**
 * Offline first için gerekli.
 */
@OptIn(ExperimentalPagingApi::class)
class ProductRemoteMediator (
    private val api: IDummyApi,
    private val db: AppDatabase
): RemoteMediator<Int, ProductEntity>(){

    private val productDao = db.createProductDao()
    private val remoteKeyDao = db.createHomeRemoteKeyDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ProductEntity>
    ): MediatorResult {
        return try {
            // 1. Hangi sayfayı çekeceğimizi belirle
            val page = when (loadType) {
                LoadType.REFRESH -> 0
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKeys = getLastRemoteKey(state)
                    remoteKeys?.nextKey ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            // 2. API'ye isteği at
            val response = api.getProducts(
                limit = state.config.pageSize,
                skip = page * state.config.pageSize
            )
            val productsDto = response.body()?.products ?: emptyList()
            val endOfPaginationReached = productsDto.isEmpty()

            // 3. Gelen veriyi ve yeni remote key'leri veritabanına kaydet
            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    productDao.clearAllProducts()
                    remoteKeyDao.clearRemoteKeys()
                }
                val prevKey = if (page == 0) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = productsDto.map {
                    HomeRemoteKeyEntity(productId = it.id!!, prevKey = prevKey, nextKey = nextKey)
                }
                val entities = productsDto.map { it.toEntity() } // DTO -> Entity

                remoteKeyDao.insertAll(keys)
                productDao.insertAll(entities)
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getLastRemoteKey(state: PagingState<Int, ProductEntity>): HomeRemoteKeyEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { product -> remoteKeyDao.getRemoteKeyForProductId(product.id) }
    }
}