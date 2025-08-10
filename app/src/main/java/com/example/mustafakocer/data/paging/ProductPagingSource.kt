package com.example.mustafakocer.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.mustafakocer.data.mapper.toDomain
import com.example.mustafakocer.data.network.IDummyApi
import com.example.mustafakocer.domain.model.Product
import retrofit2.HttpException
import java.io.IOException

/**
 * Sadece pagination için gerekli, offline first yapılmayacak
 */
class ProductPagingSource(
    private val api: IDummyApi,
    private val query: String
) : PagingSource<Int, Product>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        val page = params.key ?: PAGING_STARTING_PAGE_INDEX

        return try {
            val response = api.searchProducts(
                query = query,
                limit = PAGING_PAGE_SIZE,
                skip = page * PAGING_PAGE_SIZE
            )

            val productsDto = response.body()?.products ?: emptyList()
            val domainProducts = productsDto.map { it.toDomain() }

            LoadResult.Page(
                data = domainProducts,
                prevKey = if (page == PAGING_STARTING_PAGE_INDEX) null else page - 1,
                nextKey = if (productsDto.isEmpty()) null else page + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    companion object {
        /**
         * Bu PagingSource için sayfa numaralandırmasının başlangıç indeksi.
         */
        private const val PAGING_STARTING_PAGE_INDEX = 0

        /**
         * Bu PagingSource'un her istekte API'den kaç ürün çekeceği.
         * Bu değer, sadece ürün arama sayfasının bir iş kuralıdır.
         */
        private const val PAGING_PAGE_SIZE = 20
    }
}