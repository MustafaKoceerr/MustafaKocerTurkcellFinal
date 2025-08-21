package com.example.mustafakocer.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Stores pagination keys for the Paging 3 library for the main (home) product list.
 */
@Entity(tableName = "remote_keys")
data class HomeRemoteKeyEntity(
    @PrimaryKey val productId: Int,
    val prevKey: Int?,
    val nextKey: Int?
)