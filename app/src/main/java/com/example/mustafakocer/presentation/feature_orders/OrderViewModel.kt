package com.example.mustafakocer.presentation.feature_orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.mustafakocer.domain.model.Order
import com.example.mustafakocer.domain.usecase.GetOrdersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val getOrdersUseCase: GetOrdersUseCase
): ViewModel(){

    // Siparişleri istenen kullanıcı ID'sini tutacak bir StateFlow.
    private val _userId = MutableStateFlow<String?>(null)

    // Kullanıcı ID'si değiştikçe, yeni PagingData akışını tetikleyecek olan ana Flow.
    @OptIn(ExperimentalCoroutinesApi::class)
    val ordersFlow: Flow<PagingData<Order>> = _userId
        .flatMapLatest { id->
            if (!id.isNullOrBlank()) {
                getOrdersUseCase(id)
            } else {
                // Eğer ID yoksa, boş bir PagingData akışı döndür.
                flowOf(PagingData.empty())
            }
        }
        // PagingData'yı ViewModelScope'ta önbelleğe al.
        .cachedIn(viewModelScope)

    /**
     * UI'dan (Fragment) gelen kullanıcı ID'sini güncelleyerek sipariş akışını tetikler.
     */
    fun onUserIdSet(userId: String) {
        _userId.value = userId
    }
}