package com.example.mustafakocer.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Represents a user's order. Made Parcelable to be easily passed between
 * Android components, such as fragments.
 */
@Parcelize
data class Order(
    val id: Int,
    val totalProducts: Int,
    val totalQuantity: Int,
    val discountedTotal: String,
    val total: String,
    val products: List<OrderProduct>
) : Parcelable

/**
 * Represents a single product within an order. Also Parcelable.
 */
@Parcelize
data class OrderProduct(
    val id: Int,
    val title: String,
    val quantity: Int,
    val discountedPricePerUnit: String,
    val thumbnail: String
) : Parcelable