// com/example/mustafakocer/presentation/feature_product_category/ProductsByCategoryFragment.kt (Refactor Edilmiş Hali)
package com.example.mustafakocer.presentation.feature_product_category

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mustafakocer.R
import com.example.mustafakocer.databinding.FragmentProductsByCategoryBinding
import com.example.mustafakocer.presentation.base.BaseFragment
import com.example.mustafakocer.presentation.common.PagingLoadStateAdapter
import com.example.mustafakocer.presentation.common.ProductListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductsByCategoryFragment : BaseFragment<FragmentProductsByCategoryBinding>(
    FragmentProductsByCategoryBinding::inflate
) {
    private val viewModel: CategoryViewModel by viewModels()
    private val args: ProductsByCategoryFragmentArgs by navArgs()
    private lateinit var productListAdapter: ProductListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeProductPagingFlow()
        observeLoadState()

        // Kategori seçildiğinde ViewModel'i bilgilendir.
        viewModel.onCategorySelected(args.categoryName)
    }

    private fun setupRecyclerView() {
        productListAdapter = ProductListAdapter { productId ->
            val action =
                ProductsByCategoryFragmentDirections.actionProductsByCategoryFragmentToProductDetailFragment(
                    productId
                )
            findNavController().navigate(action)
        }

        // Retry butonu artık StateLayout tarafından yönetiliyor.
        binding.stateLayout.onRetry = {
            productListAdapter.retry()
        }

        binding.stateLayout.findViewById<RecyclerView>(R.id.contentView).apply {
            adapter = productListAdapter.withLoadStateFooter(
                footer = PagingLoadStateAdapter { productListAdapter.retry() }
            )
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
                    // Ana yükleme durumunu (refresh) al.
                    when (val refreshState = loadStates.refresh) {
                        is LoadState.Loading -> {
                            if (productListAdapter.itemCount == 0) {
                                binding.stateLayout.showLoading()
                            }
                        }

                        is LoadState.NotLoading -> {
                            // --- DEĞİŞİKLİK BURADA ---
                            // Bir listenin gerçekten boş olduğunu anlamanın en güvenilir yolu:
                            // Yükleme bitmiş OLMALI ve sayfalama sonuna gelinmiş OLMALI.
                            val isListEmpty = loadStates.append.endOfPaginationReached && productListAdapter.itemCount < 1

                            if (isListEmpty) {
                                binding.stateLayout.showEmpty()
                            } else {
                                binding.stateLayout.showContent()
                            }
                        }

                        is LoadState.Error -> {
                            val errorMessage = (refreshState.error as? Exception)?.message
                            binding.stateLayout.showError(subtitle = errorMessage)
                        }
                    }
                }
            }
        }
    }
}