package com.example.mustafakocer.domain.model

/**
 * Kullanıcının sepetindeki tek bir öğeyi temsil eder.
 * Bu model, hangi üründen (Product) kaç adet (quantity) olduğunu bir araya getirir.
 * Bu, "Kompozisyon" (Composition) prensibinin bir uygulamasıdır.
 */
data class CartItem(
    val product: Product, // Product modelini doğrudan içerir.
    val quantity: Int
)