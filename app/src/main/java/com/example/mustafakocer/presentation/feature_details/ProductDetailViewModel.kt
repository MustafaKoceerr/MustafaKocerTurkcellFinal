package com.example.mustafakocer.presentation.feature_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mustafakocer.domain.model.ProductDetail
import com.example.mustafakocer.domain.usecase.*
import com.example.mustafakocer.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val getProductDetailUseCase: GetProductDetailUseCase,
    // YENİ: Sepetle ilgili UseCase'ler
    private val getCartQuantityUseCase: GetCartQuantityUseCase,
    private val addOrIncreaseCartItemUseCase: AddOrIncreaseCartItemUseCase,
    private val decreaseOrRemoveCartItemUseCase: DecreaseOrRemoveCartItemUseCase,
    private val getUserIdUseCase: GetUserIdUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _productDetailState = MutableStateFlow<Resource<ProductDetail>>(Resource.Loading)
    val productDetailState: StateFlow<Resource<ProductDetail>> = _productDetailState.asStateFlow()

    private val _quantityInCart = MutableStateFlow(0)
    val quantityInCart: StateFlow<Int> = _quantityInCart.asStateFlow()

    private val productId: Int = savedStateHandle.get<Int>("productId")!!

    // YENİ: Açıklamanın durumunu tutan StateFlow
    private val _isDescriptionExpanded = MutableStateFlow(false)
    val isDescriptionExpanded: StateFlow<Boolean> = _isDescriptionExpanded.asStateFlow()

    fun onToggleDescription() {
        _isDescriptionExpanded.value = !_isDescriptionExpanded.value
    }

    init {
        getProductDetail()
        observeCartQuantity()
    }

    fun getProductDetail() {
        getProductDetailUseCase(productId).onEach { resource ->
            _productDetailState.value = resource
        }.launchIn(viewModelScope)
    }

    private fun observeCartQuantity() {
        viewModelScope.launch {
            // Önce kullanıcı ID'sini al
            val userId = getUserIdUseCase().first()?.toString()
            if (userId != null) {
                // Sonra bu kullanıcı ve ürün için miktarı dinlemeye başla
                getCartQuantityUseCase(userId, productId).collect { quantity ->
                    _quantityInCart.value = quantity
                }
            }
        }
    }

    fun onIncreaseClicked() {
        viewModelScope.launch {
            val userId = getUserIdUseCase().first()?.toString()
            userId?.let {
                addOrIncreaseCartItemUseCase(it, productId)
            }
        }
    }

    fun onDecreaseClicked() {
        viewModelScope.launch {
            val userId = getUserIdUseCase().first()?.toString()
            userId?.let {
                decreaseOrRemoveCartItemUseCase(it, productId)
            }
        }
    }
}