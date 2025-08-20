package com.example.mustafakocer.presentation.feature_product_list

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mustafakocer.R
import com.example.mustafakocer.databinding.FragmentHomeBinding
import com.example.mustafakocer.presentation.base.BaseFragment
import com.example.mustafakocer.presentation.common.PagingLoadStateAdapter
import com.example.mustafakocer.presentation.common.ProductListAdapter
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Displays the main screen of the application, showing a paginated grid of products.
 * It also handles the "press back again to exit" functionality.
 */
@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var productListAdapter: ProductListAdapter

    private var lastBackPressedTime = 0L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeProductPagingFlow()
        observeLoadState()
        setupBackButtonHandler()
    }

    /**
     * Initializes the RecyclerView, its adapter, and the load state footer.
     */
    private fun setupRecyclerView() {
        productListAdapter = ProductListAdapter { productId ->
            val action = HomeFragmentDirections.actionHomeFragmentToProductDetailFragment(productId)
            findNavController().navigate(action)
        }

        binding.stateLayout.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.contentView)
            .apply {
                adapter = productListAdapter.withLoadStateFooter(
                    footer = PagingLoadStateAdapter { productListAdapter.retry() }
                )
                layoutManager = GridLayoutManager(requireContext(), 2)
            }
    }

    /**
     * Subscribes to the PagingData flow from the ViewModel and submits it to the adapter.
     */
    private fun observeProductPagingFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.productsFlow.collectLatest { pagingData ->
                    productListAdapter.submitData(pagingData)
                }
            }
        }
    }

    /**
     * Subscribes to the adapter's load state to manage the UI (loading, error, empty states).
     */
    private fun observeLoadState() {
        binding.stateLayout.onRetry = {
            productListAdapter.retry()
        }

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
                            if (productListAdapter.itemCount < 1) {
                                binding.stateLayout.showEmpty()
                            } else {
                                binding.stateLayout.showContent()
                            }
                        }
                        is LoadState.Error -> {
                            binding.stateLayout.showError()
                        }
                    }
                }
            }
        }
    }

    /**
     * Sets up a custom back press handler to implement the "press back again to exit" feature.
     */
    private fun setupBackButtonHandler() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (System.currentTimeMillis() - lastBackPressedTime > 2000) {
                    Snackbar.make(binding.root, R.string.press_back_again_to_exit, Snackbar.LENGTH_SHORT)
                        .show()
                    lastBackPressedTime = System.currentTimeMillis()
                } else {
                    requireActivity().finish()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
}