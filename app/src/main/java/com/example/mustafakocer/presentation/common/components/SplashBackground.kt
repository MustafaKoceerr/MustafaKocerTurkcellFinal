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

@Composable
fun SplashBackground(content: @Composable BoxScope.() -> Unit) {
    val cs = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(cs.primaryContainer, cs.surface),
                    tileMode = TileMode.Clamp
                )
            ),
        content = content
    )
}
