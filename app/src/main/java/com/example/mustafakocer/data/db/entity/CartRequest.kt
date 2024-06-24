package com.example.mustafakocer.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


data class CartRequest(
    val userId: Int,
    val products: List<CartList>
    // api böyle istediği için böyle gönderiyorum
    // yani ben api'ye bunları göndersem yeterli, api bana gönderdiğim ürünleri tekrar döndürüyor
    // onları da alıp sepetim kısmında listeleteceğim.
)

@Entity(tableName = "cart_list")
data class CartList(
    @PrimaryKey(autoGenerate = true)
    val localId:Int,
    val userId: Int,
    @SerializedName("id") val id: Int? = null,
    @SerializedName("quantity")val quantity: Int? = null
)