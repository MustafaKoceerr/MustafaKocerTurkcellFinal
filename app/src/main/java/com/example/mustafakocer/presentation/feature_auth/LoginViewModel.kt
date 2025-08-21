package com.example.mustafakocer.presentation.feature_auth

import androidx.lifecycle.viewModelScope
import com.example.mustafakocer.domain.exception.AppException
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
) : BaseViewModel<LoginUiState, LoginEvent, LoginEffect>(
    initialState = LoginUiState()
) {

    override fun handleEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> setState { copy(email = event.email) }
            is LoginEvent.PasswordChanged -> setState { copy(password = event.password) }
            is LoginEvent.LoginClicked -> login()
        }
    }

    private fun login() {
        loginUseCase(username = currentState.email, password = currentState.password)
            .onEach { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        setState { copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        setState { copy(isLoading = false) }
                        sendEffect(LoginEffect.NavigateToHome)
                    }
                    is Resource.Error -> {
                        setState { copy(isLoading = false, error = resource.exception) }
                        handleLoginError(resource.exception)
                    }
                    is Resource.Idle -> { /* No-op */ }
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Maps a domain [AppException] to a user-friendly UI effect, like a snackbar.
     */
    private fun handleLoginError(exception: AppException) {
        val errorMessage = when (exception) {
            is AppException.Data.InputError -> exception.reason
            is AppException.Server.Unauthorized -> "Invalid username or password."
            is AppException.Network.NoInternet -> "Please check your internet connection."
            else -> "An unknown error occurred."
        }
        sendEffect(LoginEffect.ShowSnackbar(errorMessage))
    }
}