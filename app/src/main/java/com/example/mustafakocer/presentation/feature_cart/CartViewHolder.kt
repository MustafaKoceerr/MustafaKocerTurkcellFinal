package com.example.mustafakocer.presentation.feature_cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mustafakocer.databinding.RecylerRowProductCartBinding
import com.example.mustafakocer.domain.model.CartItem
import java.text.DecimalFormat

class CartViewHolder(
    private val binding: RecylerRowProductCartBinding,
    private val onEvent: (CartEvent) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    // O anki ViewHolder'ın bağlandığı CartItem'ı tutacak bir değişken.
    // Tıklama anında doğru ID'ye erişmek için kullanacağız.
    private var currentCartItem: CartItem? = null

    init {
        // Tıklama dinleyicileri ViewHolder oluşturulurken SADECE BİR KEZ ayarlanır.
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
        // 1. Tıklama olaylarının doğru ID'yi kullanabilmesi için o anki item'ı sakla.
        this.currentCartItem = cartItem

        // 2. UI bileşenlerini veriye göre güncelle.
        val product = cartItem.product
        val priceFormat = DecimalFormat("$#,##0.00")

        binding.apply {
            txtTitle.text = product.title
            txtQuantity.text = cartItem.quantity.toString()
            txtPricePerUnit.text = "${product.discountedPrice} / adet"

            val priceAsDouble = product.discountedPrice.replace("$", "").replace(",", "").toDoubleOrNull() ?: 0.0
            val lineTotal = priceAsDouble * cartItem.quantity
            txtLineTotal.text = priceFormat.format(lineTotal)

            Glide.with(root.context)
                .load(product.thumbnailUrl)
                .into(imgProduct)
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