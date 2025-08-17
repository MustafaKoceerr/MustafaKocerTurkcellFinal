package com.example.mustafakocer.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.mustafakocer.data.mapper.toDomain
import com.example.mustafakocer.data.network.IDummyApi
import com.example.mustafakocer.data.paging.OrderPagingSource // Henüz oluşturmadık, bir sonraki adımda oluşturacağız.
import com.example.mustafakocer.data.preferences.SessionManager
import com.example.mustafakocer.domain.model.Order
import com.example.mustafakocer.domain.repository.OrderRepository
import com.example.mustafakocer.util.PagingConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val api: IDummyApi,
    private val sessionManager: SessionManager,
) : OrderRepository {

    override fun getPaginatedOrdersByUserId(): Flow<PagingData<Order>> {
        val userId = sessionManager.userId.value

        userId?.let {
            return Pager(
                config = PagingConfig(
                    pageSize = PagingConstants.ORDER_PAGE_SIZE,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = { OrderPagingSource(api = api, userId = userId.toString()) }
            ).flow.map { pagingData ->
                pagingData.map { it.toDomain() }
            }
        }

        return flowOf(PagingData.empty())
    }
}