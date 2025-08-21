package com.example.mustafakocer.presentation.feature_product_search

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mustafakocer.R
import com.example.mustafakocer.databinding.FragmentSearchBinding
import com.example.mustafakocer.presentation.base.BaseFragment
import com.example.mustafakocer.presentation.common.PagingLoadStateAdapter
import com.example.mustafakocer.presentation.common.ProductListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Displays a search interface and a paginated grid of product results.
 * It observes PagingData and LoadState from the [SearchViewModel] and its adapter
 * to manage the complex UI states of the search screen.
 */
@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::inflate) {

    private val viewModel: SearchViewModel by viewModels()
    private lateinit var productListAdapter: ProductListAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.stateLayout.showPrompt()
        setupRecyclerView()
        setupSearchView()
        observeViewModel()
        setupFab()
    }

    /**
     * Initializes the RecyclerView, its adapter, and the scroll-to-top FAB.
     */
    private fun setupRecyclerView() {
        productListAdapter = ProductListAdapter { productId ->
            val action =
                SearchFragmentDirections.actionSearchFragmentToProductDetailFragment(productId)
            findNavController().navigate(action)
        }
        recyclerView = binding.stateLayout.findViewById<RecyclerView>(R.id.contentView).apply {
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
        binding.fabScrollTop.setOnClickListener {
            recyclerView.smoothScrollToPosition(0)
        }
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(rv, dx, dy)
                if (dy > 0 && !binding.fabScrollTop.isShown) {
                    binding.fabScrollTop.show()
                } else if (dy < 0 && binding.fabScrollTop.isShown) {
                    binding.fabScrollTop.hide()
                }
            }
        })
    }

    /**
     * Sets up the SearchView to listen for text changes and notify the ViewModel.
     */
    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchView.clearFocus()
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.onSearchQueryChanged(newText.orEmpty())
                return true
            }
        })
    }

    /**
     * Subscribes to the PagingData flow and the adapter's LoadState flow
     * to update the UI accordingly. This is the definitive, race-condition-free implementation.
     */
    private fun observeViewModel() {
        binding.stateLayout.onRetry = {
            productListAdapter.retry()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // PagingData akışını dinle
                launch {
                    viewModel.productsFlow.collectLatest { pagingData ->
                        productListAdapter.submitData(pagingData)
                    }
                }
                // LoadState akışını dinle
                launch {
                    productListAdapter.loadStateFlow.collectLatest { loadStates ->
                        // --- NİHAİ ÇÖZÜM ---
                        val query = binding.searchView.query.toString()
                        val refresh = loadStates.refresh

                        // Kural 0: Arama sorgusu yeterince uzun değilse, her zaman yönlendirme göster.
                        // Bu, diğer tüm durumları ezer.
                        if (query.length < 3) {
                            binding.stateLayout.showPrompt()
                            return@collectLatest
                        }

                        // Kural 1: İçerik her zaman önceliklidir. Listede veri varsa, göster.
                        val hasContent = productListAdapter.itemCount > 0
                        if (hasContent) {
                            binding.stateLayout.showContent()
                            return@collectLatest
                        }

                        // Kural 2: İçerik yoksa, `refresh` durumuna göre karar ver.
                        when (refresh) {
                            is LoadState.Loading -> {
                                binding.stateLayout.showLoading()
                            }
                            is LoadState.Error -> {
                                val errorMessage = (refresh.error as? Exception)?.message
                                binding.stateLayout.showError(subtitle = errorMessage)
                            }
                            is LoadState.NotLoading -> {
                                // İçerik yok ve yükleme bitti.
                                // Paging kütüphanesi "daha fazla sayfa kalmadı" diyorsa,
                                // o zaman liste GERÇEKTEN boştur.
                                val endOfPagination = loadStates.append.endOfPaginationReached
                                if (endOfPagination) {
                                    val subtitle = getString(R.string.search_empty_subtitle, query)
                                    binding.stateLayout.showEmpty(subtitle = subtitle)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}