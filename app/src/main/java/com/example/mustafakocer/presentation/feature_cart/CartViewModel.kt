package com.example.mustafakocer.presentation.feature_cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mustafakocer.domain.exception.AppException
import com.example.mustafakocer.domain.model.CartItem
import com.example.mustafakocer.domain.usecase.AddOrIncreaseCartItemUseCase
import com.example.mustafakocer.domain.usecase.DecreaseOrRemoveCartItemUseCase
import com.example.mustafakocer.domain.usecase.GetCartItemsUseCase
import com.example.mustafakocer.domain.usecase.GetUserIdUseCase
import com.example.mustafakocer.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val getCartItemsUseCase: GetCartItemsUseCase,
    private val addOrIncreaseCartItemUseCase: AddOrIncreaseCartItemUseCase,
    private val decreaseOrRemoveCartItemUseCase: DecreaseOrRemoveCartItemUseCase,
    private val getUserIdUseCase: GetUserIdUseCase, // YENİ BAĞIMLILIK
) : ViewModel() {

    private val _cartState = MutableStateFlow<Resource<List<CartItem>>>(Resource.Idle)
    val cartState = _cartState.asStateFlow()

    private val _totalPrice = MutableStateFlow("0.00")
    val totalPrice = _totalPrice.asStateFlow()

    // Mevcut kullanıcı ID'sini tutmak için.
    private var currentUserId: String? = null

    init {
        // ViewModel oluşturulduğunda, önce kullanıcı ID'sini al, sonra sepeti dinle.
        viewModelScope.launch {
            // getUserIdUseCase'den gelen Flow'dan ilk değeri (mevcut ID) al.
            // .first() suspend bir fonksiyondur, bu yüzden launch bloğu içindeyiz.
            val userId = getUserIdUseCase().first()
            if (userId != null) {
                currentUserId = userId.toString()
                observeCart(currentUserId!!)
            } else {
                // Kullanıcı ID'si bulunamadıysa, bu bir hata durumudur.
                _cartState.value =
                    Resource.Error(AppException.Data.ValidationError("Kullanıcı oturumu bulunamadı."))
            }
        }
    }

    private fun observeCart(userId: String) {
        getCartItemsUseCase(userId).onEach { resource ->
            _cartState.value = resource
            if (resource is Resource.Success) {
                calculateTotalPrice(resource.data)
            }
        }.launchIn(viewModelScope)
    }

    fun onIncreaseClicked(productId: Int) {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                addOrIncreaseCartItemUseCase(userId, productId)
            }
        }
    }

    fun onDecreaseClicked(productId: Int) {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                decreaseOrRemoveCartItemUseCase(userId, productId)
            }
        }
    }

    private fun calculateTotalPrice(items: List<CartItem>) {
        val total = items.sumOf {
            val priceAsDouble = it.product.discountedPrice
                .replace("$", "")
                .replace(",", "")
                .toDoubleOrNull() ?: 0.0
            priceAsDouble * it.quantity
        }
        _totalPrice.value = String.format("%.2f", total)
    }
}
