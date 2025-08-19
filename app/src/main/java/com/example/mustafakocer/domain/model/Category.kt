package com.example.mustafakocer.domain.model

/**
 * A clean data class representing a product category in the UI layer.
 *
 * @param slug The unique identifier for the category used in the API (e.g., "smartphones").
 * @param name The display-friendly name for the category (e.g., "Smartphones").
 */
data class Category(
    val slug: String,
    val name: String
)