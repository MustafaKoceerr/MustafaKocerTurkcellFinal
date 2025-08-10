package com.example.mustafakocer.data.mapper

import com.example.mustafakocer.data.model.dto.OrderDto
import com.example.mustafakocer.data.model.dto.OrderProductDto
import com.example.mustafakocer.domain.model.Order
import com.example.mustafakocer.domain.model.OrderProduct
import java.text.DecimalFormat

// OrderProductDto -> OrderProduct (Domain Model)
fun OrderProductDto.toDomain(): OrderProduct {
    val priceFormat = DecimalFormat("$#,##0.00")
    // API, ürünün toplam indirimli fiyatını veriyor, biz birim fiyatını hesaplayalım.
    val discountedPricePerUnit = if (quantity > 0) discountedTotal / quantity else 0.0

    return OrderProduct(
        id = this.id,
        title = this.title,
        quantity = this.quantity,
        discountedPricePerUnit = priceFormat.format(discountedPricePerUnit),
        thumbnail = this.thumbnail
    )
}

// OrderDto -> Order (Domain Model)
fun OrderDto.toDomain(): Order {
    val priceFormat = DecimalFormat("$#,##0.00")

    return Order(
        id = this.id,
        totalProducts = this.totalProducts,
        totalQuantity = this.totalQuantity,
        discountedTotal = priceFormat.format(this.discountedTotal),
        total = priceFormat.format(this.total),
        products = this.products.map { it.toDomain() } // İç içe mapping
    )
}