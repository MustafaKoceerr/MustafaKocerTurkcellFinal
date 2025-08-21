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
    private lateinit var recyclerView: RecyclerView // RecyclerView referansı için

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeState()
        setupFab() // Yeni eklenen fonksiyon çağrısı
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

        recyclerView = binding.stateLayout.findViewById<RecyclerView>(R.id.contentView).apply {
            adapter = orderListAdapter.withLoadStateFooter(
                footer = PagingLoadStateAdapter { orderListAdapter.retry() }
            )
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    /**
     * Sets up the ExtendedFloatingActionButton's visibility and click listener.
     */
    private fun setupFab() {
        binding.fabScrollTop.setOnClickListener {
            recyclerView.smoothScrollToPosition(0)
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // Kullanıcı aşağı kaydırıyorsa ve buton görünmüyorsa
                if (dy > 0 && !binding.fabScrollTop.isShown) {
                    binding.fabScrollTop.show()
                }
                // Kullanıcı yukarı kaydırıyorsa ve buton görünüyorsa
                else if (dy < 0 && binding.fabScrollTop.isShown) {
                    binding.fabScrollTop.hide()
                }
            }
        })
    }


    /**
     * Subscribes to the PagingData flow and the adapter's LoadState flow
     * to update the UI accordingly. This is the definitive, race-condition-free implementation.
     */
    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // PagingData akışını dinle
                launch {
                    viewModel.ordersFlow.collectLatest { pagingData ->
                        orderListAdapter.submitData(pagingData)
                    }
                }
                // LoadState akışını dinle
                launch {
                    orderListAdapter.loadStateFlow.collectLatest { loadStates ->
                        // --- NİHAİ ÇÖZÜM ---
                        val refresh = loadStates.refresh

                        // Kural 1: İçerik her zaman önceliklidir. Listede veri varsa, göster.
                        val hasContent = orderListAdapter.itemCount > 0
                        if (hasContent) {
                            binding.stateLayout.showContent()
                            return@collectLatest // Başka bir şey yapmaya gerek yok.
                        }

                        // Kural 2: İçerik yoksa, `refresh` durumuna göre karar ver.
                        when (refresh) {
                            is LoadState.Loading -> {
                                binding.stateLayout.showLoading()
                            }
                            is LoadState.Error -> {
                                val errorMessage = (refresh.error as? Exception)?.message
                                binding.stateLayout.showError(subtitle = errorMessage)
                            }
                            is LoadState.NotLoading -> {
                                // İçerik yok ve yükleme bitti.
                                // Paging kütüphanesi "daha fazla sayfa kalmadı" diyorsa,
                                // o zaman liste GERÇEKTEN boştur.
                                val endOfPagination = loadStates.append.endOfPaginationReached
                                if (endOfPagination) {
                                    binding.stateLayout.showEmpty()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}