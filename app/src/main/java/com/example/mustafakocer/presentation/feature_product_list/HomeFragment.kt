package com.example.mustafakocer.presentation.feature_product_list

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mustafakocer.databinding.FragmentHomeBinding
import com.example.mustafakocer.presentation.base.BaseFragment
import com.example.mustafakocer.presentation.common.ProductListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var productListAdapter: ProductListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeProductPagingFlow()
        observeLoadState()
    }

    private fun setupRecyclerView() {
        productListAdapter = ProductListAdapter(
            onProductClick = { product ->
                Toast.makeText(requireContext(), "${product.title} clicked", Toast.LENGTH_SHORT)
                    .show()
            },
            onAddToCartClick = { product ->
                Toast.makeText(
                    requireContext(),
                    "${product.title} added to cart",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onRemoveFromCartClick = { product ->
                Toast.makeText(
                    requireContext(),
                    "${product.title} removed from cart",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        binding.homeRecyclerView.apply {
            adapter = productListAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    private fun observeProductPagingFlow(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED)
            {  // ViewModel'den gelen PagingData akışını dinliyoruz.
                // collectLatest, yeni bir PagingData geldiğinde (örn: yeni arama)
                // eskisini iptal edip yenisini işlemeye başlar.
                viewModel.productsFlow.collectLatest { pagingData ->
                    // Gelen yeni PagingData'yı adaptöre gönderiyoruz.
                    productListAdapter.submitData(pagingData)
                }
            }
        }
    }

    private fun observeLoadState(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                productListAdapter.loadStateFlow.collectLatest { loadStates ->
                    // Sadece ilk yükleme (REFRESH) durumunu kontrol ediyoruz.
                    val refreshState = loadStates.refresh

                    // Yükleniyorsa progressBar'ı göster
                    binding.progressbar.isVisible = refreshState is LoadState.Loading

                    // Hata varsa, Toast ile göster.
                    if (refreshState is LoadState.Error) {
                        Toast.makeText(
                            requireContext(),
                            "Hata: ${refreshState.error.localizedMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }
}