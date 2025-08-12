package com.example.mustafakocer.data.repository

import CategoryProductRemoteMediator
import androidx.paging.*
import com.example.mustafakocer.data.db.AppDatabase
import com.example.mustafakocer.data.mapper.toDomain
import com.example.mustafakocer.data.network.IDummyApi
import com.example.mustafakocer.data.network.util.safeApiCall
import com.example.mustafakocer.data.paging.ProductPagingSource
import com.example.mustafakocer.data.paging.ProductRemoteMediator
import com.example.mustafakocer.domain.model.Category
import com.example.mustafakocer.domain.model.Product
import com.example.mustafakocer.domain.model.ProductDetail
import com.example.mustafakocer.domain.repository.ProductRepository
import com.example.mustafakocer.domain.util.Resource
import com.example.mustafakocer.domain.util.mapSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// DEĞİŞİKLİK: CoroutineScope bağımlılığı kaldırıldı.
class ProductRepositoryImpl @Inject constructor(
    private val api: IDummyApi,
    private val db: AppDatabase,
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
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            remoteMediator = CategoryProductRemoteMediator(categoryName, api, db),
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
        // Artık API'den CategoriesResponseDto (yani List<CategoryDto>) geliyor.
        return safeApiCall { api.getCategories() }.map { resource ->
            // Resource.Success durumunda, gelen List<CategoryDto>'yu List<Category>'ye çeviriyoruz.
            resource.mapSuccess { categoryDtoList ->
                categoryDtoList.map { dto ->
                    // Her bir DTO için yeni mapper fonksiyonumuzu kullanıyoruz.
                    dto.toDomain()
                }
            }
        }
    }

    override fun getProductsByIds(ids: List<Int>): Flow<Resource<List<Product>>> {
        // Bu fonksiyon, networkBoundResource kullanarak hem offline hem online çalışabilir.
        // Şimdilik basit bir implementasyon yapalım: Sadece veritabanından çeksin.
        // Çünkü ürünlerin zaten RemoteMediator ile veritabanına güncel olarak çekildiğini varsayıyoruz.
        return db.createProductDao().getProductsByIds(ids).map { entities ->
            Resource.Success(entities.map { it.toDomain() })
        }
    }


    override fun getProductDetail(productId: Int): Flow<Resource<ProductDetail>> {
        // 1. API çağrısını güvenli bir şekilde yap.
        return safeApiCall { api.getProductById(productId) }
            // 2. Dönen akış üzerinde map operatörünü kullan.
            .map { resource ->
                // 3. Sadece Success durumunda, içindeki DTO'yu Domain modeline çevir.
                resource.mapSuccess { productDetailDto ->
                    productDetailDto.toDomain()
                }
            }
    }
}