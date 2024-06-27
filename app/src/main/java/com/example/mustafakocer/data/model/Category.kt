package com.example.mustafakocer.data.model


typealias Categories = List<Category>

data class Category (
    val slug: String,
    val name: String,
    val url: String
)
