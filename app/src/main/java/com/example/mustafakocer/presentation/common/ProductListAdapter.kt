package com.example.mustafakocer.presentation.common

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.mustafakocer.databinding.RecylerRowProductGridBinding
import com.example.mustafakocer.domain.model.Product

/**
 * A private extension function to safely parse a formatted price string (e.g., "$1,234.56")
 * into a Double, handling various currency symbols and separators.
 */
private fun String.parsePriceToDouble(): Double {
    return this.replace(Regex("[$,₺]"), "").replace(",", "").toDoubleOrNull() ?: 0.0
}

/**
 * A [PagingDataAdapter] for displaying a grid of [Product] items.
 *
 * @param onProductClick A lambda function to be invoked when a product item is clicked.
 */
class ProductListAdapter(
    private val onProductClick: (productId: Int) -> Unit,
) : PagingDataAdapter<Product, ProductListAdapter.ProductViewHolder>(ProductDiffCallback) {

    /**
     * ViewHolder for a single product item in the grid.
     * It handles data binding and click events for its item.
     */
    inner class ProductViewHolder(private val binding: RecylerRowProductGridBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    getItem(position)?.let { product ->
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

                val originalPriceValue = product.price.parsePriceToDouble()
                val discountedPriceValue = product.discountedPrice.parsePriceToDouble()

                if (discountedPriceValue < originalPriceValue) {
                    txtPrice.isVisible = true
                    txtPrice.paintFlags = txtPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    // This else block is crucial to prevent incorrect states on recycled views.
                    txtPrice.isVisible = false
                    txtPrice.paintFlags = txtPrice.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }

                Glide.with(root.context)
                    .load(product.thumbnailUrl)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .dontAnimate()
                    .into(imgProduct)

                badgeDiscount.isVisible = false
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

    /**
     * A [DiffUtil.ItemCallback] for calculating the difference between two non-null items in a list.
     * This is essential for the [PagingDataAdapter] to efficiently update the RecyclerView.
     */
    private object ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}