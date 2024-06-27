package com.example.mustafakocer.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mustafakocer.data.model.Product


@Entity(tableName = "liked_product")
data class LikedProduct(
    @PrimaryKey(autoGenerate = true)
    val localId: Int?,
    val userId: Int,
    var isLiked: Int = 1,
    val productId: Int?,
    // db'ye eklemem için nce kalp'e tıklamam lazım.
    // tekrar tıkladığımda ürünü sileceğim.
    val product: Product
)

