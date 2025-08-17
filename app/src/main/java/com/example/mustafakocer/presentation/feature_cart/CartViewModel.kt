package com.example.mustafakocer.presentation.feature_cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mustafakocer.domain.exception.AppException
import com.example.mustafakocer.domain.model.CartItem
import com.example.mustafakocer.domain.usecase.AddOrIncreaseCartItemUseCase
import com.example.mustafakocer.domain.usecase.ClearCartUseCase
import com.example.mustafakocer.domain.usecase.DecreaseOrRemoveCartItemUseCase
import com.example.mustafakocer.domain.usecase.GetCartItemsUseCase
import com.example.mustafakocer.domain.usecase.RemoveCartItemUseCase
import com.example.mustafakocer.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val clearCartUseCase: ClearCartUseCase,
    private val removeCartItemUseCase: RemoveCartItemUseCase
    // GetUserIdUseCase bağımlılığı kaldırıldı.
) : ViewModel() {

    // State 1: Detaylı sepet listesi.
    private val _cartState = MutableStateFlow<Resource<List<CartItem>>>(Resource.Loading)
    val cartState: StateFlow<Resource<List<CartItem>>> = _cartState.asStateFlow()

    // State 2: Toplam fiyat.
    private val _totalPrice = MutableStateFlow("$0.00")
    val totalPrice: StateFlow<String> = _totalPrice.asStateFlow()

    // `currentUserId` değişkeni kaldırıldı.

    init {
        // Artık userId'yi beklemeye gerek yok, doğrudan sepeti dinlemeye başlıyoruz.
        observeCart()
    }

    private fun observeCart() {
        // UseCase artık parametre almıyor.
        getCartItemsUseCase().onEach { resource ->
            _cartState.value = resource
            if (resource is Resource.Success) {
                calculateTotalPrice(resource.data)
            } else if (resource is Resource.Error) {
                // Eğer sepeti alırken bir hata oluşursa (örn: oturum kapalı),
                // toplam fiyatı sıfırla.
                _totalPrice.value = "$0.00"
            }
        }.launchIn(viewModelScope)
    }

    fun onIncreaseClicked(productId: Int) {
        // Artık `currentUserId` kontrolüne gerek yok.
        viewModelScope.launch {
            addOrIncreaseCartItemUseCase(productId)
        }
    }

    fun onDecreaseClicked(productId: Int) {
        // Artık `currentUserId` kontrolüne gerek yok.
        viewModelScope.launch {
            decreaseOrRemoveCartItemUseCase(productId)
        }
    }

    fun onRemoveItemConfirmed(productId: Int) {
        // Artık `currentUserId` kontrolüne gerek yok.
        viewModelScope.launch {
            removeCartItemUseCase(productId)
        }
    }

    fun onClearCartConfirmed() {
        // Artık `currentUserId` kontrolüne gerek yok.
        viewModelScope.launch {
            clearCartUseCase()
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