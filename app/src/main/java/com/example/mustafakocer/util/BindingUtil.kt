package com.example.mustafakocer.util

import android.graphics.Paint
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.mustafakocer.R
import java.text.DecimalFormat


@BindingAdapter("imageUrl")
fun loadImage(view: ImageView, url: String?) {
    if (url.isNullOrEmpty()) {
        view.setImageDrawable(null)
    } else {
        Glide.with(view.context)
            .load(url)
            .into(view)
        // into means where should I put the picture
    }
}

@BindingAdapter("stockStatus")
fun setStockStatus(view: TextView, stock: Int?) {
    if (stock != null && stock > 0) {
        view.text = "In Stock: $stock"
        view.setTextColor(view.context.getColor(android.R.color.holo_green_dark))
    } else {
        view.text = "Sold Out"
        view.setTextColor(view.context.getColor(android.R.color.holo_red_dark))
    }
}

@BindingAdapter("set_rating")
fun setRating(view: RatingBar, rating: Double) {
    view.rating = rating.toFloat()
}


@BindingAdapter("setDiscountPercentage")
fun setDiscountPercent(view: TextView, discountPercentage: Double?) {
    if (discountPercentage != null) {
        val formattedText = "%$discountPercentage"
        view.text = formattedText
    } else {
        view.text = ""
    }
}

@BindingAdapter("setStrikeThrough")
fun setStrikeThrough(textView: TextView, strikeThrough: Boolean) {
    if (strikeThrough) {
        textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    } else {
        textView.paintFlags = textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
    }
}

@BindingAdapter("textViewVisibility")
fun setDiscountedPriceVisibility(textView: TextView, discountPercentage: Double?) {
    if (discountPercentage != null && discountPercentage > 0) {
        textView.visibility = View.VISIBLE
    } else {
        textView.visibility = View.INVISIBLE
    }
}


@BindingAdapter("discount_percentage", "product_price", requireAll = true)
fun setDiscountedPrice(textView: TextView, discountPercentage: Double?, price: Double?) {
    if (price != null && discountPercentage != null) {
        val newPrice = price * (1 - (discountPercentage / 100.0))
        val decimalFormat = DecimalFormat("#.##")
        val formattedPrice = decimalFormat.format(newPrice)
        textView.setText(formattedPrice)
    }
}

@BindingAdapter("setDiscountedPriceColor")
fun setDiscountedPriceColor(textView: TextView, discountPercentage: Double) {
    val context = textView.context
    val colorResId = if (discountPercentage > 0) {
        R.color.gray // Eğer indirim varsa turuncu renk
    } else {
        R.color.orange // Eğer indirim yoksa gri renk
    }
    val color = ContextCompat.getColor(context, colorResId)
    textView.setTextColor(color)
}

@BindingAdapter("set_dollar")
fun setDollarIcon(view: TextView, price: Double?) {
    price?.let {
        val text = "$$price"
        view.setText(text)
    }
}

@BindingAdapter("setQuantityText")
fun setQuantityText(view: TextView, quantity: Int) {
    view.text = "Quantity: $quantity"
}

@BindingAdapter("setTotalPriceText")
fun setTotalPriceText(view: TextView, totalPrice: Double) {
    view.text = "Total Price: $totalPrice"
}

@BindingAdapter("discount_percentage", "product_price", "product_quantity", requireAll = true)
fun setDiscountedPriceMultipleQuantity(textView: TextView, discountPercentage: Double?, price: Double?, quantity: Int) {
    if (price != null && discountPercentage != null) {
        val newPrice = price * (1 - (discountPercentage / 100.0))
        val decimalFormat = DecimalFormat("#.##")
        val withQuantity = quantity * newPrice
        val formattedPrice = decimalFormat.format(withQuantity)
        textView.setText("Total Price: $$formattedPrice")
    }
}