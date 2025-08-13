package com.example.mustafakocer.presentation.feature_cart

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mustafakocer.domain.exception.AppException
import com.example.mustafakocer.domain.model.CartItem
import com.example.mustafakocer.domain.usecase.AddOrIncreaseCartItemUseCase
import com.example.mustafakocer.domain.usecase.ClearCartUseCase
import com.example.mustafakocer.domain.usecase.DecreaseOrRemoveCartItemUseCase
import com.example.mustafakocer.domain.usecase.GetCartItemsUseCase
import com.example.mustafakocer.domain.usecase.GetUserIdUseCase
import com.example.mustafakocer.domain.usecase.RemoveCartItemUseCase
import com.example.mustafakocer.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val getCartItemsUseCase: GetCartItemsUseCase,
    private val addOrIncreaseCartItemUseCase: AddOrIncreaseCartItemUseCase,
    private val decreaseOrRemoveCartItemUseCase: DecreaseOrRemoveCartItemUseCase,
    private val getUserIdUseCase: GetUserIdUseCase,
    private val clearCartUseCase: ClearCartUseCase,
    private val removeCartItemUseCase: RemoveCartItemUseCase
) : ViewModel() {

    // State 1: Detaylı sepet listesi (Sadece bu ViewModel'in yönettiği ana state).
    private val _cartState = MutableStateFlow<Resource<List<CartItem>>>(Resource.Loading)
    val cartState: StateFlow<Resource<List<CartItem>>> = _cartState.asStateFlow()

    // State 2: Toplam fiyat (cartState'ten türetilen bir state).
    private val _totalPrice = MutableStateFlow("$0.00")
    val totalPrice: StateFlow<String> = _totalPrice.asStateFlow()

    // SİLİNDİ: _cartMap ve updateCartMap fonksiyonları artık gereksiz.

    private var currentUserId: String? = null

    init {
        viewModelScope.launch {
            val userId = getUserIdUseCase().first()
            if (userId != null) {
                currentUserId = userId.toString()
                observeCart(currentUserId!!)
            } else {
                _cartState.value = Resource.Error(AppException.Api.Unauthorized(null))
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

    // YENİ FONKSİYON: Fragment'tan gelen "Kaldır" olayını işler.
    fun onRemoveItemConfirmed(productId: Int) {
        currentUserId?.let { userId ->
            // Bu bir suspend fonksiyon olduğu için coroutine içinde çağırıyoruz.
            viewModelScope.launch {
                // İlgili UseCase'i çağırarak iş kuralını tetikliyoruz.
                removeCartItemUseCase(userId, productId)
                // Not: Burada dönen sonucu (Resource) işlememize gerek yok.
                // Çünkü `observeCart` metodu Firebase'deki değişikliği zaten dinliyor
                // ve UI'ı otomatik olarak güncelleyecektir. Bu, reaktif programlamanın gücüdür.
            }
        }
    }

    fun onClearCartConfirmed() {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                clearCartUseCase(userId)
            }
        }
    }

    private fun calculateTotalPrice(items: List<CartItem>) {
        val total = items.sumOf {
            val priceAsString = it.product.discountedPrice
                .replace("$", "")
                .replace(",", "")
            val priceAsDouble = priceAsString.toDoubleOrNull() ?: 0.0
            priceAsDouble * it.quantity
        }
        val priceFormat = DecimalFormat("$#,##0.00")
        _totalPrice.value = priceFormat.format(total)
    }
}