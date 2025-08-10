package com.example.mustafakocer.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class HomeRemoteKeyEntity(
    @PrimaryKey val productId: Int,
    val prevKey: Int?,
    val nextKey: Int?
)