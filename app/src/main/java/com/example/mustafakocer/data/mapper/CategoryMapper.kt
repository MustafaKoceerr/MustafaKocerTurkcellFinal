package com.example.mustafakocer.data.mapper

import com.example.mustafakocer.data.model.dto.CategoryDto
import com.example.mustafakocer.domain.model.Category

/**
 * Converts a [CategoryDto] from the data layer to a [Category] in the domain layer.
 */
fun CategoryDto.toDomain(): Category = Category(
    slug = slug,
    name = name
)