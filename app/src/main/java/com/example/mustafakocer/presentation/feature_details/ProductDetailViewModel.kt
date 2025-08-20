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

@OptIn(ExperimentalCoroutinesApi::class) // flatMapLatest için gerekli
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

    // 1. TETİKLEYİCİ: Artık bir Int sayacı. Her onRetry çağrısında artacak.
    private val retryTrigger = MutableStateFlow(0)

    val productDetailState: StateFlow<Resource<ProductDetail>> =
        retryTrigger.flatMapLatest {
            // retryTrigger her yeni bir değer aldığında (0, 1, 2...),
            // bu blok yeniden çalışacak ve use case'i yeniden tetikleyecek.
            getProductDetailUseCase(productId)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.Loading
        )

    // ... quantityInCart ve isDescriptionExpanded flow'ları aynı kalıyor ...
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
     * Retries fetching the product detail.
     * It increments the retryTrigger, which causes the flatMapLatest to re-execute the use case.
     */
    fun onRetry() {
        // 2. İŞLEVSELLİK: Sayacı bir artır. Değer değiştiği için (örn: 0 -> 1),
        // StateFlow kesinlikle yeni bir değer yayınlayacak ve akışı tetikleyecek.
        retryTrigger.value++
    }
}