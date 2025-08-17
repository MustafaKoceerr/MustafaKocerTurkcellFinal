package com.example.mustafakocer.domain.usecase

import androidx.paging.PagingData
import com.example.mustafakocer.domain.model.Order
import com.example.mustafakocer.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Kullanıcının siparişlerinin sayfalama destekli listesini getirme iş kuralını kapsüller.
 */
class GetOrdersUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    /**
     * @param userId Siparişleri getirilecek kullanıcının ID'si.
     * @return Kullanıcının siparişlerini içeren bir PagingData akışı.
     */
    operator fun invoke(): Flow<PagingData<Order>> {
        return orderRepository.getPaginatedOrdersByUserId()
    }
}