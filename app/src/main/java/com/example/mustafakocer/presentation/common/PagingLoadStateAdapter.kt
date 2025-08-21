package com.example.mustafakocer.presentation.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mustafakocer.databinding.ItemPagingFooterBinding

/**
 * A [LoadStateAdapter] for displaying a progress bar while data is loading,
 * and an error message with a retry button when loading fails.
 * This is typically used as a footer for a [PagingDataAdapter].
 *
 * @param retry A lambda function to be invoked when the retry button is clicked.
 */
class PagingLoadStateAdapter(
    private val retry: () -> Unit,
) : LoadStateAdapter<PagingLoadStateAdapter.LoadStateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val binding = ItemPagingFooterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LoadStateViewHolder(binding, retry)
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    /**
     * The [RecyclerView.ViewHolder] for the load state item.
     * It manages the visibility of the progress bar, error text, and retry button.
     */
    class LoadStateViewHolder(
        private val binding: ItemPagingFooterBinding,
        private val retry: () -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.btnRetry.setOnClickListener { retry.invoke() }
        }

        /**
         * Binds the current [LoadState] to the views.
         */
        fun bind(loadState: LoadState) {
            binding.apply {
                progressBar.isVisible = loadState is LoadState.Loading
                btnRetry.isVisible = loadState is LoadState.Error
                txtError.isVisible = loadState is LoadState.Error
            }
        }
    }
}