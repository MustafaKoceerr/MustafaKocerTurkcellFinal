package com.example.mustafakocer.presentation.feature_cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mustafakocer.R
import com.example.mustafakocer.databinding.RecylerRowProductCartBinding
import com.example.mustafakocer.domain.model.CartItem
import com.example.mustafakocer.presentation.common.util.parsePriceToDouble
import java.text.NumberFormat
import java.util.Locale

class CartViewHolder(
    private val binding: RecylerRowProductCartBinding,
    private val onEvent: (CartEvent) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    private var currentCartItem: CartItem? = null

    init {
        binding.btnPlus.setOnClickListener {
            currentCartItem?.let { onEvent(CartEvent.OnIncrease(it.product.id)) }
        }
        binding.btnMinus.setOnClickListener {
            currentCartItem?.let { onEvent(CartEvent.OnDecrease(it.product.id)) }
        }
        binding.btnRemove.setOnClickListener {
            currentCartItem?.let { onEvent(CartEvent.OnRemove(it.product.id)) }
        }
        binding.root.setOnClickListener {
            currentCartItem?.let { onEvent(CartEvent.OnProductClick(it.product.id)) }
        }
    }

    fun bind(cartItem: CartItem) {
        currentCartItem = cartItem
        val product = cartItem.product
        val context = binding.root.context

        // Geçerli locale ile para formatı (binlik ayırıcı aktif)
        val locale = context.resources.configuration.locales[0]
        val currency = NumberFormat.getCurrencyInstance(locale).apply {
            isGroupingUsed = true
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }

        binding.apply {
            txtTitle.text = product.title
            txtQuantity.text = cartItem.quantity.toString()

            val unitPrice = product.discountedPrice.parsePriceToDouble()
            val unitPriceText = currency.format(unitPrice)

            val lineTotal = unitPrice * cartItem.quantity
            val lineTotalText = currency.format(lineTotal)

            // İsimler değişmedi: artık %1$s bekliyorlar
            txtPricePerUnit.text =
                context.getString(R.string.price_per_unit_format_dollar, unitPriceText)
            txtLineTotal.text =
                context.getString(R.string.price_format_dollar, lineTotalText)

            Glide.with(context).load(product.thumbnailUrl).into(imgProduct)
        }
    }


    companion object {
        fun create(parent: ViewGroup, onEvent: (CartEvent) -> Unit): CartViewHolder {
            val binding = RecylerRowProductCartBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return CartViewHolder(binding, onEvent)
        }
    }
}
