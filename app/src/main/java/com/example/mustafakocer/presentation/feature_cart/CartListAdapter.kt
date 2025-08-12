package com.example.mustafakocer.presentation.feature_cart

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.mustafakocer.domain.model.CartItem

class CartListAdapter(
    // DEĞİŞTİ: Artık 3 ayrı lambda yerine, tüm olayları taşıyan tek bir lambda alıyor.
    private val onEvent: (CartEvent) -> Unit
) : ListAdapter<CartItem, CartViewHolder>(CartDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        // ViewHolder'a tek bir onEvent lambdasını iletiyoruz.
        return CartViewHolder.create(parent, onEvent)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    private object CartDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem == newItem
        }
    }
}