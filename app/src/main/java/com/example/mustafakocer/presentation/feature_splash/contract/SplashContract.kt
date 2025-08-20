package com.example.mustafakocer.presentation.feature_splash.contract

import com.example.mustafakocer.domain.exception.AppException
import com.example.mustafakocer.presentation.mvi.BaseUiEffect
import com.example.mustafakocer.presentation.mvi.BaseUiEvent
import com.example.mustafakocer.presentation.mvi.BaseUiState

data class SplashUiState(
    override val isLoading: Boolean = true,
    /**
     * A flag that, when set to true by the ViewModel, triggers the UI
     * to start its exit animation.
     */
    val animateOut: Boolean = false,
    override val error: AppException? = null,
) : BaseUiState

sealed interface SplashEvent : BaseUiEvent {
    /**
     * Sent by the UI to the ViewModel after the exit animation has completed.
     */
    object ExitAnimationFinished : SplashEvent
}

sealed interface SplashEffect : BaseUiEffect {
    object NavigateToHome : SplashEffect
    object NavigateToLogin : SplashEffect
}