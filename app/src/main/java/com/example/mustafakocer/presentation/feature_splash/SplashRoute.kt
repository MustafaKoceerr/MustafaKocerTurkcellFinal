package com.example.mustafakocer.presentation.feature_splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mustafakocer.presentation.feature_splash.contract.SplashEffect
import com.example.mustafakocer.presentation.navigation.contracts.SplashNavActions
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SplashRoute(
    navActions: SplashNavActions,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collectLatest { effect ->
            when (effect) {
                is SplashEffect.NavigateToHome -> navActions.navigateToHome()
                is SplashEffect.NavigateToLogin -> navActions.navigateToLogin()
            }
        }
    }

    // DEĞİŞTİ: Artık onEvent'i doğrudan paslıyoruz.
    SplashScreen(
        state = uiState,
        onEvent = viewModel::onEvent
    )
}