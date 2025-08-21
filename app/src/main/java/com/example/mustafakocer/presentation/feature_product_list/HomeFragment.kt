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
import androidx.recyclerview.widget.RecyclerView
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
    private lateinit var recyclerView: RecyclerView

    private var lastBackPressedTime = 0L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeProductPagingFlow()
        observeLoadState()
        setupBackButtonHandler()
        setupFab()
    }

    /**
     * Initializes the RecyclerView, its adapter, and the load state footer.
     */
    private fun setupRecyclerView() {
        productListAdapter = ProductListAdapter { productId ->
            val action = HomeFragmentDirections.actionHomeFragmentToProductDetailFragment(productId)
            findNavController().navigate(action)
        }
        recyclerView = binding.stateLayout.findViewById<RecyclerView>(R.id.contentView)
            .apply {
                adapter = productListAdapter.withLoadStateFooter(
                    footer = PagingLoadStateAdapter { productListAdapter.retry() }
                )
                layoutManager = GridLayoutManager(requireContext(), 2)
            }
    }

    /**
     * Configures the FloatingActionButton to scroll the list to the top and to
     * show/hide based on scroll direction.
     */
    private fun setupFab() {
        binding.fabScrollToTop.setOnClickListener {
            recyclerView.smoothScrollToPosition(0)
        }
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && !binding.fabScrollToTop.isShown) {
                    binding.fabScrollToTop.show()
                } else if (dy < 0 && binding.fabScrollToTop.isShown) {
                    binding.fabScrollToTop.hide()
                }
            }
        })
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
     * This is the definitive, race-condition-free implementation.
     */
    private fun observeLoadState() {
        binding.stateLayout.onRetry = {
            productListAdapter.retry()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                productListAdapter.loadStateFlow.collectLatest { loadStates ->
                    // --- NİHAİ ÇÖZÜM ---
                    val refresh = loadStates.refresh

                    // Kural 1: İçerik her zaman önceliklidir. Listede veri varsa, göster.
                    // Bu, swipe-to-refresh sırasında içeriğin kaybolmasını engeller.
                    val hasContent = productListAdapter.itemCount > 0
                    if (hasContent) {
                        binding.stateLayout.showContent()
                        return@collectLatest // Başka bir şey yapmaya gerek yok.
                    }

                    // Kural 2: İçerik yoksa, `refresh` durumuna göre karar ver.
                    when (refresh) {
                        is LoadState.Loading -> {
                            // İçerik yok ve yükleniyor -> Tam ekran yükleme göster.
                            binding.stateLayout.showLoading()
                        }
                        is LoadState.Error -> {
                            // İçerik yok ve hata var -> Tam ekran hata göster.
                            val errorMessage = (refresh.error as? Exception)?.message
                            binding.stateLayout.showError(subtitle = errorMessage)
                        }
                        is LoadState.NotLoading -> {
                            // İçerik yok ve yükleme bitti.
                            // Paging kütüphanesi "daha fazla sayfa kalmadı" diyorsa,
                            // o zaman liste GERÇEKTEN boştur.
                            val endOfPagination = loadStates.append.endOfPaginationReached
                            if (endOfPagination) {
                                binding.stateLayout.showEmpty()
                            }
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