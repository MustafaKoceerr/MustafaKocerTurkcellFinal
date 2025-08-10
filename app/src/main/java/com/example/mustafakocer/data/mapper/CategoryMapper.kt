package com.example.mustafakocer.data.mapper

import com.example.mustafakocer.data.model.dto.CategoryDto
import com.example.mustafakocer.domain.model.Category

// DTO -> Domain Model
fun CategoryDto.toDomain(): Category {
    return Category(
        // DTO'daki slug ve name alanlarını doğrudan Domain modeline aktarıyoruz.
        slug = this.slug,
        name = this.name
    )
}