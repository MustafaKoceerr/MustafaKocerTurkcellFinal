package com.example.mustafakocer.presentation.feature_cart

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mustafakocer.databinding.RecylerviewRowProductGridBinding
import com.example.mustafakocer.domain.model.CartItem

/**
 * Sepet ürünlerini göstermek için kullanılan ListAdapter.
 * Home/Search ekranlarındaki ile aynı layout'u kullanır ama farklı bir veri modeli (CartItem)
 * ve farklı tıklama olayları ile çalışır.
 *
 * @param onIncreaseClick '+' butonuna tıklandığında tetiklenir.
 * @param onDecreaseClick '-' butonuna tıklandığında tetiklenir.
 * @param onProductClick Ürün kartının tamamına tıklandığında tetiklenir.
 */
class CartListAdapter(
    private val onIncreaseClick: (Int) -> Unit,
    private val onDecreaseClick: (Int) -> Unit,
    private val onProductClick: (Int) -> Unit,
) : ListAdapter<CartItem, CartListAdapter.CartViewHolder>(CartDiffCallback) {

    inner class CartViewHolder(private val binding: RecylerviewRowProductGridBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            // Tıklama dinleyicilerini ViewHolder oluşturulurken ayarla.
            binding.root.setOnClickListener {
                getItemAtPosition()?.let { onProductClick(it.product.id) }
            }
            binding.btnPlus.setOnClickListener {
                getItemAtPosition()?.let { onIncreaseClick(it.product.id) }
            }
            binding.btnMinus.setOnClickListener {
                getItemAtPosition()?.let { onDecreaseClick(it.product.id) }
            }
        }

        private fun getItemAtPosition(): CartItem? {
            val position = bindingAdapterPosition
            return if (position != RecyclerView.NO_POSITION) getItem(position) else null
        }

        fun bind(cartItem: CartItem) {
            val product = cartItem.product
            binding.txtTitle.text = product.title
            binding.txtDiscountedPrice.text = product.discountedPrice
            binding.ratingBar.rating = product.rating
            binding.txtQuantity.text = cartItem.quantity.toString()

            // Miktar her zaman 1 veya daha fazla olacağı için miktar ve eksi butonu hep görünür.
            binding.txtQuantity.visibility = View.VISIBLE
            binding.btnMinus.visibility = View.VISIBLE

            binding.txtPrice.apply {
                text = product.price
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }

            Glide.with(binding.root.context)
                .load(product.thumbnailUrl)
                .into(binding.imgProduct)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = RecylerviewRowProductGridBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    private object CartDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            // İki sepet öğesinin aynı olup olmadığını ürün ID'sine göre kontrol et.
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            // İçeriklerinin aynı olup olmadığını kontrol et.
            // `CartItem` bir data class olduğu için bu, hem product hem de quantity'yi karşılaştırır.
            return oldItem == newItem
        }
    }
}