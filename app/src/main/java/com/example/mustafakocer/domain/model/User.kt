package com.example.mustafakocer.domain.model

/**
 * Uygulamanın UI ve Domain katmanlarında bir kullanıcıyı temsil eden temiz model.
 */
data class User(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val username: String,
    val gender: String,
    val age: Int,
    val imageUrl: String
) {
    /**
     * Ad ve soyadın o anki değerlerinden birleştirilmiş tam adı döndüren
     * bir hesaplanan özellik (computed property).
     */
    val fullName: String
        get() = "$firstName $lastName".trim()
}