package com.example.mustafakocer.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.mustafakocer.data.mapper.toDomain
import com.example.mustafakocer.data.network.DummyApi
import com.example.mustafakocer.domain.model.Product
import retrofit2.HttpException
import java.io.IOException

/**
 * A [PagingSource] for fetching product search results directly from the network.
 * It takes a search query and provides paginated [Product] domain models.
 * This implementation does not use a local database cache.
 *
 * @param api The Retrofit API service.
 * @param query The search term to filter products by.
 */
class ProductPagingSource(
    private val api: DummyApi,
    private val query: String
) : PagingSource<Int, Product>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        val page = params.key ?: PAGING_STARTING_PAGE_INDEX

        return try {
            val response = api.searchProducts(
                query = query,
                limit = params.loadSize, // Use flexible page size from PagingConfig
                skip = page * params.loadSize
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

    /**
     * Provides the key for the page to be loaded when the data is refreshed.
     */
    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    companion object {
        /**
         * The starting page index for pagination.
         */
        private const val PAGING_STARTING_PAGE_INDEX = 0
    }
}