package com.example.mustafakocer.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mustafakocer.R
import com.example.mustafakocer.data.model.Product
import com.example.mustafakocer.databinding.RecylerviewRowBinding
import com.example.mustafakocer.ui.home.LikeButtonClickListener

class ProductAdapterHome(
    private val productList: List<Product>,
    private val likeButtonClickListener: LikeButtonClickListener,
    private val likedProductsIdList: List<Int>
):RecyclerView.Adapter<ProductAdapterHome.ProductViewHolder2>() {

    inner class ProductViewHolder2(
        val recylerviewRowBinding : RecylerviewRowBinding
    ) : RecyclerView.ViewHolder(recylerviewRowBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder2 {
        return ProductViewHolder2(
            DataBindingUtil.inflate<RecylerviewRowBinding>(
                LayoutInflater.from(parent.context),
                R.layout.recylerview_row,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder2, position: Int) {

        holder.recylerviewRowBinding.product = productList.get(position)
        // bind xml layout with our products
        // todo sepet simgesi ve favorite simgesi koy ve dinle

        val isLiked : Boolean= productList.get(position).id in likedProductsIdList
        if (isLiked) {
            holder.recylerviewRowBinding.btnLike.setImageResource(R.drawable.ic_heart_filled)
        } else {
            holder.recylerviewRowBinding.btnLike.setImageResource(R.drawable.ic_heart_empty)
        }
        holder.recylerviewRowBinding.btnLike.setOnClickListener {
            likeButtonClickListener.onRecyclerViewItemClick(it!!, productList.get(position))
        }
        holder.recylerviewRowBinding.btnPlus.setOnClickListener {
            likeButtonClickListener.onRecyclerViewItemClick(it!!, productList.get(position))
        }
        holder.recylerviewRowBinding.btnMinus.setOnClickListener {
            likeButtonClickListener.onRecyclerViewItemClick(it!!, productList.get(position))
        }

    }

}