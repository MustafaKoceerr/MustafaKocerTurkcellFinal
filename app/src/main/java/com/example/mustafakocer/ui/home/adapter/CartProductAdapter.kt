package com.example.mustafakocer.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mustafakocer.R
import com.example.mustafakocer.data.model.ProductsInCart
import com.example.mustafakocer.databinding.RecyclerRowProductsInCartBinding

class CartProductAdapter(
    private val productsInCartList: List<ProductsInCart>
) :
    RecyclerView.Adapter<CartProductAdapter.CartProductViewHolder>() {

    inner class CartProductViewHolder(val recyclerRowProductsInCartBinding: RecyclerRowProductsInCartBinding) :
        RecyclerView.ViewHolder(recyclerRowProductsInCartBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductViewHolder {
        return CartProductViewHolder(
            DataBindingUtil.inflate<RecyclerRowProductsInCartBinding>(
                LayoutInflater.from(parent.context),
                R.layout.recycler_row_products_in_cart,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return productsInCartList.size
    }

    override fun onBindViewHolder(holder: CartProductViewHolder, position: Int) {

        holder.recyclerRowProductsInCartBinding.productsInCart = productsInCartList.get(position)
    }
}