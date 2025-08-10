package com.example.mustafakocer.presentation.common

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mustafakocer.databinding.RecylerviewRowProductGridBinding // YENİ LAYOUT BINDING'İ
import com.example.mustafakocer.domain.model.Product

/**
 * Ürün listelerini göstermek için kullanılacak, yeniden kullanılabilir,
 * yüksek mertebeli fonksiyonlar (HOF) ile çalışan modern bir ListAdapter.
 *
 * @param onProductClick Bir ürün kartının tamamına tıklandığında tetiklenir.
 * @param onAddToCartClick Bir ürünün 'sepete ekle' (+) butonuna tıklandığında tetiklenir.
 * @param onRemoveFromCartClick Bir ürünün 'sepetten çıkar' (-) butonuna tıklandığında tetiklenir.
 */
class ProductListAdapter(
    private val onProductClick: (Product) -> Unit,
    private val onAddToCartClick: (Product) -> Unit,
    private val onRemoveFromCartClick: (Product) -> Unit,
) : PagingDataAdapter<Product, ProductListAdapter.ProductViewHolder>(ProductDiffCallback) { // 2. Değişiklik burada

    inner class ProductViewHolder(private val binding: RecylerviewRowProductGridBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                getItemAtPosition()?.let { onProductClick(it) }
            }
            binding.btnPlus.setOnClickListener {
                getItemAtPosition()?.let { onAddToCartClick(it) }
            }
            binding.btnMinus.setOnClickListener {
                getItemAtPosition()?.let { onRemoveFromCartClick(it) }
            }
        }

        private fun getItemAtPosition(): Product? {
            val position = bindingAdapterPosition
            return if (position != RecyclerView.NO_POSITION) {
                getItem(position) // 3. getItem() PagingDataAdapter'ın kendi metodu
            } else {
                null
            }
        }

        fun bind(product: Product) {
            binding.txtTitle.text = product.title
            binding.txtDiscountedPrice.text = product.discountedPrice
            binding.ratingBar.rating = product.rating
            binding.txtStock.text = "Stock: ${product.stock}"

            binding.txtPrice.apply {
                text = product.price
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }

            Glide.with(binding.root.context)
                .load(product.thumbnailUrl)
                .into(binding.imgProduct)

            // TODO: Sepet miktarını CartViewModel'den gelen state ile güncelle
            binding.txtQuantity.text = "1"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = RecylerviewRowProductGridBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        // PagingDataAdapter, yer tutucular (placeholders) gösterirken null dönebilir.
        // Bu yüzden null kontrolü yapmak önemlidir.
        getItem(position)?.let { holder.bind(it) }
    }

    // 4. DiffUtil.ItemCallback hala aynı şekilde gereklidir ve kullanılır.
    private object ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}