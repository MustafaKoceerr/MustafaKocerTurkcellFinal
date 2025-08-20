package com.example.mustafakocer.presentation.feature_splash

import androidx.lifecycle.viewModelScope
import com.example.mustafakocer.domain.usecase.CheckAuthStatusUseCase
import com.example.mustafakocer.presentation.base.BaseViewModel
import com.example.mustafakocer.presentation.feature_splash.contract.SplashEffect
import com.example.mustafakocer.presentation.feature_splash.contract.SplashEvent
import com.example.mustafakocer.presentation.feature_splash.contract.SplashUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val checkAuthStatusUseCase: CheckAuthStatusUseCase,
) : BaseViewModel<SplashUiState, SplashEvent, SplashEffect>(
    initialState = SplashUiState()
) {

    /**
     * Temporarily stores the navigation destination, determined after the auth check.
     * This is used to trigger the correct navigation effect after the exit animation completes.
     */
    private var navigationTarget: SplashEffect? = null

    init {
        checkAuthStatus()
    }

    override fun handleEvent(event: SplashEvent) {
        if (event is SplashEvent.ExitAnimationFinished) {
            // When the UI confirms the animation is done, send the stored navigation effect.
            navigationTarget?.let(::sendEffect)
        }
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            // Delay to ensure the splash screen is visible for a minimum duration.
            delay(1100)

            val isLoggedIn = checkAuthStatusUseCase()
            navigationTarget = if (isLoggedIn) {
                SplashEffect.NavigateToHome
            } else {
                SplashEffect.NavigateToLogin
            }

            // Instead of navigating immediately, tell the UI to start its exit animation.
            setState { copy(isLoading = false, animateOut = true) }
        }
    }
}