package com.example.mustafakocer.presentation.feature_auth

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mustafakocer.presentation.feature_auth.contract.LoginEffect
import com.example.mustafakocer.presentation.navigation.contracts.LoginNavActions
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginRoute(
    navActions: LoginNavActions,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    // 1. ViewModel'den UI state'ini lifecycle'a duyarlı bir şekilde topla.
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(true) {
        viewModel.uiEffect.collectLatest { effect ->
            when (effect) {
                is LoginEffect.NavigateToHome -> {
                    navActions.navigateToHome()
                }

                is LoginEffect.ShowSnackbar -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    LoginScreen(
        state = state,
        onEvent = viewModel::onEvent
    )
}