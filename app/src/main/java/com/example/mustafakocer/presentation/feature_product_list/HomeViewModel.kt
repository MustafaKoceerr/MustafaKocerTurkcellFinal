package com.example.mustafakocer.presentation.feature_product_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.mustafakocer.domain.model.Product
import com.example.mustafakocer.domain.usecase.GetProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Manages the UI state and business logic for the Home screen.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
) : ViewModel() {

    /**
     * A flow of [PagingData] representing the main list of products.
     * The `.cachedIn(viewModelScope)` operator caches the flow's content, making the data
     * survive configuration changes and keeping the scroll position.
     */
    val productsFlow: Flow<PagingData<Product>> = getProductsUseCase()
        .cachedIn(viewModelScope)
}