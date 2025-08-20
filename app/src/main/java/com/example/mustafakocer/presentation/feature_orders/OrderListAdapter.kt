package com.example.mustafakocer.presentation.feature_orders

import android.graphics.Paint
import android.os.Build
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
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class OrderListAdapter(
    private val onOrderClick: (Order) -> Unit
) : PagingDataAdapter<Order, OrderListAdapter.OrderViewHolder>(OrderDiffCallback) {

    // Geçerli locale'i güvenli şekilde al
    private fun currentLocale(binding: RecyclerRowOrderBinding): Locale {
        val cfg = binding.root.resources.configuration
        return cfg.locales[0]
    }

    // USD göstermek istiyoruz ama ayırıcılar (binlik/ondalık) cihazın locale'ine göre olsun.
    private fun currencyFormatter(binding: RecyclerRowOrderBinding): NumberFormat =
        NumberFormat.getCurrencyInstance(currentLocale(binding)).apply {
            currency = Currency.getInstance("USD") // <- cihaz para birimini istiyorsan bu satırı sil
            minimumFractionDigits = 2
            maximumFractionDigits = 2
            isGroupingUsed = true
        }

    inner class OrderViewHolder(private val binding: RecyclerRowOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) getItem(pos)?.let(onOrderClick)
            }
        }

        fun bind(order: Order) = with(binding) {
            val fmt = currencyFormatter(this)

            val originalTotal = order.total.parsePriceToDouble()
            val discountedTotal = order.discountedTotal.parsePriceToDouble()

            txtOrderId.text = order.id.toString()
            txtDiscountedTotal.text = fmt.format(discountedTotal)
            txtOriginalTotal.text = fmt.format(originalTotal)

            val hasDiscount = discountedTotal < originalTotal
            txtOriginalTotal.isVisible = hasDiscount
            txtOriginalTotal.paintFlags = if (hasDiscount) {
                txtOriginalTotal.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                txtOriginalTotal.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            txtTotalProducts.text = root.context.getString(
                R.string.order_product_count_format,
                order.totalProducts,
                order.totalQuantity
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = RecyclerRowOrderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        getItem(position)?.let(holder::bind)
    }

    private object OrderDiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(old: Order, new: Order) = old.id == new.id
        override fun areContentsTheSame(old: Order, new: Order) = old == new
    }
}
