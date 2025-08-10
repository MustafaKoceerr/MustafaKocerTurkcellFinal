package com.example.mustafakocer.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.mustafakocer.data.mapper.toDomain
import com.example.mustafakocer.data.network.IDummyApi
import com.example.mustafakocer.data.paging.OrderPagingSource // Henüz oluşturmadık, bir sonraki adımda oluşturacağız.
import com.example.mustafakocer.domain.model.Order
import com.example.mustafakocer.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val api: IDummyApi,
    // Bu repository'nin veritabanına ihtiyacı yok, çünkü offline-first yapmıyoruz.
) : OrderRepository {

    override fun getPaginatedOrdersByUserId(userId: String): Flow<PagingData<Order>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                // Bu PagingSource'u bir sonraki adımda oluşturacağız.
                OrderPagingSource(api = api, userId = userId)
            }
        ).flow.map { pagingData ->
            // Gelen PagingData<OrderDto>'yu PagingData<Order>'a çeviriyoruz.
            pagingData.map { orderDto ->
                orderDto.toDomain()
            }
        }
    }

    companion object {
        private const val PAGE_SIZE = 10 // Siparişler için sayfa boyutu
    }
}