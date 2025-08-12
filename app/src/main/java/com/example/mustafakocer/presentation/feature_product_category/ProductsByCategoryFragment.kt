package com.example.mustafakocer.presentation.feature_product_category

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mustafakocer.databinding.FragmentProductsByCategoryBinding
import com.example.mustafakocer.presentation.base.BaseFragment
import com.example.mustafakocer.presentation.common.ProductListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductsByCategoryFragment : BaseFragment<FragmentProductsByCategoryBinding>(
    FragmentProductsByCategoryBinding::inflate
) {
    // Bu fragment, kendi ViewModel'ine sahip.
    private val viewModel: CategoryViewModel by viewModels()
    private val args: ProductsByCategoryFragmentArgs by navArgs()
    private lateinit var productListAdapter: ProductListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeProductPagingFlow()
        observeLoadState()

        // Navigasyon argümanından gelen kategori adını ViewModel'e bildirerek
        // doğru ürünlerin akışını tetikliyoruz.
        viewModel.onCategorySelected(args.categoryName)
    }

    private fun setupRecyclerView() {
        productListAdapter = ProductListAdapter { productId ->
            // ProductsByCategoryFragment'e özel action'ı kullanıyoruz.
            val action = ProductsByCategoryFragmentDirections.actionProductsByCategoryFragmentToProductDetailFragment(
                productId = productId
            )
            findNavController().navigate(action)
        }

        binding.productsByCategoryRecyclerView.apply {
            adapter = productListAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    private fun observeProductPagingFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.productsByCategoryFlow.collectLatest { pagingData ->
                    productListAdapter.submitData(pagingData)
                }
            }
        }
    }

    private fun observeLoadState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                productListAdapter.loadStateFlow.collectLatest { loadStates ->
                    val refreshState = loadStates.refresh
                    binding.progressbar.isVisible = refreshState is LoadState.Loading
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
