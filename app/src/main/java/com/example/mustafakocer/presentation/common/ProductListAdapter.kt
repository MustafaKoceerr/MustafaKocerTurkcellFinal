package com.example.mustafakocer.presentation.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.mustafakocer.databinding.RecylerRowProductGridBinding
import com.example.mustafakocer.domain.model.Product // Artık CartItem değil, Product

// ViewHolder'ı da bu dosya içine alarak daha düzenli hale getirebiliriz.
class ProductListAdapter(
    // DEĞİŞTİ: Artık karmaşık bir sealed class yerine basit bir lambda alıyoruz.
    private val onProductClick: (productId: Int) -> Unit,
) : PagingDataAdapter<Product, ProductListAdapter.ProductViewHolder>(ProductDiffCallback) {

    inner class ProductViewHolder(private val binding: RecylerRowProductGridBinding) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

        init {
            // Tıklama olayını burada yönetiyoruz.
            binding.root.setOnClickListener {
                // Pozisyonun geçerli olduğundan ve bir ürün olduğundan emin ol.
                val position = bindingAdapterPosition
                if (position != androidx.recyclerview.widget.RecyclerView.NO_POSITION) {
                    getItem(position)?.let { product ->
                        // Lambda'yı ürünün ID'si ile çağır.
                        onProductClick(product.id)
                    }
                }
            }
        }

        fun bind(product: Product) {
            binding.apply {
                txtTitle.text = product.title
                ratingBar.rating = product.rating
                txtRatingValue.text = product.rating.toString()
                txtDiscountedPrice.text = product.discountedPrice
                txtPrice.text = product.price
                // TODO: Fiyatın üzerini çizme ve rozet mantıkları eklenecek.

                // Görsel: animasyonu kapat, oran sabitse zıplama olmaz (XML’de ratio önerilir)
                Glide.with(root.context)
                    .load(product.thumbnailUrl)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .dontAnimate()
                    .into(imgProduct)
                // Sepetle ilgili tüm görünümleri gizliyoruz.
                badgeDiscount.isVisible = false // Bu mantık daha sonra eklenebilir.
                badgeStock.isVisible = false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = RecylerRowProductGridBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    private object ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}