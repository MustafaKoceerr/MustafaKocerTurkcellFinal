package com.example.mustafakocer.presentation.feature_product_search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.mustafakocer.domain.model.Product
import com.example.mustafakocer.domain.usecase.SearchProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import retrofit2.http.Query
import javax.inject.Inject

@HiltViewModel
@ExperimentalCoroutinesApi
class SearchViewModel @Inject constructor(
    private val searchProductsUseCase: SearchProductsUseCase,
) : ViewModel() {

    // Arama sorgusunu tutacak bir stateFlow
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // PagingData akışını tutacak olan ana StateFlow.
    val products: Flow<PagingData<Product>> = _searchQuery
        .flatMapLatest { query ->
            searchProductsUseCase(query)
        }
        // cachedIn, PagingData'yi viewModelScope'ta önbelleğe alır.
        // Bu ekran döndürme gibi konfig değişikliklerinde verinin kaybolmasını önşer
        .cachedIn(viewModelScope)

    /**
     * Ui'dan (SearchView) Gelen yeni arama sorgusunu günceller
     */
    fun onSearchQueryChanged(query: String){
        _searchQuery.value = query
    }

}