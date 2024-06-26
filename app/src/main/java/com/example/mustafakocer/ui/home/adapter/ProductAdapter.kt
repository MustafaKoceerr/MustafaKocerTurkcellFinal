package com.example.mustafakocer.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mustafakocer.R
import com.example.mustafakocer.data.model.Product
import com.example.mustafakocer.databinding.RecylerviewRowVerticalBinding

class ProductAdapter(
    private val productList: List<Product>
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(
        val recylerviewRowVerticalBinding: RecylerviewRowVerticalBinding
    ) : RecyclerView.ViewHolder(recylerviewRowVerticalBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            DataBindingUtil.inflate<RecylerviewRowVerticalBinding>(
                LayoutInflater.from(parent.context),
                R.layout.recylerview_row_vertical,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {

        holder.recylerviewRowVerticalBinding.product = productList.get(position)
        // bind xml layout with our products
        // todo sepet simgesi ve favorite simgesi koy ve dinle


    }


    /*
      fun updateProducts(newProducts: List<Product>) {
        productList = newProducts
        notifyDataSetChanged()
    }
     */
    //                         productAdapter.updateProducts(products)  // Assuming you have this method in your adapter
    // bunu da flow'dan verileri alınca çağırıcam


}