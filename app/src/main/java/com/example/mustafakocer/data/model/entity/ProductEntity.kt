package com.example.mustafakocer.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a product as stored in the local Room database.
 * This entity serves as a cache for product data fetched from the network.
 */
@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val price: Double,
    val discountPercentage: Double,
    val rating: Double,
    val stock: Int,
    val brand: String,
    val thumbnailUrl: String
)