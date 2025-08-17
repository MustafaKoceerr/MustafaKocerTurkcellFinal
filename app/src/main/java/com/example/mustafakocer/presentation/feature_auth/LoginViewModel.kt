package com.example.mustafakocer.presentation.feature_auth

import androidx.lifecycle.viewModelScope
import com.example.mustafakocer.domain.usecase.LoginUseCase
import com.example.mustafakocer.domain.util.Resource
import com.example.mustafakocer.presentation.base.BaseViewModel
import com.example.mustafakocer.presentation.feature_auth.contract.LoginEffect
import com.example.mustafakocer.presentation.feature_auth.contract.LoginEvent
import com.example.mustafakocer.presentation.feature_auth.contract.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    // Artık SaveSessionUseCase'e bağımlı değiliz.
) : BaseViewModel<LoginUiState, LoginEvent, LoginEffect>(
    initialState = LoginUiState()
) {

    override fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> {
                setState { copy(email = event.email) }
            }

            is LoginEvent.PasswordChanged -> {
                setState { copy(password = event.password) }
            }

            is LoginEvent.LoginClicked -> {
                login()
            }
        }
    }

    private fun login() {
        // UseCase'den gelen akışı dinle.
        loginUseCase(username = currentState.email, password = currentState.password)
            .onEach { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        // Yüklenme durumunu UI'a bildir.
                        setState { copy(isLoading = true, error = null) }
                    }

                    is Resource.Success -> {
                        // BAŞARILI!
                        // Oturumun kaydedildiğinden artık eminiz, çünkü bu iş UseCase/Repository katmanında halledildi.
                        // ViewModel'in tek yapması gereken, UI durumunu güncellemek ve navigasyon effect'ini göndermek.
                        setState { copy(isLoading = false) }
                        sendEffect(LoginEffect.NavigateToHome)
                    }

                    is Resource.Error -> {
                        // HATA!
                        // Gelen hatayı UI'a bildir ve bir Snackbar göster.
                        setState { copy(isLoading = false, error = resource.exception) }
                        val errorMessage =
                            resource.exception.message ?: "Bilinmeyen bir hata oluştu."
                        sendEffect(LoginEffect.ShowSnackbar(errorMessage))
                    }

                    is Resource.Idle -> { /* No-op */
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}