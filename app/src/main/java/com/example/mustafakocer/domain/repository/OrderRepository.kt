package com.example.mustafakocer.domain.repository

import androidx.paging.PagingData
import com.example.mustafakocer.domain.model.Order
import kotlinx.coroutines.flow.Flow

/**
 * Siparişlerle ilgili tüm veri operasyonları için sözleşme (arayüz).
 */
interface OrderRepository {

    fun getPaginatedOrdersByUserId(): Flow<PagingData<Order>>
}