package com.example.mustafakocer.presentation.feature_splash.contract

import com.example.mustafakocer.domain.exception.AppException
import com.example.mustafakocer.presentation.mvi.BaseUiEffect
import com.example.mustafakocer.presentation.mvi.BaseUiEvent
import com.example.mustafakocer.presentation.mvi.BaseUiState

data class SplashUiState(
    override val isLoading: Boolean = true,
    // YENİ: Bu bayrak, ViewModel tarafından 'true' yapıldığında
    // UI'ın çıkış animasyonunu başlatmasını tetikleyecek.
    val animateOut: Boolean = false,
    override val error: AppException? = null,
) : BaseUiState

sealed interface SplashEvent : BaseUiEvent {
    // UI, çıkış animasyonu bittiğinde bu olayı ViewModel'a gönderecek.
    data object ExitAnimationFinished : SplashEvent
}

sealed interface SplashEffect : BaseUiEffect {
    data object NavigateToHome : SplashEffect
    data object NavigateToLogin : SplashEffect
}