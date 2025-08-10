package com.example.mustafakocer.presentation.feature_splash.contract

import com.example.mustafakocer.domain.exception.AppException
import com.example.mustafakocer.domain.presentation.BaseUiEffect
import com.example.mustafakocer.domain.presentation.BaseUiEvent
import com.example.mustafakocer.domain.presentation.BaseUiState

/**
 * Splash ekranının UI durumunu (State) temsil eder.
 *
 * @param isLoading Oturum durumu kontrol edilirken bir yükleme göstergesinin
 *                  gösterilip gösterilmeyeceğini belirtir.
 * @param error Bu ekranda genellikle bir hata gösterilmez, ancak base state'in
 *              bir parçası olarak bulunur.
 */
data class SplashUiState(
    // isLoading'i true başlatarak, ekran açılır açılmaz bir yükleme
    // göstergesi (örn: CircularProgressIndicator) gösterilmesini sağlıyoruz.
    override val isLoading: Boolean = true,
    override val error: AppException? = null,
) : BaseUiState


sealed interface SplashEvent : BaseUiEvent


sealed interface SplashEffect : BaseUiEffect {

    data object NavigateToHome : SplashEffect
    data object NavigateToLogin : SplashEffect
}