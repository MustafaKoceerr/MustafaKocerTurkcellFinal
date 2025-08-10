package com.example.mustafakocer.data.model.entity

import com.google.firebase.database.IgnoreExtraProperties

// Firebase'in bilmediği alanları görmezden gelmesini sağlar.
@IgnoreExtraProperties
data class CartItemEntity(
    val productId: Int? = null,
    val quantity: Int? = null
) {
    // Firebase'in boş constructor'a ihtiyacı vardır.
    constructor() : this(0, 0)
}