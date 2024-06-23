package com.example.mustafakocer.data.db.entity



data class CartRequest(
    val userId: Int,
    val products: List<ProductItem>
)

data class ProductItem(
    val id: Int,
    val quantity: Int
)