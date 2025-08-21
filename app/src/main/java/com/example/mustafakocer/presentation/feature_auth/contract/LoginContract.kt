package com.example.mustafakocer.presentation.feature_auth.contract

import com.example.mustafakocer.domain.exception.AppException
import com.example.mustafakocer.presentation.mvi.BaseUiEffect
import com.example.mustafakocer.presentation.mvi.BaseUiEvent
import com.example.mustafakocer.presentation.mvi.BaseUiState

/**
 * Represents the UI state for the Login screen.
 *
 * @param email The email address entered by the user.
 * @param password The password entered by the user.
 * @param emailError A validation error message for the email field.
 * @param passwordError A validation error message for the password field.
 * @param isLoading Indicates if a login operation is in progress.
 * @param error A general exception to be displayed (e.g., network error).
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    override val isLoading: Boolean = false,
    override val error: AppException? = null
) : BaseUiState

/**
 * Represents all possible user actions (Events) on the Login screen.
 */
sealed interface LoginEvent : BaseUiEvent {
    data class EmailChanged(val email: String) : LoginEvent
    data class PasswordChanged(val password: String) : LoginEvent
    object LoginClicked : LoginEvent
}

/**
 * Represents one-time side-effects (Effects) that can be triggered from the Login screen,
 * such as navigation or showing a snackbar.
 */
sealed interface LoginEffect : BaseUiEffect {
    object NavigateToHome : LoginEffect
    data class ShowSnackbar(val message: String) : LoginEffect
}
