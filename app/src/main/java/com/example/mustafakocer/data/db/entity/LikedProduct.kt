package com.example.mustafakocer.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mustafakocer.data.model.Dimensions
import com.example.mustafakocer.data.model.Meta
import com.example.mustafakocer.data.model.Product
import com.example.mustafakocer.data.model.Reviews
import com.google.gson.annotations.SerializedName


@Entity(tableName = "liked_product")
data class LikedProduct(
    @PrimaryKey(autoGenerate = true)
    val localId: Int?,
    val userId: Int,
    var isLiked: Int = 1,
    // böylelikle eklenen product'lar
    val productId: Int?,
    // db'ye eklemem için nce kalp'e tıklamam lazım.
    // tekrar tıkladığımda ürünü sileceğim.
    val product: Product
    // product'ı tuttum, adapter'dan göstereceğim ürünler db'den gelecekler
)

