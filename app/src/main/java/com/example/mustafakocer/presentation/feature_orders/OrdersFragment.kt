package com.example.mustafakocer.presentation.feature_orders

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mustafakocer.R
import com.example.mustafakocer.databinding.FragmentOrdersBinding
import com.example.mustafakocer.presentation.base.BaseFragment
import com.example.mustafakocer.presentation.common.PagingLoadStateAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrdersFragment : BaseFragment<FragmentOrdersBinding>(FragmentOrdersBinding::inflate) {

    private val viewModel: OrderViewModel by viewModels()
    private lateinit var orderListAdapter: OrderListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeState()
    }

    private fun setupRecyclerView() {
        orderListAdapter = OrderListAdapter { order ->
            val action = OrdersFragmentDirections.actionOrdersFragmentToOrderDetailsFragment(order)
            findNavController().navigate(action)
        }

        // Retry butonu artık StateLayout tarafından yönetiliyor.
        binding.stateLayout.onRetry = {
            orderListAdapter.retry()
        }

        binding.stateLayout.findViewById<RecyclerView>(R.id.contentView).apply {
            adapter = orderListAdapter.withLoadStateFooter(
                footer = PagingLoadStateAdapter { orderListAdapter.retry() }
            )
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // 1. ViewModel'den gelen PagingData'yı dinle ve adaptöre gönder.
                launch {
                    viewModel.ordersFlow.collectLatest { pagingData ->
                        orderListAdapter.submitData(pagingData)
                    }
                }
                // 2. PagingDataAdapter'ın durumunu dinleyerek UI'ı güncelle.
                launch {
                    orderListAdapter.loadStateFlow.collectLatest { loadStates ->
                        when (val refreshState = loadStates.refresh) {
                            is LoadState.Loading -> {
                                if (orderListAdapter.itemCount == 0) {
                                    binding.stateLayout.showLoading()
                                }
                            }

                            is LoadState.NotLoading -> {
                                if (orderListAdapter.itemCount < 1) {
                                    binding.stateLayout.showEmpty()
                                } else {
                                    binding.stateLayout.showContent()
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