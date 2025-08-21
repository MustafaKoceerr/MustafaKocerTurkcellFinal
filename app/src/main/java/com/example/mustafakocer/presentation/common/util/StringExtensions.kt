package com.example.mustafakocer.presentation.common.util

/**
 * A public extension function to safely parse a formatted price string into a Double.
 * It removes currency symbols ($, ₺) and commas, making it robust for different formats.
 *
 * @return The parsed Double value, or 0.0 if parsing fails.
 */
fun String.parsePriceToDouble(): Double {
    // Regex'i bir kere oluşturarak küçük bir performans artışı sağlanır.
    val currencyRegex = Regex("[$,₺]")
    return this.replace(currencyRegex, "").replace(",", "").toDoubleOrNull() ?: 0.0
}