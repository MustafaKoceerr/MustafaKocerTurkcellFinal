package com.example.mustafakocer.data.model.entity

import androidx.room.Entity

/**
 * Stores pagination keys for the Paging 3 library, specific to each product category.
 * This allows for independent pagination states for different category lists.
 * A composite primary key ensures uniqueness for each product within a specific category.
 */
@Entity(
    tableName = "category_remote_keys",
    primaryKeys = ["category", "productId"]
)
data class CategoryRemoteKeyEntity(
    val category: String,
    val productId: Int,
    val prevKey: Int?,
    val nextKey: Int?
)