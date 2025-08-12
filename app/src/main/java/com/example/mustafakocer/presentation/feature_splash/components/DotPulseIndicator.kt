package com.example.mustafakocer.presentation.feature_splash.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp

@Composable
fun DotPulseIndicator(
    modifier: Modifier = Modifier,
    dotSize: Int = 10,
    spacing: Int = 8,
    duration: Int = 900
) {
    val cs = MaterialTheme.colorScheme
    val transition = rememberInfiniteTransition(label = "dots")
    val d1 = transition.animateFloat(
        initialValue = 0.6f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(duration, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "d1"
    )
    val d2 = transition.animateFloat(
        initialValue = 0.6f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(duration, delayMillis = duration / 6, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "d2"
    )
    val d3 = transition.animateFloat(
        initialValue = 0.6f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(duration, delayMillis = duration / 3, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "d3"
    )
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(spacing.dp)) {
        listOf(d1.value, d2.value, d3.value).forEach { s ->
            Surface(
                modifier = Modifier.size(dotSize.dp).scale(s),
                color = cs.primary,
                contentColor = cs.onPrimary,
                shape = CircleShape
            ) {}
        }
    }
}
