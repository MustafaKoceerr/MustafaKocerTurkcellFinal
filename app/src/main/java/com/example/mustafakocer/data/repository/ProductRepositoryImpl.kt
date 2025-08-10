package com.example.mustafakocer.data.repository

import CategoryProductRemoteMediator
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.mustafakocer.data.db.AppDatabase
import com.example.mustafakocer.data.mapper.toDomain
import com.example.mustafakocer.data.model.dto.ProductsResponseDto
import com.example.mustafakocer.data.network.IDummyApi
import com.example.mustafakocer.data.network.util.safeApiCall
import com.example.mustafakocer.data.paging.ProductPagingSource
import com.example.mustafakocer.data.paging.ProductRemoteMediator
import com.example.mustafakocer.domain.model.Category
import com.example.mustafakocer.domain.model.Product
import com.example.mustafakocer.domain.repository.ProductRepository
import com.example.mustafakocer.domain.util.Resource
import com.example.mustafakocer.domain.util.mapSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.Response
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val api: IDummyApi,
    private val db: AppDatabase,
) : ProductRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getPaginatedProducts(): Flow<PagingData<Product>> {
        val pagingSourceFactory = { db.createProductDao().getProductsPagingSource() }

        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            remoteMediator = ProductRemoteMediator(api, db),
            pagingSourceFactory = pagingSourceFactory
        ).flow.map { pagingData ->
            pagingData.map { productEntity ->
                productEntity.toDomain() // Entity -> Domain
            }
        }
    }


    // ... mevcut fonksiyonlar

    @OptIn(ExperimentalPagingApi::class)
    override fun getPaginatedProductsByCategory(categoryName: String): Flow<PagingData<Product>> {
        // PagingSource'un, veritabanından SADECE ilgili kategorinin ürünlerini çekmesini sağlıyoruz.
        val pagingSourceFactory =
            { db.createProductDao().getProductsByCategoryPagingSource(categoryName) }

        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            // RemoteMediator'a hangi kategori için çalıştığını bildiriyoruz.
            remoteMediator = CategoryProductRemoteMediator(
                categoryName = categoryName,
                api = api,
                db = db
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow.map { pagingData ->
            pagingData.map { productEntity ->
                productEntity.toDomain() // Entity -> Domain dönüşümü
            }
        }
    }

    override fun searchPaginatedProducts(query: String): Flow<PagingData<Product>> {
        // Pager, paging3'ün ana giriş noktasıdır.
        return Pager(
            // PaginConfig, sayfalama davranışını yapılandırır.
            config = PagingConfig(
                pageSize = PAGE_SIZE, // Her sayfada kaç öğe olacağı.
                enablePlaceholders = false // Yüklenmemiş öğeler için yer tutucu gösterilip gösterilmeyeceği.
            ),
            // pagingSourceFactory, Paging 3'e veriyi nasıl çekeceğini söyleyen
            // PagingSource'umuzun yeni bir örneğini oluşturan bir lambda'dır.
            pagingSourceFactory = {
                ProductPagingSource(api = api, query = query)
            }
        ).flow // Pager'ı bir Flow<PagingData<Product>> akışına dönüştürür.        )
    }

    companion object {
        // PagingConfig'in bu değere ihtiyacı olduğu için, sabiti
        // PagingSource'tan buraya taşımak daha mantıklıdır.
        private const val PAGE_SIZE = 20
    }

    /**
     * Tekrarlanan ürün getirme ve DTO'dan Domain'e çevirme mantığını
     * tek bir özel fonksiyonda topladık (DRY Prensibi).
     */
    private fun fetchProductsFromApi(
        apiCall: suspend () -> Response<ProductsResponseDto>,
    ): Flow<Resource<List<Product>>> {
        return safeApiCall(apiCall).map { resource ->
            // Artık karmaşık bir 'when' bloğuna gerek yok!
            // Sadece 'mapSuccess'i çağırıyoruz.
            resource.mapSuccess { productsResponseDto ->
                // Bu blok SADECE VE SADECE 'resource' bir 'Success' ise çalışır.
                // 'productsResponseDto' artık doğrudan 'data'nın kendisidir.
                productsResponseDto.products.map { dto ->
                    dto.toDomain()
                }
            }
        }
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
}