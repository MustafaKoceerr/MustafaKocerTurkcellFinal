package com.example.mustafakocer.presentation.feature_cart

/**
 * CartViewHolder içinde gerçekleşebilecek tüm kullanıcı etkileşimlerini
 * tip-güvenli bir şekilde modelleyen sealed interface.
 */
sealed interface CartEvent {
    data class OnIncrease(val productId: Int) : CartEvent
    data class OnDecrease(val productId: Int) : CartEvent
    data class OnRemove(val productId: Int) : CartEvent
    data class OnProductClick(val productId: Int) : CartEvent
}