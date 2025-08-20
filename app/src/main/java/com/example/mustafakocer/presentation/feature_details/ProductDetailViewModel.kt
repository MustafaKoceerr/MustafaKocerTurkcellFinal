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

/**
 * Manages the UI state and business logic for the Product Detail screen.
 *
 * @param savedStateHandle Injected by Hilt to access navigation arguments, like `productId`.
 */
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
     * A flow that holds the state of the product detail fetching operation.
     * It uses `stateIn` to convert the cold Flow from the use case into a hot StateFlow.
     */
    val productDetailState: StateFlow<Resource<ProductDetail>> =
        getProductDetailUseCase(productId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Resource.Loading
            )

    /**
     * A flow that holds the current quantity of this product in the user's cart.
     */
    val quantityInCart: StateFlow<Int> =
        getCartQuantityUseCase(productId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = 0
            )

    private val _isDescriptionExpanded = MutableStateFlow(false)
    val isDescriptionExpanded: StateFlow<Boolean> = _isDescriptionExpanded.asStateFlow()

    /**
     * Toggles the expanded/collapsed state of the product description.
     */
    fun onToggleDescription() {
        _isDescriptionExpanded.update { !it }
    }

    /**
     * Handles the event when the user clicks the increase quantity button.
     */
    fun onIncreaseClicked() = viewModelScope.launch {
        addOrIncreaseCartItemUseCase(productId)
    }

    /**
     * Handles the event when the user clicks the decrease quantity button.
     */
    fun onDecreaseClicked() = viewModelScope.launch {
        decreaseOrRemoveCartItemUseCase(productId)
    }

    /**
     * Retries fetching the product detail if the initial load failed.
     */
    fun onRetry() {
        // The productDetailState is a self-restarting flow, but if we want to
        // explicitly re-trigger, we would need to adjust the use case or repository.
        // For now, this is a placeholder. A common pattern is to use a trigger Flow.
    }
}