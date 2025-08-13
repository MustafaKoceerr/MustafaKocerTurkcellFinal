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
    // --- Fiyat Hesaplamaları ---
    val originalPrice = this.price ?: 0.0
    val discountPercentage = this.discountPercentage ?: 0.0
    val calculatedDiscountedPrice = originalPrice * (1 - (discountPercentage / 100.0))
    val priceFormat = DecimalFormat("$#,##0.00")
    val savingsFormat = DecimalFormat("#'%'")

    // --- Etiketleri Oluşturma Mantığı ---
    // Tekrar edenleri engellemek için bir Set kullanıyoruz.
    val tagSet = mutableSetOf<String>()

    // Metinleri formatlamak için yeniden kullanılabilir bir lambda.
    val formatTag: (String) -> String = { tag ->
        tag.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

    // 1. Markayı al, formatla ve Set'e ekle.
    this.brand?.takeIf { it.isNotBlank() }?.let { tagSet.add(formatTag(it)) }

    // 2. Kategoriyi al, formatla ve Set'e ekle.
    this.category?.takeIf { it.isNotBlank() }?.let { tagSet.add(formatTag(it)) }

    // 3. API'den gelen "tags" dizisindeki tüm etiketleri formatla ve Set'e ekle.
    this.tags?.forEach { tag ->
        if (tag.isNotBlank()) {
            tagSet.add(formatTag(tag))
        }
    }

    // --- Domain Modelini Oluşturma ---
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
        // Set'i nihai bir List'e çevirip atıyoruz.
        tags = tagSet.toList(),
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