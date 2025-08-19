package com.example.mustafakocer.presentation.feature_product_search

import androidx.paging.PagingData
import com.example.mustafakocer.domain.exception.AppException
import com.example.mustafakocer.domain.model.Product

/**
 * Represents the complete, immutable state of the Search screen at any given time.
 * The Fragment will observe this single state object to render the entire UI.
 */
data class SearchUiState(
    val searchQuery: String = "",
    val screenState: ScreenState = ScreenState.Idle // Ekranın ne göstereceğini bu belirler
)

/**
 * Defines the possible states the screen can be in.
 * This sealed class makes state management exhaustive and type-safe.
 */
sealed class ScreenState {
    object Idle : ScreenState()
    object Loading : ScreenState()
    data class Content(val products: PagingData<Product>) : ScreenState()
    object Empty : ScreenState()
    data class Error(val exception: AppException) : ScreenState()
}