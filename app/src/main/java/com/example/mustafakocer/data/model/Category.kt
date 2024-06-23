package com.example.mustafakocer.data.model

import com.google.gson.annotations.SerializedName

data class Categories(
    val categories: List<Category> = mutableListOf()
)

data class Category (

    @SerializedName("slug" ) var slug : String? = null,
    @SerializedName("name" ) var name : String? = null,
    @SerializedName("url"  ) var url  : String? = null

)
