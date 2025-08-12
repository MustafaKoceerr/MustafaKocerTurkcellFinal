package com.example.mustafakocer.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.mustafakocer.data.db.AppDatabase
import com.example.mustafakocer.data.mapper.toEntity
import com.example.mustafakocer.data.model.entity.HomeRemoteKeyEntity
import com.example.mustafakocer.data.model.entity.ProductEntity
import com.example.mustafakocer.data.network.IDummyApi
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
// Hilt anotasyonu yok, bu basit bir sınıf.
class ProductRemoteMediator(
    private val api: IDummyApi,
    private val db: AppDatabase
) : RemoteMediator<Int, ProductEntity>() {

    private val productDao = db.createProductDao()
    private val remoteKeyDao = db.createHomeRemoteKeyDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ProductEntity>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 0
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    // DEĞİŞTİ: Anahtarı PagingState yerine doğrudan DB'den alıyoruz.
                    val remoteKey = getRemoteKeyForLastItem()
                    // Eğer son anahtarın bir sonraki sayfası yoksa, paginasyon bitti demektir.
                    remoteKey?.nextKey ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            val response = api.getProducts(
                limit = state.config.pageSize,
                skip = page * state.config.pageSize
            )
            val productsDto = response.body()?.products ?: emptyList()
            val endOfPaginationReached = productsDto.isEmpty()

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
                val entities = productsDto.map { it.toEntity() }

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

    // DEĞİŞTİ: Bu fonksiyon artık PagingState'e bağımlı değil.
    private suspend fun getRemoteKeyForLastItem(): HomeRemoteKeyEntity? {
        // Veritabanındaki son ürünü al ve onun remote key'ini döndür.
        return productDao.getLastProduct()?.let { product ->
            remoteKeyDao.getRemoteKeyForProductId(product.id)
        }
    }
}