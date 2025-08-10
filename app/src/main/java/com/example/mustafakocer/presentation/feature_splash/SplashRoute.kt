package com.example.mustafakocer.presentation.feature_splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mustafakocer.presentation.navigation.contracts.SplashNavActions
import androidx.compose.runtime.getValue
import com.example.mustafakocer.presentation.feature_splash.contract.SplashEffect
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SplashRoute(
    navActions: SplashNavActions,
    viewModel: SplashViewModel = hiltViewModel()
){
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // ViewModel'den gelen tek seferlik Effect'leri dinle.
    // LaunchedEffect'in key'i 'true' olduğu için bu blok sadece bir kez çalışır.
    LaunchedEffect(Unit) {
        viewModel.uiEffect.collectLatest { effect ->
            when (effect) {
                is SplashEffect.NavigateToHome -> navActions.navigateToHome()
                is SplashEffect.NavigateToLogin -> navActions.navigateToLogin()
            }
        }
    }

    // Aptal UI'a state'i pasla.
    SplashScreen(state = state)
}