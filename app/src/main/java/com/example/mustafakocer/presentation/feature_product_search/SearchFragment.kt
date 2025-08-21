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
     * to update the UI accordingly.
     */
    private fun observeViewModel() {
        binding.stateLayout.onRetry = {
            productListAdapter.retry()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observe the PagingData from the ViewModel and submit it to the adapter.
                launch {
                    viewModel.productsFlow.collectLatest { pagingData ->
                        productListAdapter.submitData(pagingData)
                    }
                }
                // Observe the adapter's load state to show/hide loading, error, and empty states.
                launch {
                    productListAdapter.loadStateFlow.collectLatest { loadStates ->
                        val query = binding.searchView.query.toString()
                        when (val refreshState = loadStates.refresh) {
                            is LoadState.NotLoading -> {
                                // --- DEĞİŞİKLİK BAŞLANGICI ---
                                // Listenin gerçekten boş olduğundan emin olmak için,
                                // sadece itemCount'u değil, aynı zamanda sayfalama işleminin
                                // tamamen bittiğini de kontrol ediyoruz.
                                val isListEmpty = productListAdapter.itemCount < 1
                                if (query.length < 3) {
                                    binding.stateLayout.showPrompt()
                                } else if (isListEmpty) {
                                    // Eğer refresh işlemi NotLoading durumundaysa ve liste boşsa,
                                    // bu durum ya gerçekten sonuç olmadığını ya da henüz yüklemenin
                                    // başlamadığını gösterir. Yükleme durumu (Loading) kendi
                                    // bloğunda ele alındığı için, burası sadece "gerçekten boş"
                                    // durumunu yönetir ve race condition'ı engeller.
                                    val subtitle = getString(R.string.search_empty_subtitle, query)
                                    binding.stateLayout.showEmpty(subtitle = subtitle)
                                } else {
                                    binding.stateLayout.showContent()
                                }
                                // --- DEĞİŞİKLİK SONU ---
                            }
                            is LoadState.Loading -> {
                                // Yükleme durumu her zaman önceliklidir.
                                // Eğer yeni bir arama yapıldıysa ve adaptör temizlendiyse bile,
                                // bu blok çalışacağı için "Boş Ekran" gösterilmez.
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