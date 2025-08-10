package com.example.mustafakocer.presentation.feature_orders

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mustafakocer.R
import com.example.mustafakocer.databinding.RecyclerRowOrderBinding
import com.example.mustafakocer.domain.model.Order

/**
 * Sipariş listesini RecyclerView'da göstermek için kullanılan PagingDataAdapter.
 *
 * @param onOrderClick Bir sipariş kartına tıklandığında çağrılacak olan lambda.
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
            binding.txtOrderId.text = order.id.toString()
            binding.txtDiscountedTotal.text = order.discountedTotal

            // YENİ EKLENEN KISIM
            binding.txtOriginalTotal.text = order.total
            binding.txtOriginalTotal.paintFlags = binding.txtOriginalTotal.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

            binding.txtTotalProducts.text = binding.root.context.getString(
                R.string.order_product_count_format,
                order.totalProducts,
                order.totalQuantity
            )
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