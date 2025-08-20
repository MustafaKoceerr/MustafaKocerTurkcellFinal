package com.example.mustafakocer.presentation.common

import android.content.Context
import androidx.annotation.StringRes

/**
 * A sealed interface to represent text that can be either a direct String
 * or a String resource. This allows ViewModels to remain free of Android Context dependencies.
 */
sealed interface UiText {
    data class DynamicString(val value: String) : UiText
    data class StringResource(@StringRes val id: Int) : UiText

    fun asString(context: Context): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> context.getString(id)
        }
    }
}