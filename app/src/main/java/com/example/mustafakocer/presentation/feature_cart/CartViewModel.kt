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
    private val getUserIdUseCase: GetUserIdUseCase,
) : ViewModel() {

    // State 1: Detaylı sepet listesi (CartFragment için)
    private val _cartState = MutableStateFlow<Resource<List<CartItem>>>(Resource.Idle)
    val cartState = _cartState.asStateFlow()

    // State 2: productId -> quantity haritası (Diğer fragment'lar için)
    private val _cartMap = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val cartMap = _cartMap.asStateFlow()

    // YENİ: Toplam fiyatı string olarak tutacak StateFlow.
    private val _totalPrice = MutableStateFlow("0.00")
    val totalPrice = _totalPrice.asStateFlow()

    // Mevcut kullanıcı ID'sini tekrar tekrar çekmemek için saklayalım.
    private var currentUserId: String? = null

    init {
        // ViewModel oluşturulur oluşturulmaz, kullanıcı ID'sini alıp sepeti dinlemeye başla.
        viewModelScope.launch {
            // .first() ile Flow'dan o anki kullanıcı ID'sini tek seferlik alıyoruz.
            val userId = getUserIdUseCase().first()
            if (userId != null) {
                currentUserId = userId.toString()
                observeCart(currentUserId!!)
            } else {
                // Eğer kullanıcı ID'si yoksa (bir hata veya oturum kapalıysa),
                // UI'a gösterilecek bir hata durumu yayınla.
                _cartState.value = Resource.Error(AppException.Api.Unauthorized(null))
            }
        }
    }

    /**
     * Verilen kullanıcı ID'si için sepeti dinlemeye başlar ve her değişiklikte
     * hem `cartState`'i hem de `cartMap`'i günceller.
     */
    private fun observeCart(userId: String) {
        getCartItemsUseCase(userId).onEach { resource ->
            _cartState.value = resource
            if (resource is Resource.Success) {
                updateCartMap(resource.data)
                calculateTotalPrice(resource.data) // YENİ: Toplam fiyatı hesapla.
            }
        }.launchIn(viewModelScope)
    }

    /**
     * UI'dan gelen "+" tıklama olayını yönetir.
     */
    fun onIncreaseClicked(productId: Int) {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                // UseCase'i çağır. Sonucu dinlememize gerek yok,
                // çünkü `observeCart` zaten Firebase'deki değişikliği yakalayıp state'i güncelleyecek.
                addOrIncreaseCartItemUseCase(userId, productId)
            }
        }
    }

    /**
     * UI'dan gelen "-" tıklama olayını yönetir.
     */
    fun onDecreaseClicked(productId: Int) {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                decreaseOrRemoveCartItemUseCase(userId, productId)
            }
        }
    }

    /**
     * Gelen CartItem listesini, Map<productId, quantity> formatına çevirir.
     */
    private fun updateCartMap(items: List<CartItem>) {
        // Listeyi, product.id'yi anahtar, quantity'yi değer olarak alan bir haritaya dönüştür.
        _cartMap.value = items.associate { it.product.id to it.quantity }
    }

    // YENİ: Gelen sepet listesine göre toplam fiyatı hesaplayan fonksiyon.
    private fun calculateTotalPrice(items: List<CartItem>) {
        val total = items.sumOf {
            val priceAsString = it.product.discountedPrice
                .replace("$", "")
                .replace(",", "") // Binlik ayırıcıyı kaldır

            val priceAsDouble = priceAsString.toDoubleOrNull() ?: 0.0
            priceAsDouble * it.quantity
        }
        // Sonucu iki ondalık basamaklı bir string'e formatla.
        _totalPrice.value = String.format("%.2f", total)
    }
}