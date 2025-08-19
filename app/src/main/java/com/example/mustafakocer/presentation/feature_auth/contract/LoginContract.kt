package com.example.mustafakocer.presentation.feature_auth.contract

import com.example.mustafakocer.domain.exception.AppException
import com.example.mustafakocer.presentation.mvi.BaseUiEffect
import com.example.mustafakocer.presentation.mvi.BaseUiEvent
import com.example.mustafakocer.presentation.mvi.BaseUiState

/**
 * Login ekranının UI durumunu (State) temsil eden veri sınıfı.
 *
 * @param email Kullanıcının girdiği e-posta adresi.
 * @param password Kullanıcının girdiği şifre.
 * @param isLoading Giriş işlemi sırasında yüklenme durumunu belirtir.
 * @param error Bir hata oluştuğunda gösterilecek olan istisna.
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    override val isLoading: Boolean = false,
    override val error: AppException? = null
) : BaseUiState

/**
 * Login ekranında gerçekleşebilecek tüm kullanıcı eylemlerini (Event) temsil eder.
 */
sealed interface LoginEvent : BaseUiEvent {
    data class EmailChanged(val email: String) : LoginEvent
    data class PasswordChanged(val password: String) : LoginEvent
    object LoginClicked : LoginEvent
}

/**
 * Login ekranından tetiklenebilecek tek seferlik olayları (Effect) temsil eder.
 * Örn: Navigasyon, Snackbar gösterme.
 */
sealed interface LoginEffect : BaseUiEffect {
    object NavigateToHome : LoginEffect
    data class ShowSnackbar(val message: String) : LoginEffect
}