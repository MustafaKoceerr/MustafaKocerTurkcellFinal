package com.example.mustafakocer.data.mapper

import com.example.mustafakocer.data.model.dto.ProductDetailDto
import com.example.mustafakocer.data.model.dto.ProductDto
import com.example.mustafakocer.data.model.entity.ProductEntity
import com.example.mustafakocer.domain.model.Product
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * A centralized, locale-independent price formatter.
 * It ensures that prices are always formatted as Strings with a dollar sign prefix ($)
 * and a dot (.) as the decimal separator, regardless of the device's locale.
 * This is crucial for reliable calculations later on.
 * Example: $1234.56
 */
// DEĞİŞTİ: Format desenine '$' eklendi.
private val priceFormat = DecimalFormat("'$'0.00", DecimalFormatSymbols(Locale.US))

// --- DTO to Domain ---

/**
 * Maps a [ProductDto] (from a list) to a [Product] domain model.
 */
fun ProductDto.toDomain(): Product {
    val originalPrice = this.price ?: 0.0
    val discount = this.discountPercentage ?: 0.0
    val calculatedDiscountedPrice = originalPrice * (1 - (discount / 100.0))

    return Product(
        id = this.id ?: 0,
        title = this.title.orEmpty(),
        price = priceFormat.format(originalPrice),
        discountedPrice = priceFormat.format(calculatedDiscountedPrice),
        thumbnailUrl = this.thumbnail.orEmpty(),
        rating = (this.rating ?: 0.0).toFloat(),
        stock = this.stock ?: 0
    )
}

/**
 * Maps a [ProductDetailDto] (from a detail screen API call) to a [Product] domain model.
 */
fun ProductDetailDto.toDomainProduct(): Product {
    val originalPrice = this.price ?: 0.0
    val discount = this.discountPercentage ?: 0.0
    val calculatedDiscountedPrice = originalPrice * (1 - (discount / 100.0))

    return Product(
        id = this.id ?: 0,
        title = this.title.orEmpty(),
        price = priceFormat.format(originalPrice),
        discountedPrice = priceFormat.format(calculatedDiscountedPrice),
        thumbnailUrl = this.thumbnail.orEmpty(),
        rating = (this.rating ?: 0.0).toFloat(),
        stock = this.stock ?: 0
    )
}

// --- DTO to Entity ---

/**
 * Maps a [ProductDto] to a [ProductEntity] for database storage.
 */
fun ProductDto.toEntity(): ProductEntity {
    return ProductEntity(
        id = this.id ?: 0,
        title = this.title.orEmpty(),
        description = this.description.orEmpty(),
        category = this.category.orEmpty(),
        price = this.price ?: 0.0,
        discountPercentage = this.discountPercentage ?: 0.0,
        rating = this.rating ?: 0.0,
        stock = this.stock ?: 0,
        brand = this.brand.orEmpty(),
        thumbnailUrl = this.thumbnail.orEmpty()
    )
}

// --- Entity to Domain ---

/**
 * Maps a [ProductEntity] from the database to a [Product] domain model.
 */
fun ProductEntity.toDomain(): Product {
    val calculatedDiscountedPrice = this.price * (1 - (this.discountPercentage / 100.0))

    return Product(
        id = this.id,
        title = this.title,
        price = priceFormat.format(this.price),
        discountedPrice = priceFormat.format(calculatedDiscountedPrice),
        thumbnailUrl = this.thumbnailUrl,
        rating = this.rating.toFloat(),
        stock = this.stock
    )
}