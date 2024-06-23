package com.example.mustafakocer.ui.home.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mustafakocer.R
import com.example.mustafakocer.data.model.Category
import com.example.mustafakocer.data.model.Product
import com.example.mustafakocer.databinding.RecyclerRowCategoryBinding


class CategoryAdapter(
    private val categoryList: List<Category>
): RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(
        val recyclerRowCategoryBinding : RecyclerRowCategoryBinding
    ): RecyclerView.ViewHolder(recyclerRowCategoryBinding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            DataBindingUtil.inflate<RecyclerRowCategoryBinding>(
                LayoutInflater.from(parent.context),
                R.layout.recycler_row_category,
                parent,
                false
            )
        )

    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {

        holder.recyclerRowCategoryBinding.category = categoryList.get(position)
        // bind xml layout with our categories
        // todo setoncliklistener ile başka bir fragment'a götür ve kategorideki ürünleri listelet

        holder.recyclerRowCategoryBinding.root.setOnClickListener {
            Log.d("bakalım", "bakalım,bakalım,bakalım ${categoryList.get(position)}")
        }

    }


}