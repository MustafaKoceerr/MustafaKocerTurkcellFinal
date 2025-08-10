package com.example.mustafakocer.domain.model

/**
 * UI katmanında bir kategoriyi temsil eden temiz veri sınıfı.
 *
 * @param slug Kategorinin API'de kullanılan benzersiz adı (örn: "smartphones").
 * @param name Kategorinin kullanıcıya gösterilen adı (örn: "Smartphones").
 */
data class Category(
    val slug: String,
    val name: String
)