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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.stateLayout.showPrompt()

        setupRecyclerView()
        setupSearchView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        productListAdapter = ProductListAdapter { productId ->
            val action =
                SearchFragmentDirections.actionSearchFragmentToProductDetailFragment(productId)
            findNavController().navigate(action)
        }
        binding.stateLayout.findViewById<RecyclerView>(R.id.contentView).apply {
            adapter = productListAdapter.withLoadStateFooter(
                footer = PagingLoadStateAdapter { productListAdapter.retry() }
            )
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
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
        // ... SearchView EditText stil kodları aynı kalıyor ...
    }

    private fun observeViewModel() {
        // Retry butonuna basıldığında adaptörü tetikle.
        binding.stateLayout.onRetry = {
            productListAdapter.retry()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // 1. ViewModel'den gelen PagingData'yı dinle ve adaptöre gönder.
                launch {
                    viewModel.productsFlow.collectLatest { pagingData ->
                        productListAdapter.submitData(pagingData)
                    }
                }

                // 2. PagingDataAdapter'ın durumunu dinleyerek UI'ı güncelle.
                launch {
                    productListAdapter.loadStateFlow.collectLatest { loadStates ->
                        // O anki arama sorgusunu al.
                        val query = binding.searchView.query.toString()

                        // Ana yükleme durumuna (refresh) odaklan.
                        when (val refreshState = loadStates.refresh) {
                            is LoadState.NotLoading -> {
                                // Yükleme bittiğinde:
                                if (query.length < 3) {
                                    // Sorgu yetersizse, PROMPT durumunu göster.
                                    binding.stateLayout.showPrompt()
                                } else if (productListAdapter.itemCount < 1) {
                                    // Sorgu yeterli ama sonuç yoksa, EMPTY durumunu göster.
                                    val subtitle = getString(R.string.search_empty_subtitle, query)
                                    binding.stateLayout.showEmpty(subtitle = subtitle)
                                } else {
                                    // Sonuç varsa, CONTENT'i göster.
                                    binding.stateLayout.showContent()
                                }
                            }

                            is LoadState.Loading -> {
                                // Yükleme başladığında:
                                if (query.length >= 3) {
                                    // Sadece sorgu yeterliyse LOADING göster.
                                    binding.stateLayout.showLoading()
                                } else {
                                    // Yetersiz sorgu için yükleme animasyonu gösterme, PROMPT'ta kal.
                                    binding.stateLayout.showPrompt()
                                }
                            }

                            is LoadState.Error -> {
                                // Hata oluştuğunda:
                                // Hata mesajını alıp ERROR durumunu göster.
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