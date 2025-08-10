package com.example.mustafakocer.presentation.feature_splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.mustafakocer.presentation.feature_splash.contract.SplashUiState

// Bu, "aptal" UI bileşenidir. Sadece state'i alır ve gösterir.
@Composable
fun SplashScreen(
    state: SplashUiState,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // TODO: Buraya uygulamanın logosunu ekleyebilirsin.
        if (state.isLoading) {
            CircularProgressIndicator()
        }
    }
}