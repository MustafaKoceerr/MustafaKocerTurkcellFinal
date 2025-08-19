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
import com.example.mustafakocer.R
import com.example.mustafakocer.databinding.FragmentSearchBinding
import com.example.mustafakocer.databinding.LayoutStateEmptyBinding
import com.example.mustafakocer.domain.mapper.ErrorMapper
import com.example.mustafakocer.domain.usecase.SearchProductsUseCase
import com.example.mustafakocer.presentation.base.BaseFragment
import com.example.mustafakocer.presentation.common.PagingLoadStateAdapter
import com.example.mustafakocer.presentation.common.ProductListAdapter
import com.example.mustafakocer.presentation.common.UiErrorMapper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::inflate) {

    private val viewModel: SearchViewModel by viewModels()
    private lateinit var productListAdapter: ProductListAdapter

    @Inject
    lateinit var errorMapper: ErrorMapper

    @Inject
    lateinit var injectedUiErrorMapper: UiErrorMapper

    // --- BaseFragment Implementasyonu ---
    override val uiErrorMapper: UiErrorMapper by lazy { injectedUiErrorMapper }

    override fun onRetry() {
        productListAdapter.retry()
    }
    // ------------------------------------

    // ViewStub'lar inflate edildikten sonra binding'lerini tutmak için.
    private var emptyBinding: LayoutStateEmptyBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchView()
        observeProductPagingFlow()
        observeLoadState()
    }

    private fun setupRecyclerView() {
        productListAdapter = ProductListAdapter { productId ->
            val action =
                SearchFragmentDirections.actionSearchFragmentToProductDetailFragment(productId)
            findNavController().navigate(action)
        }
        binding.contentView.apply {
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
    }

    private fun observeProductPagingFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.products.collectLatest { pagingData ->
                    productListAdapter.submitData(pagingData)
                }
            }
        }
    }

    private fun observeLoadState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                productListAdapter.loadStateFlow.collectLatest { loadStates ->
                    val refreshState = loadStates.refresh
                    val query = viewModel.searchQuery.value
                    val isListEmpty = productListAdapter.itemCount == 0

                    // Durumları belirle
                    val isIdle = query.length < SearchProductsUseCase.MIN_QUERY_LENGTH
                    val isLoading = refreshState is LoadState.Loading && isListEmpty
                    val isError = refreshState is LoadState.Error && isListEmpty
                    val isTrulyEmpty =
                        refreshState is LoadState.NotLoading && isListEmpty && !isIdle

                    // Görünürlükleri yönet
                    binding.viewLoadingStub.isVisible = isLoading
                    binding.contentView.isVisible = !isLoading && !isError

                    // Hata durumunu işle
                    if (isError) {
                        val appException = errorMapper.map((refreshState as LoadState.Error).error)
                        handleErrorState(binding.viewErrorStub, appException)
                    } else {
                        hideErrorState()
                    }

                    // Boş veya Boşta durumunu işle
                    val showEmptyOrIdle = isIdle || isTrulyEmpty
                    if (showEmptyOrIdle) {
                        if (emptyBinding == null) {
                            emptyBinding =
                                LayoutStateEmptyBinding.bind(binding.viewEmptyStub.inflate())
                        }
                        emptyBinding?.root?.isVisible = true
                        if (isIdle) {
                            emptyBinding?.txtEmptyTitle?.setText(R.string.search_idle_title)
                            emptyBinding?.txtEmptySubtitle?.setText(R.string.search_idle_subtitle)
                        } else { // isTrulyEmpty
                            emptyBinding?.txtEmptyTitle?.setText(R.string.search_empty_title)
                            emptyBinding?.txtEmptySubtitle?.text =
                                getString(R.string.search_empty_subtitle, query)
                        }
                    } else {
                        emptyBinding?.root?.isVisible = false
                    }
                }
            }
        }
    }
}