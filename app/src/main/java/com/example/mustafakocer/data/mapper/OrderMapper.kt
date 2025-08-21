package com.example.mustafakocer.data.mapper

import com.example.mustafakocer.data.model.dto.OrderDto
import com.example.mustafakocer.data.model.dto.OrderProductDto
import com.example.mustafakocer.domain.model.Order
import com.example.mustafakocer.domain.model.OrderProduct
import java.text.DecimalFormat

/**
 * A private helper function to format a Double into a standard currency string.
 * Encapsulates the formatting logic to avoid code duplication.
 */
private fun Double.toFormattedPrice(): String = DecimalFormat("$#,##0.00").format(this)

/**
 * Converts an [OrderProductDto] from the data layer to an [OrderProduct] in the domain layer.
 * It also calculates the price per unit from the total price.
 */
fun OrderProductDto.toDomain(): OrderProduct {
    val discountedPricePerUnit = if (quantity > 0) discountedTotal / quantity else 0.0

    return OrderProduct(
        id = id,
        title = title,
        quantity = quantity,
        discountedPricePerUnit = discountedPricePerUnit.toFormattedPrice(),
        thumbnail = thumbnail
    )
}

/**
 * Converts an [OrderDto] from the data layer to an [Order] in the domain layer.
 * It also formats the total prices and maps its nested product list.
 */
fun OrderDto.toDomain(): Order = Order(
    id = id,
    totalProducts = totalProducts,
    totalQuantity = totalQuantity,
    discountedTotal = discountedTotal.toFormattedPrice(),
    total = total.toFormattedPrice(),
    products = products.map { it.toDomain() }
)