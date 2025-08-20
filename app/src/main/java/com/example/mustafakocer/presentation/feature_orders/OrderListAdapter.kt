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
import com.example.mustafakocer.presentation.common.util.parsePriceToDouble


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
                val context = root.context // Context'i bir kere alalım

                // Fiyat string'lerini Double değerlere çevir
                val originalTotal = order.total.parsePriceToDouble()
                val discountedTotal = order.discountedTotal.parsePriceToDouble()

                // Değerleri strings.xml'deki kaynak ile Dolar ($) formatında yeniden oluştur
                val formattedOriginalTotal = context.getString(R.string.price_format_dollar, originalTotal)
                val formattedDiscountedTotal = context.getString(R.string.price_format_dollar, discountedTotal)

                // UI elemanlarına formatlanmış yeni değerleri ata
                txtOrderId.text = order.id.toString()
                txtDiscountedTotal.text = formattedDiscountedTotal
                txtOriginalTotal.text = formattedOriginalTotal

                // İndirim olup olmadığını kontrol et ve üstü çizili metni ayarla
                val hasDiscount = discountedTotal < originalTotal
                txtOriginalTotal.isVisible = hasDiscount
                if (hasDiscount) {
                    txtOriginalTotal.paintFlags = txtOriginalTotal.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    txtOriginalTotal.paintFlags = txtOriginalTotal.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }

                // Ürün sayısı metnini ayarla
                txtTotalProducts.text = context.getString(
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