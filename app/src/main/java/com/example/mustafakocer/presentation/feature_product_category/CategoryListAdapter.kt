package com.example.mustafakocer.presentation.feature_product_category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mustafakocer.databinding.RecyclerRowCategoryBinding
import com.example.mustafakocer.domain.model.Category

/**
 * Kategori listesini RecyclerView'da göstermek için kullanılan ListAdapter.
 *
 * @param onCategoryClick Bir kategori öğesine tıklandığında çağrılacak olan lambda.
 *                        Tıklanan kategorinin 'slug' adını parametre olarak alır.
 */
class CategoryListAdapter(
    private val onCategoryClick: (String) -> Unit
) : ListAdapter<Category, CategoryListAdapter.CategoryViewHolder>(CategoryDiffCallback) {

    inner class CategoryViewHolder(private val binding: RecyclerRowCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            // ViewHolder oluşturulurken tıklama dinleyicisini ayarla.
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val category = getItem(position)
                    // Tıklanan kategorinin 'slug'ını callback ile dışarıya bildir.
                    onCategoryClick(category.slug)
                }
            }
        }

        fun bind(category: Category) {
            // Gelen 'Category' nesnesinin 'name' alanını TextView'e ata.
            binding.rowCategoryName.text = category.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = RecyclerRowCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // ListAdapter'ın verimli çalışması için DiffUtil.ItemCallback.
    private object CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.slug == newItem.slug
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem
        }
    }
}