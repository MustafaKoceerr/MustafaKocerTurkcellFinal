package com.example.mustafakocer.presentation.feature_cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mustafakocer.domain.model.CartItem
import com.example.mustafakocer.domain.usecase.*
import com.example.mustafakocer.domain.util.Resource
import com.example.mustafakocer.presentation.common.util.parsePriceToDouble
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * Manages the UI state and business logic for the Cart screen.
 * It observes cart items, handles user actions, and calculates the total price.
 */
@HiltViewModel
class CartViewModel @Inject constructor(
    private val getCartItemsUseCase: GetCartItemsUseCase,
    private val addOrIncreaseCartItemUseCase: AddOrIncreaseCartItemUseCase,
    private val decreaseOrRemoveCartItemUseCase: DecreaseOrRemoveCartItemUseCase,
    private val clearCartUseCase: ClearCartUseCase,
    private val removeCartItemUseCase: RemoveCartItemUseCase,
) : ViewModel() {

    private val _cartState = MutableStateFlow<Resource<List<CartItem>>>(Resource.Loading)
    val cartState: StateFlow<Resource<List<CartItem>>> = _cartState.asStateFlow()

    private val _totalPrice = MutableStateFlow(0.0)
    val totalPrice: StateFlow<Double> = _totalPrice.asStateFlow()

    init {
        observeCart()
    }

    private fun observeCart() {
        getCartItemsUseCase().onEach { resource ->
            _cartState.value = resource
            when (resource) {
                is Resource.Success -> calculateTotalPrice(resource.data)
                // 3. Hata durumunda toplam fiyat 0.0 olarak ayarlandı.
                is Resource.Error -> _totalPrice.value = 0.0
                else -> { /* No-op for Loading/Idle */
                }
            }
        }.launchIn(viewModelScope)
    }

    fun onIncreaseClicked(productId: Int) = viewModelScope.launch {
        addOrIncreaseCartItemUseCase(productId)
    }

    fun onDecreaseClicked(productId: Int) = viewModelScope.launch {
        decreaseOrRemoveCartItemUseCase(productId)
    }

    fun onRemoveItemConfirmed(productId: Int) = viewModelScope.launch {
        removeCartItemUseCase(productId)
    }

    fun onClearCartConfirmed() = viewModelScope.launch {
        clearCartUseCase()
    }

    private fun calculateTotalPrice(items: List<CartItem>) {
        val total = items.sumOf {
            it.product.discountedPrice.parsePriceToDouble() * it.quantity
        }
        _totalPrice.value = total
    }
}