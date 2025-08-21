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

/**
 * Displays a paginated grid of products for a specific category.
 * It reuses the [CategoryViewModel] to get a reactive flow of products based on the
 * category name passed through navigation arguments.
 */
@AndroidEntryPoint
class ProductsByCategoryFragment : BaseFragment<FragmentProductsByCategoryBinding>(
    FragmentProductsByCategoryBinding::inflate
) {
    private val viewModel: CategoryViewModel by viewModels()
    private val args: ProductsByCategoryFragmentArgs by navArgs()
    private lateinit var productListAdapter: ProductListAdapter
    private lateinit var recyclerView: RecyclerView // RecyclerView referansı için

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeProductPagingFlow()
        observeLoadState()
        setupFab() // Yeni eklenen fonksiyon çağrısı

        // Inform the ViewModel about the selected category.
        viewModel.onCategorySelected(args.categoryName)
    }

    /**
     * Initializes the RecyclerView, its adapter, and the load state footer.
     */
    private fun setupRecyclerView() {
        productListAdapter = ProductListAdapter { productId ->
            val action =
                ProductsByCategoryFragmentDirections.actionProductsByCategoryFragmentToProductDetailFragment(
                    productId
                )
            findNavController().navigate(action)
        }

        binding.stateLayout.onRetry = {
            productListAdapter.retry()
        }

        recyclerView = binding.stateLayout.findViewById<RecyclerView>(R.id.contentView).apply {
            adapter = productListAdapter.withLoadStateFooter(
                footer = PagingLoadStateAdapter { productListAdapter.retry() }
            )
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    /**
     * Sets up the ExtendedFloatingActionButton's visibility and click listener.
     */
    private fun setupFab() {
        binding.fabScrollTop.setOnClickListener {
            recyclerView.smoothScrollToPosition(0)
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // Kullanıcı aşağı kaydırıyorsa ve buton görünmüyorsa
                if (dy > 0 && !binding.fabScrollTop.isShown) {
                    binding.fabScrollTop.show()
                }
                // Kullanıcı yukarı kaydırıyorsa ve buton görünüyorsa
                else if (dy < 0 && binding.fabScrollTop.isShown) {
                    binding.fabScrollTop.hide()
                }
            }
        })
    }


    /**
     * Subscribes to the paginated product flow from the ViewModel.
     */
    private fun observeProductPagingFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.productsByCategoryFlow.collectLatest { pagingData ->
                    productListAdapter.submitData(pagingData)
                }
            }
        }
    }

    /**
     * Subscribes to the adapter's load state to manage the UI (loading, error, empty states).
     */
    private fun observeLoadState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                productListAdapter.loadStateFlow.collectLatest { loadStates ->
                    when (val refreshState = loadStates.refresh) {
                        is LoadState.Loading -> {
                            if (productListAdapter.itemCount == 0) {
                                binding.stateLayout.showLoading()
                            }
                        }
                        is LoadState.NotLoading -> {
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