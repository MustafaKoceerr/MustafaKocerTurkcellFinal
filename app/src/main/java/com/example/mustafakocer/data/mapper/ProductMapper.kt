package com.example.mustafakocer.data.mapper

import com.example.mustafakocer.data.model.dto.ProductDetailDto
import com.example.mustafakocer.data.model.dto.ProductDto
import com.example.mustafakocer.data.model.entity.ProductEntity
import com.example.mustafakocer.domain.model.Product
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * A reusable, locale-independent price formatter.
 * Ensures prices are formatted with a dollar sign and a dot for the decimal separator.
 */
private val priceFormatter = DecimalFormat("'$'0.00", DecimalFormatSymbols(Locale.US))
private fun Double.toFormattedPrice(): String = priceFormatter.format(this)

/**
 * Private factory function to create a [Product] domain model.
 * Encapsulates the common mapping logic from different DTOs to prevent code duplication.
 */
private fun createProductFromData(
    id: Int?,
    title: String?,
    price: Double?,
    discountPercentage: Double?,
    thumbnail: String?,
    rating: Double?,
    stock: Int?
): Product {
    val originalPrice = price ?: 0.0
    val discount = discountPercentage ?: 0.0
    val calculatedDiscountedPrice = originalPrice * (1 - (discount / 100.0))

    return Product(
        id = id ?: 0,
        title = title.orEmpty(),
        price = originalPrice.toFormattedPrice(),
        discountedPrice = calculatedDiscountedPrice.toFormattedPrice(),
        thumbnailUrl = thumbnail.orEmpty(),
        rating = (rating ?: 0.0).toFloat(),
        stock = stock ?: 0
    )
}

/**
 * Converts a [ProductDto] from the data layer to a [Product] in the domain layer.
 */
fun ProductDto.toDomain(): Product = createProductFromData(
    id, title, price, discountPercentage, thumbnail, rating, stock
)

/**
 * Converts a [ProductDetailDto] from the data layer to a [Product] in the domain layer.
 * Reuses the common mapping logic.
 */
fun ProductDetailDto.toDomainProduct(): Product = createProductFromData(
    id, title, price, discountPercentage, thumbnail, rating, stock
)

/**
 * Converts a [ProductDto] from the data layer to a [ProductEntity] for database caching.
 */
fun ProductDto.toEntity(): ProductEntity = ProductEntity(
    id = id ?: 0,
    title = title.orEmpty(),
    description = description.orEmpty(),
    category = category.orEmpty(),
    price = price ?: 0.0,
    discountPercentage = discountPercentage ?: 0.0,
    rating = rating ?: 0.0,
    stock = stock ?: 0,
    brand = brand.orEmpty(),
    thumbnailUrl = thumbnail.orEmpty()
)

/**
 * Converts a [ProductEntity] from the database to a [Product] in the domain layer.
 */
fun ProductEntity.toDomain(): Product {
    val calculatedDiscountedPrice = price * (1 - (discountPercentage / 100.0))

    return Product(
        id = id,
        title = title,
        price = price.toFormattedPrice(),
        discountedPrice = calculatedDiscountedPrice.toFormattedPrice(),
        thumbnailUrl = thumbnailUrl,
        rating = rating.toFloat(),
        stock = stock
    )
}