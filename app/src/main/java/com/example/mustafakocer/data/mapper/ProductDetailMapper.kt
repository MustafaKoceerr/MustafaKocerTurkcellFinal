package com.example.mustafakocer.data.mapper

import com.example.mustafakocer.data.model.dto.ProductDetailDto
import com.example.mustafakocer.data.model.dto.ReviewDto
import com.example.mustafakocer.domain.model.ProductDetail
import com.example.mustafakocer.domain.model.Review
import java.text.DecimalFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

// DTO -> Domain Model

fun ProductDetailDto.toDomain(): ProductDetail {
    val originalPrice = this.price ?: 0.0
    val discountPercentage = this.discountPercentage ?: 0.0
    val calculatedDiscountedPrice = originalPrice * (1 - (discountPercentage / 100.0))

    val priceFormat = DecimalFormat("$#,##0.00")
    val savingsFormat = DecimalFormat("#'%'")

    return ProductDetail(
        id = this.id ?: 0,
        title = this.title.orEmpty(),
        description = this.description.orEmpty(),
        formattedPrice = priceFormat.format(originalPrice),
        formattedDiscountedPrice = priceFormat.format(calculatedDiscountedPrice),
        savingsInfo = "Tasarruf: ${savingsFormat.format(discountPercentage)}",
        rating = (this.rating ?: 0.0).toFloat(),
        ratingCount = this.reviews?.size ?: 0,
        stock = this.stock ?: 0,
        brand = this.brand.orEmpty(),
        category = this.category.orEmpty(),
        images = this.images ?: emptyList(),
        reviews = this.reviews?.map { it.toDomain() } ?: emptyList()
    )
}

fun ReviewDto.toDomain(): Review {
    // API'den gelen tarih formatı: "2024-05-23T08:56:21.629Z" (ISO_ZONED_DATE_TIME)
    // Bizim istediğimiz format: "23 May 2024"
    val inputFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME
    val outputFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.getDefault())

    val formattedDate = try {
        val zonedDateTime = ZonedDateTime.parse(this.date, inputFormatter)
        zonedDateTime.format(outputFormatter)
    } catch (e: Exception) {
        // Tarih parse edilemezse, boş bir string veya ham veriyi döndür.
        this.date.orEmpty().substringBefore("T") // "2024-05-23"
    }

    return Review(
        rating = this.rating ?: 0,
        comment = this.comment.orEmpty(),
        reviewerName = this.reviewerName.orEmpty(),
        formattedDate = formattedDate
    )
}