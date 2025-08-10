package com.example.mustafakocer.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "products") // Tablo adını "products" olarak değiştirdik.
data class ProductEntity(
    @PrimaryKey val id: Int,
    // Artık userId'ye ihtiyacımız yok, bu global bir önbellek.
    val title: String,
    val description: String,
    val category: String, // YENİ VE KRİTİK SÜTUN
    val price: Double,
    val discountPercentage: Double,
    val rating: Double,
    val stock: Int,
    val brand: String,
    val thumbnailUrl: String
)