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
    private val getCartQuantityUseCase: GetCartQuantityUseCase,
    private val addOrIncreaseCartItemUseCase: AddOrIncreaseCartItemUseCase,
    private val decreaseOrRemoveCartItemUseCase: DecreaseOrRemoveCartItemUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _productDetailState = MutableStateFlow<Resource<ProductDetail>>(Resource.Loading)
    val productDetailState: StateFlow<Resource<ProductDetail>> = _productDetailState.asStateFlow()

    private val _quantityInCart = MutableStateFlow(0)
    val quantityInCart: StateFlow<Int> = _quantityInCart.asStateFlow()

    private val _isDescriptionExpanded = MutableStateFlow(false)
    val isDescriptionExpanded: StateFlow<Boolean> = _isDescriptionExpanded.asStateFlow()

    // productId'yi sınıf seviyesinde bir değişkende tutmaya devam ediyoruz.
    private val productId: Int = savedStateHandle.get<Int>("productId")
        ?: throw IllegalStateException("productId must be passed to ProductDetailViewModel")

    init {
        getProductDetail()
        observeCartQuantity()
    }

    fun onToggleDescription() {
        _isDescriptionExpanded.value = !_isDescriptionExpanded.value
    }

    fun getProductDetail() {
        getProductDetailUseCase(productId).onEach { resource ->
            _productDetailState.value = resource
        }.launchIn(viewModelScope)
    }

    private fun observeCartQuantity() {
        // Artık userId'yi beklemeye gerek yok, doğrudan miktarı dinliyoruz.
        // UseCase, oturum kapalıysa 0 döndürecek şekilde güncellenmelidir.
        getCartQuantityUseCase(productId).onEach { quantity ->
            _quantityInCart.value = quantity
        }.launchIn(viewModelScope)
    }

    fun onIncreaseClicked() {
        // Artık userId'yi almamıza gerek yok.
        viewModelScope.launch {
            addOrIncreaseCartItemUseCase(productId)
        }
    }

    fun onDecreaseClicked() {
        // Artık userId'yi almamıza gerek yok.
        viewModelScope.launch {
            decreaseOrRemoveCartItemUseCase(productId)
        }
    }
}