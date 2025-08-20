package com.example.mustafakocer.presentation.feature_cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mustafakocer.domain.model.CartItem
import com.example.mustafakocer.domain.usecase.*
import com.example.mustafakocer.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import javax.inject.Inject

/**
 * A private extension function to safely parse a formatted price string into a Double.
 * This centralizes the parsing logic to avoid repetition.
 */
private fun String.parsePriceToDouble(): Double {
    return this.replace(Regex("[$,₺]"), "").replace(",", "").toDoubleOrNull() ?: 0.0
}

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
    private val removeCartItemUseCase: RemoveCartItemUseCase
) : ViewModel() {

    private val _cartState = MutableStateFlow<Resource<List<CartItem>>>(Resource.Loading)
    val cartState: StateFlow<Resource<List<CartItem>>> = _cartState.asStateFlow()

    private val _totalPrice = MutableStateFlow("₺0.00")
    val totalPrice: StateFlow<String> = _totalPrice.asStateFlow()

    /**
     * A single, reusable formatter instance to improve performance.
     */
    private val priceFormatter = DecimalFormat("₺#,##0.00")

    init {
        observeCart()
    }

    private fun observeCart() {
        getCartItemsUseCase().onEach { resource ->
            _cartState.value = resource
            when (resource) {
                is Resource.Success -> calculateTotalPrice(resource.data)
                is Resource.Error -> _totalPrice.value = "₺0.00"
                else -> { /* No-op for Loading/Idle */ }
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
        _totalPrice.value = priceFormatter.format(total)
    }
}