package com.example.mustafakocer.data.mapper

import com.example.mustafakocer.data.model.dto.ProductDetailDto
import com.example.mustafakocer.data.model.dto.ReviewDto
import com.example.mustafakocer.domain.model.ProductDetail
import com.example.mustafakocer.domain.model.Review
import java.text.DecimalFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

// Reusable formatters to avoid creating new instances on every function call.
private val currencyFormatter = DecimalFormat("$#,##0.00")
private val percentageFormatter = DecimalFormat("#'%'")
private val inputDateFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME
private val outputDateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.getDefault())

// Private extension functions for clean and reusable formatting logic.
private fun Double.toCurrencyString(): String = currencyFormatter.format(this)
private fun Double.toPercentageString(): String = percentageFormatter.format(this)

/**
 * Converts a [ProductDetailDto] from the data layer to a [ProductDetail] in the domain layer.
 * This mapping includes price calculations, tag aggregation, and formatting.
 */
fun ProductDetailDto.toDomain(): ProductDetail {
    val originalPrice = price ?: 0.0
    val discountPercentage = discountPercentage ?: 0.0
    val calculatedDiscountedPrice = originalPrice * (1 - (discountPercentage / 100.0))

    val formatTag: (String) -> String = { tag ->
        tag.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

    val tagSet = mutableSetOf<String>().apply {
        brand?.takeIf { it.isNotBlank() }?.let { add(formatTag(it)) }
        category?.takeIf { it.isNotBlank() }?.let { add(formatTag(it)) }
        tags?.forEach { tag ->
            if (tag.isNotBlank()) add(formatTag(tag))
        }
    }

    return ProductDetail(
        id = id ?: 0,
        title = title.orEmpty(),
        description = description.orEmpty(),
        formattedPrice = originalPrice.toCurrencyString(),
        formattedDiscountedPrice = calculatedDiscountedPrice.toCurrencyString(),
        savingsInfo = "Savings: ${discountPercentage.toPercentageString()}",
        rating = (rating ?: 0.0).toFloat(),
        ratingCount = reviews?.size ?: 0,
        stock = stock ?: 0,
        tags = tagSet.toList(),
        images = images ?: emptyList(),
        reviews = reviews?.map { it.toDomain() } ?: emptyList()
    )
}

/**
 * Converts a [ReviewDto] from the data layer to a [Review] in the domain layer.
 * It also formats the date string from ISO format to a more readable format.
 */
fun ReviewDto.toDomain(): Review = Review(
    rating = rating ?: 0,
    comment = comment.orEmpty(),
    reviewerName = reviewerName.orEmpty(),
    formattedDate = date?.let {
        try {
            ZonedDateTime.parse(it, inputDateFormatter).format(outputDateFormatter)
        } catch (e: Exception) {
            it.substringBefore("T") // Fallback to "YYYY-MM-DD"
        }
    } ?: ""
)