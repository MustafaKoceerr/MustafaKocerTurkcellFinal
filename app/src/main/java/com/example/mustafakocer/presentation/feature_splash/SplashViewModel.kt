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

    init {
        // ViewModel oluşturulduğunda oturum kontrolünü başlat.
        checkAuthStatus()
    }

    override fun onEvent(event: SplashEvent) {
        // Bu ekranda UI'dan gelen bir event beklemiyoruz, o yüzden bu blok boş.
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            // Splash ekranının çok hızlı geçmemesi için küçük bir gecikme ekleyelim.
            // Bu, daha iyi bir kullanıcı deneyimi sağlar.
            delay(1500)

            val isLoggedIn = checkAuthStatusUseCase()
            if (isLoggedIn) {
                sendEffect(SplashEffect.NavigateToHome)
            } else {
                sendEffect(SplashEffect.NavigateToLogin)
            }
        }
    }

}