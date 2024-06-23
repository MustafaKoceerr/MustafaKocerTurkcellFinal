package com.example.mustafakocer.ui.home.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.mustafakocer.R
import com.example.mustafakocer.data.model.Category
import com.example.mustafakocer.databinding.RecyclerRowCategoryBinding
import com.example.mustafakocer.ui.home.fragment.CategoryFragmentDirections


class CategoryAdapter(
    private val categoryList: List<Category>
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(
        val recyclerRowCategoryBinding: RecyclerRowCategoryBinding
    ) : RecyclerView.ViewHolder(recyclerRowCategoryBinding.root)


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
            val action =
                CategoryFragmentDirections.actionCategoryFragmentToProductsByCategoryFragment(categoryList.get(position).slug)
            Navigation.findNavController(holder.recyclerRowCategoryBinding.root).navigate(action)
        }

    }


}