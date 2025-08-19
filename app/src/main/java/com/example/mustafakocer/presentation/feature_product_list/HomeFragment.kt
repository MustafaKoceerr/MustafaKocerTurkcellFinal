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
                    // Sadece refresh (ilk yükleme veya pull-to-refresh) durumuna odaklan.
                    val refreshState = loadStates.refresh

                    // İçeriği her zaman görünür olarak başlat.
                    binding.contentView.isVisible = true

                    // Tam ekran durumlarını (loading, error, empty) sadece liste boşsa yönet.
                    if (productListAdapter.itemCount == 0) {
                        binding.viewLoadingStub.isVisible = refreshState is LoadState.Loading

                        val isError = refreshState is LoadState.Error
                        if (isError) {
                            // Hata varsa, içeriği gizle ve hata ekranını göster.
                            binding.contentView.isVisible = false
                            val appException = errorMapper.map((refreshState as LoadState.Error).error)
                            handleErrorState(binding.viewErrorStub, appException)
                        } else {
                            hideErrorState()
                        }

                        val isEmpty = refreshState is LoadState.NotLoading && loadStates.append.endOfPaginationReached
                        if (isEmpty) {
                            binding.contentView.isVisible = false
                            if (emptyBinding == null) {
                                emptyBinding = LayoutStateEmptyBinding.bind(binding.viewEmptyStub.inflate())
                            }
                            emptyBinding?.root?.isVisible = true
                            // ... (emptyBinding'i doldur)
                        } else {
                            emptyBinding?.root?.isVisible = false
                        }
                    } else {
                        // Liste doluysa, tam ekran durumlarını her zaman gizle.
                        binding.viewLoadingStub.isVisible = false
                        hideErrorState()
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