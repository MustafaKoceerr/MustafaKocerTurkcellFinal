package com.example.mustafakocer.presentation.feature_orders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mustafakocer.R
import com.example.mustafakocer.databinding.RecyclerRowOrderProductBinding
import com.example.mustafakocer.domain.model.OrderProduct

/**
 * A [ListAdapter] for displaying a list of [OrderProduct] items within an order detail screen.
 *
 * @param onProductClick A lambda to be invoked when a product item is clicked.
 */
class OrderProductListAdapter(
    private val onProductClick: (productId: Int) -> Unit,
) : ListAdapter<OrderProduct, OrderProductListAdapter.OrderProductViewHolder>(
    OrderProductDiffCallback
) {

    inner class OrderProductViewHolder(private val binding: RecyclerRowOrderProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val product = getItem(position)
                    onProductClick(product.id)
                }
            }
        }

        fun bind(product: OrderProduct) {
            binding.apply {
                txtProductTitle.text = product.title
                txtPricePerUnit.text = product.discountedPricePerUnit
                txtQuantity.text = root.context.getString(
                    R.string.order_item_quantity_format,
                    product.quantity
                )

                Glide.with(root.context)
                    .load(product.thumbnail)
                    .into(imgProduct)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderProductViewHolder {
        val binding = RecyclerRowOrderProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private object OrderProductDiffCallback : DiffUtil.ItemCallback<OrderProduct>() {
        override fun areItemsTheSame(oldItem: OrderProduct, newItem: OrderProduct): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: OrderProduct, newItem: OrderProduct): Boolean {
            return oldItem == newItem
        }
    }
}