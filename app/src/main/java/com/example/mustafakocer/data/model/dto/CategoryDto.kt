package com.example.mustafakocer.data.model.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

typealias CategoriesResponseDto = List<CategoryDto>

@Serializable
data class CategoryDto(
    // API'den gelen "slug", "name", "url" alanlarını alıyoruz.
    // "Fail-Fast" prensibiyle, slug ve name'in zorunlu olduğunu belirtiyoruz.
    @SerialName("slug") val slug: String,
    @SerialName("name") val name: String,
    @SerialName("url") val url: String? = null // URL opsiyonel olabilir.
)