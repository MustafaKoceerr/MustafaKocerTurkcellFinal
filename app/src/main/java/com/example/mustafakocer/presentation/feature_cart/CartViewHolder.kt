package com.example.mustafakocer.presentation.feature_cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mustafakocer.R
import com.example.mustafakocer.databinding.RecylerRowProductCartBinding
import com.example.mustafakocer.domain.model.CartItem
import com.example.mustafakocer.presentation.common.util.parsePriceToDouble

/**
 * A [RecyclerView.ViewHolder] for displaying a single [CartItem].
 *
 * @param binding The ViewBinding instance for the item layout.
 * @param onEvent The callback to send [CartEvent]s for user interactions.
 */
class CartViewHolder(
    private val binding: RecylerRowProductCartBinding,
    private val onEvent: (CartEvent) -> Unit,
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
        val context = binding.root.context // Context'i al

        binding.apply {
            txtTitle.text = product.title
            txtQuantity.text = cartItem.quantity.toString()

            val priceAsDouble = product.discountedPrice.parsePriceToDouble()

            // strings.xml'deki kaynakları kullanarak metinleri formatla
            val formattedPricePerUnit =
                context.getString(R.string.price_per_unit_format_dollar, priceAsDouble)
            txtPricePerUnit.text = formattedPricePerUnit

            val lineTotal = priceAsDouble * cartItem.quantity
            val formattedLineTotal = context.getString(R.string.price_format_dollar, lineTotal)
            txtLineTotal.text = formattedLineTotal

            Glide.with(context)
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