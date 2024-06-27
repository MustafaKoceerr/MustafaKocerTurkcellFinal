package com.example.mustafakocer.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


data class CartRequest(
    val userId: Int,
    val products: List<Cart>
)

@Entity(tableName = "cart_list")
data class Cart(
    @PrimaryKey(autoGenerate = true)
    val localId:Int?,
    val userId: Int,
    @SerializedName("id") val id: Int? = null, // aslında product id
    @SerializedName("quantity")val quantity: Int? = null
)