package com.example.mustafakocer.presentation.feature_product_search

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mustafakocer.R
import com.example.mustafakocer.databinding.FragmentSearchBinding
import com.example.mustafakocer.databinding.LayoutStateEmptyBinding
import com.example.mustafakocer.databinding.LayoutStateIdleSearchBinding
import com.example.mustafakocer.presentation.base.BaseFragment
import com.example.mustafakocer.presentation.common.PagingLoadStateAdapter
import com.example.mustafakocer.presentation.common.ProductListAdapter
import com.example.mustafakocer.presentation.common.UiErrorMapper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::inflate) {

    private val viewModel: SearchViewModel by viewModels()
    private lateinit var productListAdapter: ProductListAdapter

    @Inject
    lateinit var injectedUiErrorMapper: UiErrorMapper
    override val uiErrorMapper: UiErrorMapper by lazy { injectedUiErrorMapper }
    override fun onRetry() { productListAdapter.retry() }

    // ViewStub'lar inflate edildikten sonra binding'lerini tutmak için.
    private var emptyBinding: LayoutStateEmptyBinding? = null
    private var idleBinding: LayoutStateIdleSearchBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearchView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        productListAdapter = ProductListAdapter { productId ->
            val action = SearchFragmentDirections.actionSearchFragmentToProductDetailFragment(productId)
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
        val searchEditText = binding.searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText.background = null
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // 1. Paging verisini her zaman adaptöre gönder.
                // distinctUntilChanged, aynı PagingData'nın tekrar tekrar gönderilmesini engeller.
                launch {
                    viewModel.uiState
                        .map { it.screenState }
                        .distinctUntilChanged()
                        .collectLatest { screenState ->
                            if (screenState is ScreenState.Content) {
                                productListAdapter.submitData(screenState.products)
                            }
                        }
                }

                // 2. Paging'in yükleme durumlarını ViewModel'e bildir.
                launch {
                    productListAdapter.loadStateFlow.collect { loadStates ->
                        viewModel.onPagingLoadStateChanged(loadStates, productListAdapter.itemCount)
                    }
                }

                // 3. ViewModel'den gelen TEK UiState'i dinle ve EKRANI ÇİZ.
                launch {
                    viewModel.uiState.collect { state ->
                        render(state)
                    }
                }
            }
        }
    }

    private fun render(state: SearchUiState) {
        // Tüm view'ları başlangıçta gizle
        binding.contentView.isVisible = false
        binding.viewLoadingStub.isVisible = false
        hideErrorState()
        idleBinding?.root?.isVisible = false
        emptyBinding?.root?.isVisible = false

        // Doğru durumu göster
        when (val screenState = state.screenState) {
            is ScreenState.Idle -> {
                if (idleBinding == null) {
                    idleBinding = LayoutStateIdleSearchBinding.bind(binding.viewIdleStub.inflate())
                }
                idleBinding?.root?.isVisible = true
            }
            is ScreenState.Loading -> {
                binding.viewLoadingStub.isVisible = true
            }
            is ScreenState.Error -> {
                handleErrorState(binding.viewErrorStub, screenState.exception)
            }
            is ScreenState.Empty -> {
                if (emptyBinding == null) {
                    emptyBinding = LayoutStateEmptyBinding.bind(binding.viewEmptyStub.inflate())
                }
                emptyBinding?.root?.isVisible = true
                emptyBinding?.txtEmptyTitle?.setText(R.string.search_empty_title)
                emptyBinding?.txtEmptySubtitle?.text = getString(R.string.search_empty_subtitle, state.searchQuery)
            }
            is ScreenState.Content -> {
                binding.contentView.isVisible = true
            }
        }
    }
}