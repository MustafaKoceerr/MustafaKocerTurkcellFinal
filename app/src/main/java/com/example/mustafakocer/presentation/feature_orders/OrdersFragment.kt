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
import com.example.mustafakocer.presentation.common.defaultSlideOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Displays a paginated list of the user's past orders.
 * This fragment observes a PagingData flow from the [OrderViewModel] and also
 * listens to the adapter's load states to manage the UI (loading, error, empty states).
 */
@AndroidEntryPoint
class OrdersFragment : BaseFragment<FragmentOrdersBinding>(FragmentOrdersBinding::inflate) {

    private val viewModel: OrderViewModel by viewModels()
    private lateinit var orderListAdapter: OrderListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeState()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Transition varsa temizle (action animleri çalışsın)
        enterTransition = null
        exitTransition = null
        reenterTransition = null
        returnTransition = null
    }

    /**
     * Initializes the RecyclerView, its adapter, and the load state footer.
     * Also handles item click events for navigation.
     */
    private fun setupRecyclerView() {
        orderListAdapter = OrderListAdapter { order ->
            val action = OrdersFragmentDirections
                .actionOrdersFragmentToOrderDetailsFragment(order)
            findNavController().navigate(action, defaultSlideOptions())
        }

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

    /**
     * Subscribes to the PagingData flow and the adapter's LoadState flow
     * to update the UI accordingly.
     */
    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observe the PagingData from the ViewModel and submit it to the adapter.
                launch {
                    viewModel.ordersFlow.collectLatest { pagingData ->
                        orderListAdapter.submitData(pagingData)
                    }
                }
                // Observe the adapter's load state to show/hide loading, error, and empty states.
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