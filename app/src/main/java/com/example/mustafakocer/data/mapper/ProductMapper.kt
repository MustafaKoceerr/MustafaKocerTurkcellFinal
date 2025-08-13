package com.example.mustafakocer.data.mapper

import com.example.mustafakocer.data.model.dto.ProductDetailDto
import com.example.mustafakocer.data.model.dto.ProductDto
import com.example.mustafakocer.data.model.entity.ProductEntity
import com.example.mustafakocer.domain.model.Product
import java.text.DecimalFormat

// DTO -> Domain Model
fun ProductDto.toDomain(): Product { // 'isLiked' parametresini şimdilik kaldırabiliriz.
    val originalPrice = this.price ?: 0.0
    val discount = this.discountPercentage ?: 0.0
    val calculatedDiscountedPrice = originalPrice * (1 - (discount / 100.0))
    val priceFormat = DecimalFormat("$#,##0.00")

    return Product(
        id = this.id ?: 0,
        title = this.title.orEmpty(),
        price = priceFormat.format(originalPrice),
        discountedPrice = priceFormat.format(calculatedDiscountedPrice),
        thumbnailUrl = this.thumbnail.orEmpty(),
        rating = (this.rating ?: 0.0).toFloat(), // YENİ ALAN EKLENDİ
        stock = this.stock ?: 0                 // YENİ ALAN EKLENDİ
    )
}

// DTO -> Entity (DÜZELTİLDİ)
fun ProductDto.toEntity(): ProductEntity {
    return ProductEntity(
        id = this.id ?: 0,
        title = this.title.orEmpty(),
        description = this.description.orEmpty(),
        category = this.category.orEmpty(), // DÜZELTME: Eksik olan category alanı eklendi.
        price = this.price ?: 0.0,
        discountPercentage = this.discountPercentage ?: 0.0,
        rating = this.rating ?: 0.0,
        stock = this.stock ?: 0,
        brand = this.brand.orEmpty(),
        thumbnailUrl = this.thumbnail.orEmpty()
    )
}

// Entity -> Domain Model
fun ProductEntity.toDomain(): Product {
    val calculatedDiscountedPrice = this.price * (1 - (this.discountPercentage / 100.0))
    val priceFormat = DecimalFormat("$#,##0.00")

    return Product(
        id = this.id,
        title = this.title,
        price = priceFormat.format(this.price),
        discountedPrice = priceFormat.format(calculatedDiscountedPrice),
        thumbnailUrl = this.thumbnailUrl,
        rating = this.rating.toFloat(), // YENİ ALAN EKLENDİ
        stock = this.stock              // YENİ ALAN EKLENDİ
    )
}


fun ProductDetailDto.toDomainProduct(): Product {
    val safeId = this.id ?: throw IllegalArgumentException("Product ID from API cannot be null")

    val originalPrice = this.price ?: 0.0
    val discountPercentage = this.discountPercentage ?: 0.0
    val discountedPriceValue = if (discountPercentage > 0) {
        originalPrice * (1 - discountPercentage / 100)
    } else {
        originalPrice
    }

    val priceFormat = DecimalFormat("$#,##0.00")
    val formattedPrice = priceFormat.format(originalPrice)
    val formattedDiscountedPrice = priceFormat.format(discountedPriceValue)

    // 4. BİRLEŞTİRME:
    // Tüm verileri temiz Product modelinde birleştir.
    return Product(
        id = safeId,
        title = this.title ?: "Unnamed Product",
        price = formattedPrice,
        discountedPrice = formattedDiscountedPrice,
        thumbnailUrl = this.thumbnail ?: "",
        rating = (this.rating?.toFloat() ?: 0.0f),
        stock = this.stock ?: 0
    )
}