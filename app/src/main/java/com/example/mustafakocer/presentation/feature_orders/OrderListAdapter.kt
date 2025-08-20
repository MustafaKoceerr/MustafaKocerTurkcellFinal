package com.example.mustafakocer.presentation.feature_orders

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mustafakocer.R
import com.example.mustafakocer.databinding.RecyclerRowOrderBinding
import com.example.mustafakocer.domain.model.Order

/**
 * A private extension function to safely parse a formatted price string into a Double.
 */
private fun String.parsePriceToDouble(): Double {
    return this.replace(Regex("[$,₺]"), "").replace(",", "").toDoubleOrNull() ?: 0.0
}

/**
 * A [PagingDataAdapter] for displaying a list of [Order] items in a RecyclerView.
 *
 * @param onOrderClick A lambda to be invoked when an order card is clicked.
 */
class OrderListAdapter(
    private val onOrderClick: (Order) -> Unit
) : PagingDataAdapter<Order, OrderListAdapter.OrderViewHolder>(OrderDiffCallback) {

    inner class OrderViewHolder(private val binding: RecyclerRowOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    getItem(position)?.let { order ->
                        onOrderClick(order)
                    }
                }
            }
        }

        fun bind(order: Order) {
            binding.apply {
                txtOrderId.text = order.id.toString()
                txtDiscountedTotal.text = order.discountedTotal
                txtOriginalTotal.text = order.total

                val originalTotal = order.total.parsePriceToDouble()
                val discountedTotal = order.discountedTotal.parsePriceToDouble()

                val hasDiscount = discountedTotal < originalTotal
                txtOriginalTotal.isVisible = hasDiscount
                if (hasDiscount) {
                    txtOriginalTotal.paintFlags = txtOriginalTotal.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    txtOriginalTotal.paintFlags = txtOriginalTotal.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }

                txtTotalProducts.text = root.context.getString(
                    R.string.order_product_count_format,
                    order.totalProducts,
                    order.totalQuantity
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = RecyclerRowOrderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    private object OrderDiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }
}