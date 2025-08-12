package com.example.mustafakocer.presentation.feature_product_search

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mustafakocer.databinding.FragmentSearchBinding
import com.example.mustafakocer.domain.usecase.SearchProductsUseCase
import com.example.mustafakocer.presentation.base.BaseFragment
import com.example.mustafakocer.presentation.common.ProductListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::inflate) {
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var productListAdapter: ProductListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchView()
        observeSearchResults()
        observeLoadState()
        observeSearchQuery() // İşte burada 'searchQuery' kullanılıyor.
    }

    private fun setupRecyclerView() {
        productListAdapter = ProductListAdapter { productId ->
            // SearchFragment'e özel action'ı kullanıyoruz.
            val action = SearchFragmentDirections.actionSearchFragmentToProductDetailFragment(
                productId = productId
            )
            findNavController().navigate(action)
        }

        binding.searchRecyclerView.apply {
            adapter = productListAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    private fun setupSearchView() {

        binding.searchView.setOnClickListener {
            binding.searchView.isIconified = false
        }
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // UI'dan gelen olayı ViewModel'e iletiyoruz.
                viewModel.onSearchQueryChanged(newText.orEmpty())
                return true
            }
        })
    }

    private fun observeSearchResults() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // ViewModel'den gelen PagingData<Product> akışını dinliyoruz.
                viewModel.products.collectLatest { pagingData ->
                    productListAdapter.submitData(pagingData)
                }
            }
        }
    }

    private fun observeSearchQuery() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // ViewModel'deki searchQuery'yi dinleyerek UI durumunu yönetiyoruz.
                viewModel.searchQuery.collectLatest { query ->
                    val isQueryTooShort = query.length < SearchProductsUseCase.MIN_QUERY_LENGTH
                    // Arama metni çok kısaysa, "Aramaya başla" ekranını göster.
                    binding.stateIdleGroup.isVisible = isQueryTooShort
                    // Arama metni yeterince uzunsa, sonuç listesini göster.
                    binding.searchRecyclerView.isVisible = !isQueryTooShort

                    if (isQueryTooShort) {
                        binding.stateEmptyGroup.isVisible = false
                    }
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

                    // Diğer UI durumlarını (boş, hata) yönetme mantığı buraya eklenebilir.
                }
            }
        }
    }
}