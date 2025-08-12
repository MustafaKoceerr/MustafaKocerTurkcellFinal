package com.example.mustafakocer.presentation.feature_splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.example.mustafakocer.presentation.feature_splash.components.DotPulseIndicator
import com.example.mustafakocer.presentation.common.components.SplashBackground
import com.example.mustafakocer.presentation.feature_splash.components.SplashLogo
import com.example.mustafakocer.presentation.feature_splash.contract.SplashEvent
import com.example.mustafakocer.presentation.feature_splash.contract.SplashUiState
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    state: SplashUiState,
    onEvent: (SplashEvent) -> Unit
) {
    val alpha by animateFloatAsState(if (state.animateOut) 0f else 1f, tween(300), label = "fadeOut")
    val scale by animateFloatAsState(if (state.animateOut) 0.9f else 1f, tween(300), label = "scaleOut")

    LaunchedEffect(state.animateOut) {
        if (state.animateOut) {
            delay(300)
            onEvent(SplashEvent.ExitAnimationFinished)
        }
    }

    SplashBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
                .padding(horizontal = 24.dp)
                .alpha(alpha)
                .scale(scale),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            SplashLogo()
            Spacer(Modifier.height(24.dp))
            AnimatedVisibility(
                visible = state.isLoading,
                enter = fadeIn(tween(200)),
                exit = fadeOut(tween(200))
            ) { DotPulseIndicator() }
        }
    }
}
