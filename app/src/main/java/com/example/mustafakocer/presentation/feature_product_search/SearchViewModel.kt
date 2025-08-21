package com.example.mustafakocer.presentation.feature_product_search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.mustafakocer.domain.model.Product
import com.example.mustafakocer.domain.usecase.SearchProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * Manages the business logic for the product search feature.
 * It exposes a single reactive flow of [PagingData] that updates based on the search query.
 */
@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchProductsUseCase: SearchProductsUseCase,
) : ViewModel() {

    val productsFlow: Flow<PagingData<Product>>
    private val _searchQuery = MutableStateFlow("")

    init {
        productsFlow = _searchQuery
            .debounce(300L)
            .distinctUntilChanged()
            .flatMapLatest { query ->
                if (query.length >= SearchProductsUseCase.MIN_QUERY_LENGTH) {
                    searchProductsUseCase(query)
                } else {
                    MutableStateFlow(PagingData.empty())
                }
            }
            .cachedIn(viewModelScope)
    }

    /**
     * Called by the UI when the search query text changes.
     */
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query.trim()
    }
}