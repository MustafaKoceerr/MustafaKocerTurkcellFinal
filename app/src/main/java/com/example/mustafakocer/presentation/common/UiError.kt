package com.example.mustafakocer.presentation.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.mustafakocer.R

/**
 * Represents a structured error state for the UI layer.
 * It contains all the necessary information to display a user-friendly error screen.
 */
data class UiError(
    @StringRes val title: Int,
    @StringRes val subtitle: Int,
    @DrawableRes val icon: Int,
    @StringRes val retryButtonText: Int = R.string.action_retry // Default retry text
)