package com.example.mustafakocer.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.mustafakocer.data.mapper.toDomain
import com.example.mustafakocer.data.network.DummyApi
import com.example.mustafakocer.data.paging.OrderPagingSource
import com.example.mustafakocer.data.preferences.SessionManager
import com.example.mustafakocer.domain.model.Order
import com.example.mustafakocer.domain.repository.OrderRepository
import com.example.mustafakocer.util.PagingConstants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implements the [OrderRepository] interface.
 * This repository is session-aware; it reacts to changes in the user's login state.
 * It provides a paginated flow of orders for the currently logged-in user.
 */
class OrderRepositoryImpl @Inject constructor(
    private val api: DummyApi,
    private val sessionManager: SessionManager,
) : OrderRepository {

    /**
     * Provides a [Flow] of [PagingData] for the current user's orders.
     * It uses [flatMapLatest] to reactively switch the data source based on the user's
     * session state. If the user logs out, it emits empty data. If they log in, it
     * creates a new [Pager] with the new user ID.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getPaginatedOrdersByUserId(): Flow<PagingData<Order>> =
        sessionManager.userId.flatMapLatest { userId ->
            if (userId == null) {
                flowOf(PagingData.empty())
            } else {
                Pager(
                    config = PagingConfig(
                        pageSize = PagingConstants.ORDER_PAGE_SIZE,
                        enablePlaceholders = false
                    ),
                    pagingSourceFactory = { OrderPagingSource(api, userId.toString()) }
                ).flow.map { pagingData ->
                    pagingData.map { it.toDomain() }
                }
            }
        }
}