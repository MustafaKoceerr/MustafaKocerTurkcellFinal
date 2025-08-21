package com.example.mustafakocer.presentation.feature_cart

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.mustafakocer.domain.model.CartItem

/**
 * A [ListAdapter] for displaying a list of [CartItem]s in a RecyclerView.
 * It uses a single `onEvent` lambda to communicate all user interactions back to the
 * calling component (typically a Fragment or ViewModel), adhering to MVI principles.
 *
 * @param onEvent A function to be invoked when a user interaction occurs.
 */
class CartListAdapter(
    private val onEvent: (CartEvent) -> Unit
) : ListAdapter<CartItem, CartViewHolder>(CartDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
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