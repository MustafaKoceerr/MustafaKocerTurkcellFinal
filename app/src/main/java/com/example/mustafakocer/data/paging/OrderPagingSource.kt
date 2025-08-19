package com.example.mustafakocer.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.mustafakocer.data.model.dto.OrderDto
import com.example.mustafakocer.data.network.DummyApi
import retrofit2.HttpException
import java.io.IOException

/**
 * A [PagingSource] that fetches a user's orders directly from the network.
 * This implementation does not use a local database cache, making it suitable for data
 * that is frequently updated or does not need to be available offline.
 *
 * @param api The Retrofit API service.
 * @param userId The ID of the user whose orders are to be fetched.
 */
class OrderPagingSource(
    private val api: DummyApi,
    private val userId: String
) : PagingSource<Int, OrderDto>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, OrderDto> {
        val page = params.key ?: PAGING_STARTING_PAGE_INDEX

        return try {
            val response = api.getOrdersByUserId(
                userId = userId,
                limit = params.loadSize,
                skip = page * params.loadSize
            )

            val ordersDto = response.body()?.carts ?: emptyList()

            LoadResult.Page(
                data = ordersDto,
                prevKey = if (page == PAGING_STARTING_PAGE_INDEX) null else page - 1,
                nextKey = if (ordersDto.isEmpty()) null else page + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    /**
     * Provides the key for the page to be loaded when the data is refreshed.
     * This ensures a smooth user experience by loading data around the user's
     * current viewport.
     */
    override fun getRefreshKey(state: PagingState<Int, OrderDto>): Int? {
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