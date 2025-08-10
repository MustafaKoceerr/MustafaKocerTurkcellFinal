package com.example.mustafakocer.presentation.feature_auth

import androidx.lifecycle.viewModelScope
import com.example.mustafakocer.domain.exception.AppException
import com.example.mustafakocer.domain.usecase.LoginUseCase
import com.example.mustafakocer.domain.usecase.SaveSessionUseCase
import com.example.mustafakocer.domain.util.Resource
import com.example.mustafakocer.presentation.base.BaseViewModel
import com.example.mustafakocer.presentation.feature_auth.contract.LoginEffect
import com.example.mustafakocer.presentation.feature_auth.contract.LoginEvent
import com.example.mustafakocer.presentation.feature_auth.contract.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val saveSessionUseCase: SaveSessionUseCase
) : BaseViewModel<LoginUiState, LoginEvent, LoginEffect>(
    initialState = LoginUiState() // Başlangıç state'ini veriyoruz.
) {

    /**
     * UI'dan gelen tüm event'leri (kullanıcı eylemleri) işleyen merkezi fonksiyon.
     */
    override fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> {
                // Email TextField'ı her değiştiğinde state'i güncelle.
                setState { copy(email = event.email) }
            }

            is LoginEvent.PasswordChanged -> {
                // Password TextField'ı her değiştiğinde state'i güncelle.
                setState { copy(password = event.password) }
            }

            is LoginEvent.LoginClicked -> {
                // Login butonuna tıklandığında giriş işlemini başlat.
                login()
            }
        }
    }

    private fun login() {
        val email = currentState.email
        val password = currentState.password

        loginUseCase(username = email, password = password)
            .onEach { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        setState { copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        val token = resource.data.token
                        val userId = resource.data.id

                        if (token != null && userId != null) {
                            viewModelScope.launch {
                                saveSessionUseCase(token = token, userId = userId)
                                setState { copy(isLoading = false) }
                                sendEffect(LoginEffect.NavigateToHome)
                            }
                        } else {
                            // API'den token veya id null geldiyse, bu beklenmedik bir veri hatasıdır.
                            val error = AppException.Data.Parse(null)
                            setState { copy(isLoading = false, error = error) }
                            sendEffect(LoginEffect.ShowSnackbar(error.message ?: "Bir hata oluştu."))
                        }
                    }
                    is Resource.Error -> {
                        // Gelen hatayı state'e ata ve UI'a bir Snackbar göstermesi için Effect gönder.
                        setState { copy(isLoading = false, error = resource.exception) }
                        val errorMessage = resource.exception.message ?: "Bilinmeyen bir hata oluştu."
                        sendEffect(LoginEffect.ShowSnackbar(errorMessage))
                    }
                    is Resource.Idle -> { /* No-op */ }
                }
            }
            .launchIn(viewModelScope)
    }
}