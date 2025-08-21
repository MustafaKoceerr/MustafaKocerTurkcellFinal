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

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::inflate) {

    private val viewModel: SearchViewModel by viewModels()
    private lateinit var productListAdapter: ProductListAdapter
    private lateinit var recyclerView: RecyclerView // RecyclerView referansı

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.stateLayout.showPrompt()

        setupRecyclerView()
        setupSearchView()
        observeViewModel()
        setupFab() // Scroll-to-top FAB
    }

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
        // … (EditText stil ayarların aynen kalabilir)
    }

    private fun observeViewModel() {
        // Retry butonuna basıldığında adaptörü tetikle.
        binding.stateLayout.onRetry = {
            productListAdapter.retry()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // 1) PagingData akışını topla
                launch {
                    viewModel.productsFlow.collectLatest { pagingData ->
                        productListAdapter.submitData(pagingData)
                    }
                }

                // 2) LoadState'e göre UI state yönetimi
                launch {
                    productListAdapter.loadStateFlow.collectLatest { loadStates ->
                        val query = binding.searchView.query.toString()

                        when (val refreshState = loadStates.refresh) {
                            is LoadState.NotLoading -> {
                                if (query.length < 3) {
                                    binding.stateLayout.showPrompt()
                                } else if (productListAdapter.itemCount < 1) {
                                    val subtitle = getString(R.string.search_empty_subtitle, query)
                                    binding.stateLayout.showEmpty(subtitle = subtitle)
                                } else {
                                    binding.stateLayout.showContent()
                                }
                            }
                            is LoadState.Loading -> {
                                if (query.length >= 3) {
                                    binding.stateLayout.showLoading()
                                } else {
                                    binding.stateLayout.showPrompt()
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
}
