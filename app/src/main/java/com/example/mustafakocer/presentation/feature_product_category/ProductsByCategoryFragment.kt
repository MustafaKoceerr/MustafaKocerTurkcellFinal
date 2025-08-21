package com.example.mustafakocer.presentation.feature_product_category

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
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
    private lateinit var recyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeProductPagingFlow()
        observeLoadState()
        setupFab()

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

    private fun setupFab() {
        binding.fabScrollTop.setOnClickListener {
            recyclerView.smoothScrollToPosition(0)
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && !binding.fabScrollTop.isShown) {
                    binding.fabScrollTop.show()
                } else if (dy < 0 && binding.fabScrollTop.isShown) {
                    binding.fabScrollTop.hide()
                }
            }
        })
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

    /**
     * Subscribes to the adapter's load state to manage the UI (loading, error, empty states).
     * This is the definitive, race-condition-free implementation.
     */
    private fun observeLoadState() {
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
}