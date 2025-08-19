package com.example.mustafakocer.presentation.feature_product_search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.cachedIn
import com.example.mustafakocer.domain.mapper.ErrorMapper
import com.example.mustafakocer.domain.usecase.SearchProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchProductsUseCase: SearchProductsUseCase,
    private val errorMapper: ErrorMapper
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        // Arama sorgusu değiştiğinde PagingData akışını tetikle
        viewModelScope.launch {
            _searchQuery
                .debounce(400L)
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    // Paging verisini al, ama henüz UI state'e ekleme
                    searchProductsUseCase(query)
                }
                .cachedIn(viewModelScope)
                .collect { pagingData ->
                    // Yeni PagingData geldiğinde, bunu Content state'ine koy.
                    // Eğer mevcut durum zaten Content ise, sadece PagingData'yı güncelle.
                    // Değilse, yeni bir Content state'i oluştur.
                    val currentState = _uiState.value.screenState
                    if (currentState is ScreenState.Content || _searchQuery.value.length >= SearchProductsUseCase.MIN_QUERY_LENGTH) {
                        _uiState.update { it.copy(screenState = ScreenState.Content(pagingData)) }
                    }
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        // Sorgu değiştiğinde, UI state'ini de anında güncelle.
        // Eğer sorgu kısaysa, hemen Idle durumuna geç.
        if (query.length < SearchProductsUseCase.MIN_QUERY_LENGTH) {
            _uiState.update { it.copy(searchQuery = query, screenState = ScreenState.Idle) }
        } else {
            _uiState.update { it.copy(searchQuery = query) }
        }
    }

    /**
     * Fragment'tan gelen Paging yükleme durumlarını alır ve bunu
     * ScreenState'i güncellemek için kullanır.
     */
    fun onPagingLoadStateChanged(loadStates: CombinedLoadStates, itemCount: Int) {
        // Eğer sorgu kısaysa, Paging'in durumu ne olursa olsun Idle'da kal.
        if (_searchQuery.value.length < SearchProductsUseCase.MIN_QUERY_LENGTH) {
            _uiState.update { it.copy(screenState = ScreenState.Idle) }
            return
        }

        val refreshState = loadStates.refresh

        val newScreenState = when {
            refreshState is LoadState.Loading && itemCount == 0 -> ScreenState.Loading
            refreshState is LoadState.Error && itemCount == 0 -> ScreenState.Error(errorMapper.map(refreshState.error))
            refreshState is LoadState.NotLoading && itemCount == 0 -> ScreenState.Empty
            // Eğer liste doluysa, mevcut Content state'ini koru.
            // Bu, arka planda refresh olurken ekranın yanıp sönmesini engeller.
            _uiState.value.screenState is ScreenState.Content -> _uiState.value.screenState
            else -> ScreenState.Idle // Beklenmedik bir durum için fallback
        }

        _uiState.update { it.copy(screenState = newScreenState) }
    }
}