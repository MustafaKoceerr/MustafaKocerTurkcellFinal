package com.example.mustafakocer.domain.model

/**
 * Detay ekranında bir ürünü temsil eden temiz domain modeli.
 * Sadece UI'ın ihtiyaç duyduğu, formatlanmış ve basit verileri içerir.
 */
data class ProductDetail(
    val id: Int,
    val title: String,
    val description: String,
    val formattedPrice: String,
    val formattedDiscountedPrice: String,
    val savingsInfo: String, // Örn: "%15 Tasarruf"
    val rating: Float,
    val ratingCount: Int, // Yorum sayısı
    val stock: Int,
    val brand: String,
    val category: String,
    val images: List<String>,
    val reviews: List<Review>
)

/**
 * Bir ürün yorumunu temsil eden temiz domain modeli.
 */
data class Review(
    val rating: Int,
    val comment: String,
    val formattedDate: String,
    val reviewerName: String
)