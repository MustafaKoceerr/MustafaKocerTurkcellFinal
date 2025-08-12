package com.example.mustafakocer.presentation.feature_splash

import androidx.lifecycle.viewModelScope
import com.example.mustafakocer.domain.usecase.CheckAuthStatusUseCase
import com.example.mustafakocer.presentation.base.BaseViewModel
import com.example.mustafakocer.presentation.feature_splash.contract.SplashEffect
import com.example.mustafakocer.presentation.feature_splash.contract.SplashEvent
import com.example.mustafakocer.presentation.feature_splash.contract.SplashUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val checkAuthStatusUseCase: CheckAuthStatusUseCase,
) : BaseViewModel<SplashUiState, SplashEvent, SplashEffect>(
    initialState = SplashUiState()
) {
    // YENİ: Gidilecek hedefi geçici olarak saklamak için.
    private val _navigationTarget = MutableStateFlow<SplashEffect?>(null)

    init {
        checkAuthStatus()
    }

    override fun onEvent(event: SplashEvent) {
        when (event) {
            // DEĞİŞTİ: UI "animasyon bitti" dediğinde, sakladığımız hedefi Effect olarak gönder.
            SplashEvent.ExitAnimationFinished -> {
                _navigationTarget.value?.let { target ->
                    sendEffect(target)
                }
            }
        }
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            delay(1100) // Logo ve animasyonun görünmesi için bekleme süresi

            val isLoggedIn = checkAuthStatusUseCase()
            val targetEffect = if (isLoggedIn) {
                SplashEffect.NavigateToHome
            } else {
                SplashEffect.NavigateToLogin
            }

            // DEĞİŞTİ: Navigasyon hedefini state'imizde saklıyoruz.
            _navigationTarget.value = targetEffect

            // DEĞİŞTİ: Navigasyon komutu göndermek yerine, UI'a "animasyonu başlat" diyoruz.
            setState { copy(isLoading = false, animateOut = true) }
        }
    }
}