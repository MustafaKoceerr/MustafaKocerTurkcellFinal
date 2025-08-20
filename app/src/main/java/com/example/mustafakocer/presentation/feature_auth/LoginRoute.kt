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

/**
 * A "smart" composable that acts as a route-level entry point for the Login feature.
 * Its primary responsibility is to connect the [LoginViewModel] to the [LoginScreen],
 * collecting UI state and handling one-time UI effects.
 *
 * @param navActions An interface containing the navigation actions available from this screen.
 * @param viewModel The Hilt-injected [LoginViewModel] for this feature.
 */
@Composable
fun LoginRoute(
    navActions: LoginNavActions,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Listen for one-time effects from the ViewModel.
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

    // Pass the state and event handler down to the "dumb" UI screen.
    LoginScreen(
        state = state,
        onEvent = viewModel::onEvent
    )
}