package com.example.mustafakocer.presentation.common.util

// presentation/common/format/Currency.kt
import java.text.NumberFormat
import java.util.*

fun Double.usd(): String =
    NumberFormat.getCurrencyInstance(Locale.US).apply {
        maximumFractionDigits = 2; minimumFractionDigits = 2
        currency = Currency.getInstance("USD")
    }.format(this)
