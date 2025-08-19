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
import com.example.mustafakocer.R
import com.example.mustafakocer.databinding.FragmentProductsByCategoryBinding
import com.example.mustafakocer.databinding.LayoutStateEmptyBinding
import com.example.mustafakocer.domain.mapper.ErrorMapper
import com.example.mustafakocer.presentation.base.BaseFragment
import com.example.mustafakocer.presentation.common.PagingLoadStateAdapter
import com.example.mustafakocer.presentation.common.ProductListAdapter
import com.example.mustafakocer.presentation.common.UiErrorMapper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProductsByCategoryFragment : BaseFragment<FragmentProductsByCategoryBinding>(
    FragmentProductsByCategoryBinding::inflate
) {
    private val viewModel: CategoryViewModel by viewModels()
    private val args: ProductsByCategoryFragmentArgs by navArgs()
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
        observeProductPagingFlow()
        observeLoadState()

        viewModel.onCategorySelected(args.categoryName)
    }

    private fun setupRecyclerView() {
        productListAdapter = ProductListAdapter { productId ->
            val action = ProductsByCategoryFragmentDirections.actionProductsByCategoryFragmentToProductDetailFragment(productId)
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
                viewModel.productsByCategoryFlow.collectLatest { pagingData ->
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
                        emptyBinding?.txtEmptyTitle?.setText(R.string.pbc_empty_title)
                        emptyBinding?.txtEmptySubtitle?.setText(R.string.pbc_empty_subtitle)
                    } else {
                        emptyBinding?.root?.isVisible = false
                    }
                }
            }
        }
    }
}