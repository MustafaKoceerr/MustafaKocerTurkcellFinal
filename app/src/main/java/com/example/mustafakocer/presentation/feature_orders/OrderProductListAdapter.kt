package com.example.mustafakocer.presentation.feature_orders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mustafakocer.databinding.RecyclerRowOrderProductBinding
import com.example.mustafakocer.domain.model.OrderProduct

class OrderProductListAdapter :
    ListAdapter<OrderProduct, OrderProductListAdapter.OrderProductViewHolder>(OrderProductDiffCallback) {

    inner class OrderProductViewHolder(private val binding: RecyclerRowOrderProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: OrderProduct) {
            binding.txtProductTitle.text = product.title
            binding.txtPricePerUnit.text = product.discountedPricePerUnit
            binding.txtQuantity.text = "Adet: ${product.quantity}"

            Glide.with(binding.root.context)
                .load(product.thumbnail)
                .into(binding.imgProduct)
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