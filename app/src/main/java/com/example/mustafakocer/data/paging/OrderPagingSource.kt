package com.example.mustafakocer.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.mustafakocer.data.model.dto.OrderDto
import com.example.mustafakocer.data.network.IDummyApi
import retrofit2.HttpException
import java.io.IOException

class OrderPagingSource(
    private val api: IDummyApi,
    private val userId: String
) : PagingSource<Int, OrderDto>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, OrderDto> {
        // Yüklenecek sayfa numarasını belirle. İlk başta null ise, başlangıç sayfasını kullan.
        val page = params.key ?: PAGING_STARTING_PAGE_INDEX

        return try {
            // API'ye isteği at. Skip parametresi ile hangi sayfada olduğumuzu belirtiyoruz.
            val response = api.getOrdersByUserId(
                userId = userId,
                limit = params.loadSize, // PagingConfig'den gelen sayfa boyutunu kullan.
                skip = page * params.loadSize
            )

            val ordersDto = response.body()?.carts ?: emptyList()

            // Dönen listeye göre bir sonraki ve bir önceki anahtarları belirle.
            LoadResult.Page(
                data = ordersDto,
                prevKey = if (page == PAGING_STARTING_PAGE_INDEX) null else page - 1,
                nextKey = if (ordersDto.isEmpty()) null else page + 1
            )
        } catch (e: IOException) {
            // İnternet bağlantısı yok gibi ağ hataları
            LoadResult.Error(e)
        } catch (e: HttpException) {
            // 404, 500 gibi HTTP hata kodları
            LoadResult.Error(e)
        }
    }

    /**
     * Liste yenilendiğinde (refresh) hangi sayfadan başlanacağını belirler.
     * Genellikle en yakın sayfadan devam etmesi sağlanır.
     */
    override fun getRefreshKey(state: PagingState<Int, OrderDto>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    companion object {
        private const val PAGING_STARTING_PAGE_INDEX = 0
    }
}