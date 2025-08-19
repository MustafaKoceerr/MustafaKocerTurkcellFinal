package com.example.mustafakocer.domain.repository

import androidx.paging.PagingData
import com.example.mustafakocer.domain.model.Order
import kotlinx.coroutines.flow.Flow

/**
 * A contract for the data layer to handle all order-related data operations.
 */
interface OrderRepository {

    /**
     * Retrieves a paginated stream of orders for the currently logged-in user.
     * @return A flow of [PagingData] containing the user's [Order]s.
     */
    fun getPaginatedOrdersByUserId(): Flow<PagingData<Order>>
}