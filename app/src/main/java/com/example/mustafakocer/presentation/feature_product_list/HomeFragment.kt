package com.example.mustafakocer.presentation.feature_product_list

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mustafakocer.R
import com.example.mustafakocer.databinding.FragmentHomeBinding
import com.example.mustafakocer.databinding.LayoutStateEmptyBinding
import com.example.mustafakocer.domain.mapper.ErrorMapper
import com.example.mustafakocer.presentation.base.BaseFragment
import com.example.mustafakocer.presentation.common.PagingLoadStateAdapter
import com.example.mustafakocer.presentation.common.ProductListAdapter
import com.example.mustafakocer.presentation.common.UiErrorMapper
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val viewModel: HomeViewModel by viewModels()
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
    private var lastBackPressedTime = 0L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeProductPagingFlow()
        observeLoadState()
        setupBackButtonHandler()
    }

    private fun setupRecyclerView() {
        productListAdapter = ProductListAdapter { productId ->
            val action = HomeFragmentDirections.actionHomeFragmentToProductDetailFragment(productId)
            findNavController().navigate(action)
        }

        binding.contentView.apply {
            adapter = productListAdapter.withLoadStateFooter(
                footer = PagingLoadStateAdapter { productListAdapter.retry() }
            )
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    private fun observeProductPagingFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.productsFlow.collectLatest { pagingData ->
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
                    val isListEmpty = productListAdapter.itemCount == 0

                    // Durumları belirle
                    val isLoading = refreshState is LoadState.Loading && isListEmpty
                    val isError = refreshState is LoadState.Error && isListEmpty
                    val isTrulyEmpty = refreshState is LoadState.NotLoading && isListEmpty

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

                    // Boş durumunu işle
                    if (isTrulyEmpty) {
                        if (emptyBinding == null) {
                            emptyBinding = LayoutStateEmptyBinding.bind(binding.viewEmptyStub.inflate())
                        }
                        emptyBinding?.root?.isVisible = true
                        emptyBinding?.txtEmptyTitle?.setText(R.string.empty_products_title)
                        emptyBinding?.txtEmptySubtitle?.setText(R.string.empty_products_subtitle)
                    } else {
                        emptyBinding?.root?.isVisible = false
                    }
                }
            }
        }
    }

    private fun setupBackButtonHandler() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (System.currentTimeMillis() - lastBackPressedTime > 2000) {
                    Snackbar.make(binding.root, "Çıkmak için tekrar basın", Snackbar.LENGTH_SHORT).show()
                    lastBackPressedTime = System.currentTimeMillis()
                } else {
                    requireActivity().finish()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
}