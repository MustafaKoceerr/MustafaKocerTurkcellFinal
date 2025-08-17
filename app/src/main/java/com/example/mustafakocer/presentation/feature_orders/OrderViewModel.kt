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

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val getOrdersUseCase: GetOrdersUseCase,
) : ViewModel() {

    // `ordersFlow` artık doğrudan UseCase'i çağırıyor.
    // UseCase, hangi userId'yi kullanacağını SessionManager sayesinde biliyor.
    val ordersFlow: Flow<PagingData<Order>> = getOrdersUseCase()
        // PagingData'yı ViewModelScope'ta önbelleğe al.
        // Bu, ekran döndüğünde veya konfigürasyon değiştiğinde verinin kaybolmasını önler.
        .cachedIn(viewModelScope)

}