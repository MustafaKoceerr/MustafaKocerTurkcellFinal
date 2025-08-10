package com.example.mustafakocer.data.model.entity

import androidx.room.Entity

/**
 * Kategoriye göre ürün listelerinin sayfalama durumunu saklar.
 */
@Entity(
    tableName = "category_remote_keys",
    primaryKeys = ["category", "productId"] // Birleşik anahtar
)
data class CategoryRemoteKeyEntity(
    val category: String, // Hangi kategoriye ait olduğunu belirtir
    val productId: Int,
    val prevKey: Int?,
    val nextKey: Int?,
)