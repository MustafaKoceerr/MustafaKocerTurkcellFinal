package com.example.mustafakocer.presentation.feature_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mustafakocer.domain.model.ProductDetail
import com.example.mustafakocer.domain.usecase.*
import com.example.mustafakocer.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val getProductDetailUseCase: GetProductDetailUseCase,
    private val getCartQuantityUseCase: GetCartQuantityUseCase,
    private val addOrIncreaseCartItemUseCase: AddOrIncreaseCartItemUseCase,
    private val decreaseOrRemoveCartItemUseCase: DecreaseOrRemoveCartItemUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val productId: Int = savedStateHandle.get<Int>("productId")
        ?: throw IllegalStateException("productId must be passed to ProductDetailViewModel")

    /**
     * A trigger that causes the product detail flow to be re-executed when its value changes.
     */
    private val retryTrigger = MutableStateFlow(0)

    /**
     * A reactive flow for product details. It uses `flatMapLatest` to re-subscribe to the
     * `getProductDetailUseCase` whenever the `retryTrigger` emits a new value.
     */
    val productDetailState: StateFlow<Resource<ProductDetail>> =
        retryTrigger.flatMapLatest {
            getProductDetailUseCase(productId)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.Loading
        )

    val quantityInCart: StateFlow<Int> = getCartQuantityUseCase(productId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _isDescriptionExpanded = MutableStateFlow(false)
    val isDescriptionExpanded: StateFlow<Boolean> = _isDescriptionExpanded.asStateFlow()

    fun onToggleDescription() {
        _isDescriptionExpanded.update { !it }
    }

    fun onIncreaseClicked() = viewModelScope.launch {
        addOrIncreaseCartItemUseCase(productId)
    }

    fun onDecreaseClicked() = viewModelScope.launch {
        decreaseOrRemoveCartItemUseCase(productId)
    }

    /**
     * Retries fetching the product detail by incrementing the retryTrigger,
     * which causes the `flatMapLatest` operator to re-execute the use case.
     */
    fun onRetry() {
        retryTrigger.value++
    }
}