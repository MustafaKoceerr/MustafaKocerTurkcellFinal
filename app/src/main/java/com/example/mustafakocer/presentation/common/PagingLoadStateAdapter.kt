package com.example.mustafakocer.presentation.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mustafakocer.databinding.ItemPagingFooterBinding

class PagingLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<PagingLoadStateAdapter.LoadStateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        // ViewBinding kullanımı burada tamamen aynı ve doğru.
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

    // ViewHolder sınıfı da ViewBinding kullanacak şekilde güncellendi.
    class LoadStateViewHolder(
        private val binding: ItemPagingFooterBinding,
        private val retry: () -> Unit // retry fonksiyonunu burada da alıyoruz.
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            // "Tekrar Dene" butonunun click listener'ını burada set ediyoruz.
            binding.btnRetry.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            // `binding.apply` bloğu yerine doğrudan `binding` üzerinden erişim sağlıyoruz.
            // Bu, ViewBinding için standart ve temiz bir kullanımdır.
            binding.progressBar.isVisible = loadState is LoadState.Loading
            binding.btnRetry.isVisible = loadState is LoadState.Error
            binding.txtError.isVisible = loadState is LoadState.Error
        }
    }
}