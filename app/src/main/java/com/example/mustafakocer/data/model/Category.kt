package com.example.mustafakocer.data.model

import com.google.gson.annotations.SerializedName

typealias Categories = List<Category>

data class Category (
    val slug: String,
    val name: String,
    val url: String
)
