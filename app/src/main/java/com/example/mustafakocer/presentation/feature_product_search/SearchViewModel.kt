// com/example/mustafakocer/presentation/feature_product_search/SearchViewModel.kt (Refactor Edilmiş Hali)
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchProductsUseCase: SearchProductsUseCase,
) : ViewModel() {

    // 1. UI'ın dinleyeceği tek ve ana veri akışı bu olacak.
    val productsFlow: Flow<PagingData<Product>>

    // 2. Arama sorgusunu tutan ve arama mantığını tetikleyen StateFlow.
    private val _searchQuery = MutableStateFlow("")

    init {
        productsFlow = _searchQuery
            .debounce(300L) // Kullanıcı yazmayı bırakınca 300ms bekle
            .distinctUntilChanged() // Aynı sorguyu tekrar gönderme
            .flatMapLatest { query ->
                // Sadece sorgu yeterli uzunluktaysa use case'i çağır.
                // Değilse, boş bir PagingData akışı döndür.
                if (query.length >= SearchProductsUseCase.MIN_QUERY_LENGTH) {
                    searchProductsUseCase(query)
                } else {
                    MutableStateFlow(PagingData.empty())
                }
            }
            .cachedIn(viewModelScope) // Sonuçları scope içinde cache'le
    }

    /**
     * Fragment'tan çağrılacak olan metod.
     * Kullanıcı arama kutusuna bir şey yazdığında bu tetiklenir.
     */
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query.trim()
    }
}