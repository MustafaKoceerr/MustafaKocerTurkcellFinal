package com.example.mustafakocer.domain.model

/**
 * A clean domain model representing a product on its detail screen.
 * It contains only the formatted and simplified data required by the UI.
 */
data class ProductDetail(
    val id: Int,
    val title: String,
    val description: String,
    val formattedPrice: String,
    val formattedDiscountedPrice: String,
    val savingsInfo: String, // e.g., "15% Savings"
    val rating: Float,
    val ratingCount: Int,
    val stock: Int,
    val tags: List<String>,
    val images: List<String>,
    val reviews: List<Review>
)

/**
 * A clean domain model representing a single product review.
 */
data class Review(
    val rating: Int,
    val comment: String,
    val formattedDate: String,
    val reviewerName: String
)