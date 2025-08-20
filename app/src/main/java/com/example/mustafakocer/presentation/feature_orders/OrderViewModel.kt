package com.example.mustafakocer.presentation.feature_orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.mustafakocer.domain.model.Order
import com.example.mustafakocer.domain.usecase.GetOrdersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Manages the UI state and business logic for the Orders screen.
 */
@HiltViewModel
class OrderViewModel @Inject constructor(
    private val getOrdersUseCase: GetOrdersUseCase,
) : ViewModel() {

    /**
     * A flow of [PagingData] representing the user's orders.
     *
     * The `.cachedIn(viewModelScope)` operator is crucial for Paging 3. It caches the
     * content of the Flow in the ViewModel's scope, making the data survive
     * configuration changes (like screen rotations) and keeping the scroll position.
     */
    val ordersFlow: Flow<PagingData<Order>> = getOrdersUseCase()
        .cachedIn(viewModelScope)
}