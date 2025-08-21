package com.example.mustafakocer.presentation.feature_splash.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp

/**
 * A composable that displays a pulsing three-dot loading indicator.
 * The animation is continuous and uses the primary theme color.
 *
 * @param modifier The modifier to be applied to the Row container.
 * @param dotSize The size of each individual dot in dp.
 * @param spacing The spacing between each dot in dp.
 * @param duration The duration for one cycle of the pulse animation in milliseconds.
 */
@Composable
fun DotPulseIndicator(
    modifier: Modifier = Modifier,
    dotSize: Int = 10,
    spacing: Int = 8,
    duration: Int = 900
) {
    val colorScheme = MaterialTheme.colorScheme
    val transition = rememberInfiniteTransition(label = "dots")

    val dots = List(3) { index ->
        transition.animateFloat(
            initialValue = 0.6f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(duration, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
                initialStartOffset = StartOffset((duration / 6) * index)
            ),
            label = "dot$index"
        )
    }

    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(spacing.dp)) {
        dots.forEach { scale ->
            Surface(
                modifier = Modifier
                    .size(dotSize.dp)
                    .scale(scale.value),
                color = colorScheme.primary,
                shape = CircleShape
            ) {}
        }
    }
}