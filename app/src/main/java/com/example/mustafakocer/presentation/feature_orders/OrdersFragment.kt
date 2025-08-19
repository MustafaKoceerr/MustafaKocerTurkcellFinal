package com.example.mustafakocer.presentation.feature_orders

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mustafakocer.R
import com.example.mustafakocer.databinding.FragmentOrdersBinding
import com.example.mustafakocer.databinding.LayoutStateEmptyBinding
import com.example.mustafakocer.domain.mapper.ErrorMapper
import com.example.mustafakocer.presentation.base.BaseFragment
import com.example.mustafakocer.presentation.common.PagingLoadStateAdapter
import com.example.mustafakocer.presentation.common.UiErrorMapper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class OrdersFragment : BaseFragment<FragmentOrdersBinding>(FragmentOrdersBinding::inflate) {

    private val viewModel: OrderViewModel by viewModels()
    private lateinit var orderListAdapter: OrderListAdapter

    @Inject
    lateinit var errorMapper: ErrorMapper

    @Inject
    lateinit var injectedUiErrorMapper: UiErrorMapper

    // --- BaseFragment Implementasyonu ---
    override val uiErrorMapper: UiErrorMapper by lazy { injectedUiErrorMapper }

    override fun onRetry() {
        orderListAdapter.retry()
    }
    // ------------------------------------

    // ViewStub'lar inflate edildikten sonra binding'lerini tutmak için.
    private var emptyBinding: LayoutStateEmptyBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeOrderFlow()
        observeLoadState()
    }

    private fun setupRecyclerView() {
        orderListAdapter = OrderListAdapter { order ->
            val action = OrdersFragmentDirections.actionOrdersFragmentToOrderDetailsFragment(order)
            findNavController().navigate(action)
        }

        binding.contentView.apply {
            adapter = orderListAdapter.withLoadStateFooter(
                footer = PagingLoadStateAdapter { orderListAdapter.retry() }
            )
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeOrderFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.ordersFlow.collectLatest { pagingData ->
                    orderListAdapter.submitData(pagingData)
                }
            }
        }
    }

    private fun observeLoadState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                orderListAdapter.loadStateFlow.collectLatest { loadStates ->
                    val refreshState = loadStates.refresh
                    val isListEmpty = orderListAdapter.itemCount == 0

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
                            emptyBinding =
                                LayoutStateEmptyBinding.bind(binding.viewEmptyStub.inflate())
                        }
                        emptyBinding?.root?.isVisible = true
                        emptyBinding?.txtEmptyTitle?.setText(R.string.empty_orders_title)
                        emptyBinding?.txtEmptySubtitle?.setText(R.string.empty_orders_subtitle)
                    } else {
                        emptyBinding?.root?.isVisible = false
                    }
                }
            }
        }
    }
}