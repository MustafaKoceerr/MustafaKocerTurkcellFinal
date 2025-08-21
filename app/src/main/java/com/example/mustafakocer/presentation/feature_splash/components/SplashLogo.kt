package com.example.mustafakocer.presentation.feature_splash.components

import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.mustafakocer.R

/**
 * A composable that displays the application logo with a subtle "pop-in" scale animation
 * when it first appears on the screen.
 */
@Composable
fun SplashLogo(modifier: Modifier = Modifier) {
    var started by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (started) 1f else 0.85f,
        animationSpec = tween(700, easing = EaseOutBack),
        label = "logoScale"
    )
    LaunchedEffect(Unit) { started = true }

    val logoAspectRatio = 149f / 35f

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.nexus_cart_logo),
            contentDescription = stringResource(R.string.cd_app_logo),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth(0.60f)
                .aspectRatio(logoAspectRatio) // Maintains the logo's proportions
                .scale(scale)
        )
        Spacer(Modifier.height(12.dp))
    }
}