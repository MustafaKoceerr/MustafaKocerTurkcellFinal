package com.example.mustafakocer.data.model

import com.google.gson.annotations.SerializedName


data class CartResponse (

    @SerializedName("id"              ) var id              : Int?                = null,
    @SerializedName("products"        ) var products        : List<ProductsInCart> = mutableListOf(),
    @SerializedName("total"           ) var total           : Double?             = null,
    @SerializedName("discountedTotal" ) var discountedTotal : Int?                = null,
    @SerializedName("userId"          ) var userId          : Int?                = null,
    @SerializedName("totalProducts"   ) var totalProducts   : Int?                = null,
    @SerializedName("totalQuantity"   ) var totalQuantity   : Int?                = null

)

data class ProductsInCart (
    // the items in cart, I will get them after add some items in cart
    // then I will show them in

    @SerializedName("id"                 ) var id                 : Int?    = null,
    @SerializedName("title"              ) var title              : String? = null,
    @SerializedName("price"              ) var price              : Double? = null,
    @SerializedName("quantity"           ) var quantity           : Int?    = null,
    @SerializedName("total"              ) var total              : Double? = null,
    @SerializedName("discountPercentage" ) var discountPercentage : Double? = null,
    @SerializedName("discountedPrice"    ) var discountedPrice    : Int?    = null,
    @SerializedName("thumbnail"          ) var thumbnail          : String? = null

)