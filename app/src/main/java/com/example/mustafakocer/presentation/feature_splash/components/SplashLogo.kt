package com.example.mustafakocer.presentation.feature_splash.components

import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import com.example.mustafakocer.R

@Composable
fun SplashLogo(modifier: Modifier = Modifier) {
    var started by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (started) 1f else 0.85f,
        animationSpec = tween(700, easing = EaseOutBack),
        label = "logoScale"
    )
    LaunchedEffect(Unit) { started = true }

    // Viewport oranı: 149 / 35
    val logoAspect = 149f / 35f

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.nexus_cart_logo), // lockup (geniş)
            contentDescription = stringResource(R.string.cd_app_logo),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth(0.60f)    // ekranın %60’ı kadar genişlik
                .aspectRatio(logoAspect) // yüksekliği otomatik hesaplanır
                .scale(scale)
        )
        Spacer(Modifier.height(12.dp))
    }
}
