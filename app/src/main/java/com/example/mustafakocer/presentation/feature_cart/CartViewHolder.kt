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
    private val displayFormat = DecimalFormat("#,##0.00")

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
        this.currentCartItem = cartItem
        val product = cartItem.product

        binding.apply {
            txtTitle.text = product.title
            txtQuantity.text = cartItem.quantity.toString()

            // 1. Mapper'dan gelen güvenli String'i Double'a çevir.
            // Bu, hesaplama için kullanılacak ham değerdir.
            val priceAsDouble = product.discountedPrice
                .replace("$", "")
                .toDoubleOrNull() ?: 0.0

            // 2. DÜZELTME: Birim fiyatı, kullanıcıya göstermek için formatla.
            // Örnek: 8.94 -> "₺8,94"
            val formattedPricePerUnit = "₺${displayFormat.format(priceAsDouble)}"
            txtPricePerUnit.text = "$formattedPricePerUnit / adet"

            // 3. Satır toplamını hesapla ve onu da gösterim için formatla.
            val lineTotal = priceAsDouble * cartItem.quantity
            txtLineTotal.text = "₺${displayFormat.format(lineTotal)}"

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