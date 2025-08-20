package com.example.mustafakocer.domain.usecase

import androidx.paging.PagingData
import com.example.mustafakocer.domain.model.Order
import com.example.mustafakocer.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Encapsulates the business logic for retrieving the paginated list of the current user's orders.
 * This use case is session-aware, meaning it will automatically react to login/logout events
 * because it relies on a reactive repository.
 */
class GetOrdersUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    /**
     * Executes the use case.
     * @return A [Flow] of [PagingData] containing the user's orders.
     */
    operator fun invoke(): Flow<PagingData<Order>> =
        orderRepository.getPaginatedOrdersByUserId()
}