package com.example.mustafakocer.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mustafakocer.R
import com.example.mustafakocer.data.model.CartResponse
import com.example.mustafakocer.databinding.RecyclerRowProductsInCartBinding
import com.example.mustafakocer.databinding.RecylerRowOrderBinding

class OrderProductAdapter(
    private val cartsInOrder: List<CartResponse>) :
    RecyclerView.Adapter<OrderProductAdapter.OrderCartViewHolder>() {

    inner class OrderCartViewHolder(val recyclerRowOrderBinding: RecylerRowOrderBinding) :
        RecyclerView.ViewHolder(recyclerRowOrderBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderCartViewHolder {
        return OrderCartViewHolder(
            DataBindingUtil.inflate<RecylerRowOrderBinding>(
                LayoutInflater.from(parent.context),
                R.layout.recyler_row_order,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return cartsInOrder.size
    }

    override fun onBindViewHolder(holder: OrderCartViewHolder, position: Int) {

        holder.recyclerRowOrderBinding.order = cartsInOrder.get(position)
    }
}