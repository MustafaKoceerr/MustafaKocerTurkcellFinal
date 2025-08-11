package com.example.mustafakocer.presentation.common

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mustafakocer.databinding.RecylerviewRowProductGridBinding
import com.example.mustafakocer.domain.model.Product

class ProductListAdapter(
    private val onProductClick: (Product) -> Unit,
    private val onAddToCartClick: (Product) -> Unit,
    private val onRemoveFromCartClick: (Product) -> Unit,
) : PagingDataAdapter<Product, ProductListAdapter.ProductViewHolder>(ProductDiffCallback) {

    // YENİ: Sepet durumunu (productId -> quantity) tutacak olan harita.
    // Başlangıçta boş bir harita olarak ayarlanır.
    private var cartMap: Map<Int, Int> = emptyMap()

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
                getItem(position)
            } else {
                null
            }
        }

        // DEĞİŞTİ: bind metodu artık sadece Product değil, o ürünün sepetteki miktarını da alıyor.
        fun bind(product: Product, quantityInCart: Int) {
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

            // YENİ: Miktara göre UI'ı güncelleme mantığı
            if (quantityInCart > 0) {
                // Eğer ürün sepette varsa, miktarını göster ve eksi butonunu görünür yap.
                binding.txtQuantity.text = quantityInCart.toString()
                binding.txtQuantity.visibility = View.VISIBLE
                binding.btnMinus.visibility = View.VISIBLE
            } else {
                // Eğer ürün sepette yoksa, miktar ve eksi butonunu gizle.
                binding.txtQuantity.visibility = View.GONE
                binding.btnMinus.visibility = View.GONE
            }
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
        getItem(position)?.let { product ->
            // Haritadan bu ürünün miktarını al. Eğer ürün haritada yoksa, miktarı 0 kabul et.
            val quantity = cartMap[product.id] ?: 0
            holder.bind(product, quantity)
        }
    }

    // YENİ: Fragment'tan ViewModel'deki değişiklikleri bu adaptöre bildirmek için kullanılacak fonksiyon.
    fun updateCartMap(newCartMap: Map<Int, Int>) {
        val oldCartMap = this.cartMap
        this.cartMap = newCartMap

        // Eğer harita gerçekten değiştiyse, güncelleme yap.
        if (oldCartMap != newCartMap) {
            // DEĞİŞTİ: notifyDataSetChanged() yerine bunu kullanıyoruz.
            // Bu, RecyclerView'a tüm öğelerin potansiyel olarak değiştiğini,
            // ancak pozisyonlarının aynı kaldığını söyler.
            // RecyclerView, sadece görünürdeki item'lar için onBindViewHolder'ı
            // tekrar çağırarak UI'ı verimli bir şekilde günceller.
            notifyItemRangeChanged(0, itemCount)
        }
    }

    private object ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            // DİKKAT: İçerik kontrolünü sadece product'a göre yapıyoruz.
            // Sepet miktarı bu karşılaştırmaya dahil değil, çünkü o ayrı bir state.
            return oldItem == newItem
        }
    }
}