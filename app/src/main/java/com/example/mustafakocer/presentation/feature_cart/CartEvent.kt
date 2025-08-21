package com.example.mustafakocer.presentation.feature_cart

import com.example.mustafakocer.presentation.mvi.BaseUiEvent

/**
 * A sealed interface that models all possible user interactions (Events) within the Cart feature.
 * This provides a type-safe way for the UI to communicate user actions to the ViewModel.
 */
sealed interface CartEvent : BaseUiEvent {
    data class OnIncrease(val productId: Int) : CartEvent
    data class OnDecrease(val productId: Int) : CartEvent
    data class OnRemove(val productId: Int) : CartEvent
    data class OnProductClick(val productId: Int) : CartEvent
}