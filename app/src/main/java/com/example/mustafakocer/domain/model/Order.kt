package com.example.mustafakocer.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// 1. @Parcelize annotation'ını ekle
// 2. Parcelable arayüzünü implemente et
@Parcelize
data class Order(
    val id: Int,
    val totalProducts: Int,
    val totalQuantity: Int,
    val discountedTotal: String,
    val total: String,
    val products: List<OrderProduct> // İçindeki liste de Parcelable olmalı
) : Parcelable // Parcelable'ı implemente et

@Parcelize // Bu sınıfı da Parcelable yap
data class OrderProduct(
    val id: Int,
    val title: String,
    val quantity: Int,
    val discountedPricePerUnit: String,
    val thumbnail: String
) : Parcelable // Parcelable'ı implemente et