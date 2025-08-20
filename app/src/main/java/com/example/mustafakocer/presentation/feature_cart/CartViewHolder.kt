package com.example.mustafakocer.presentation.feature_cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mustafakocer.databinding.RecylerRowProductCartBinding
import com.example.mustafakocer.domain.model.CartItem
import java.text.DecimalFormat

/**
 * A reusable, file-level formatter to avoid creating new instances for each ViewHolder.
 */
private val priceFormatter = DecimalFormat("₺#,##0.00")

/**
 * A private extension function to safely parse a formatted price string into a Double.
 */
private fun String.parsePriceToDouble(): Double {
    return this.replace(Regex("[$,₺]"), "").replace(",", "").toDoubleOrNull() ?: 0.0
}

/**
 * A [RecyclerView.ViewHolder] for displaying a single [CartItem].
 *
 * @param binding The ViewBinding instance for the item layout.
 * @param onEvent The callback to send [CartEvent]s for user interactions.
 */
class CartViewHolder(
    private val binding: RecylerRowProductCartBinding,
    private val onEvent: (CartEvent) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private var currentCartItem: CartItem? = null

    init {
        // Listeners are set only once for performance, referencing `currentCartItem`.
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

    /**
     * Binds a [CartItem] to the views in the ViewHolder.
     */
    fun bind(cartItem: CartItem) {
        this.currentCartItem = cartItem
        val product = cartItem.product

        binding.apply {
            txtTitle.text = product.title
            txtQuantity.text = cartItem.quantity.toString()

            val priceAsDouble = product.discountedPrice.parsePriceToDouble()
            val formattedPricePerUnit = priceFormatter.format(priceAsDouble)
            txtPricePerUnit.text = "$formattedPricePerUnit / unit"

            val lineTotal = priceAsDouble * cartItem.quantity
            txtLineTotal.text = priceFormatter.format(lineTotal)

            Glide.with(root.context)
                .load(product.thumbnailUrl)
                .into(imgProduct)
        }
    }

    companion object {
        /**
         * A factory method to create a new [CartViewHolder] instance.
         */
        fun create(parent: ViewGroup, onEvent: (CartEvent) -> Unit): CartViewHolder {
            val binding = RecylerRowProductCartBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return CartViewHolder(binding, onEvent)
        }
    }
}