package com.example.mustafakocer.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.TileMode

/**
 * A reusable Composable that provides a consistent gradient background for screens.
 * It uses colors from the app's [MaterialTheme] to support theming (e.g., light/dark mode).
 *
 * @param content A lambda that defines the content to be placed on top of this background.
 * The `BoxScope` receiver allows for alignment and layering of child components.
 */
@Composable
fun SplashBackground(content: @Composable BoxScope.() -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(colorScheme.primaryContainer, colorScheme.surface),
                    tileMode = TileMode.Clamp
                )
            ),
        content = content
    )
}