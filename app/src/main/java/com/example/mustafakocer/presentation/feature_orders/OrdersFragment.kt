package com.example.mustafakocer.presentation.feature_orders

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
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
        observeOrderFlow()
        observeLoadState()

        // DEĞİŞTİ: Artık ViewModel'e userId göndermemize gerek yok.
        // ViewModel, oluşturulduğu anda doğru kullanıcı için veri akışını
        // kendi kendine başlatır. Fragment'ın bu detayı bilmesine gerek kalmadı.
        // viewModel.onUserIdSet("6") satırı ve tüm ilgili yorumlar kaldırıldı.
    }

    private fun setupRecyclerView() {
        orderListAdapter = OrderListAdapter { order ->
            val action = OrdersFragmentDirections.actionOrdersFragmentToOrderDetailsFragment(order)
            findNavController().navigate(action)
        }

        binding.orderRecyclerView.apply {
            // Paging 3'ün LoadState'lerini göstermek için bir footer adaptörü eklemek
            // kullanıcı deneyimini iyileştirir (örn: sayfa yükleniyor spinner'ı).
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
                    // Yüklenme durumunu sadece ilk yükleme (refresh) için yönetiyoruz.
                    binding.progressbar.isVisible = loadStates.refresh is LoadState.Loading

                    // Hata durumunu yönet
                    val errorState = loadStates.refresh as? LoadState.Error
                        ?: loadStates.append as? LoadState.Error
                        ?: loadStates.prepend as? LoadState.Error

                    errorState?.let {
                        Toast.makeText(
                            requireContext(),
                            "Hata: ${it.error.localizedMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    // Boş durumunu yönet (Listenin ilk yüklemesi bittiğinde ve liste boşsa)
                    val isListEmpty =
                        loadStates.refresh is LoadState.NotLoading && orderListAdapter.itemCount == 0
                    binding.txtEmptyOrders.isVisible = isListEmpty
                    if (isListEmpty) {
                        binding.txtEmptyOrders.text = "Henüz verilmiş bir siparişiniz bulunmuyor."
                    } else {
                        // Eğer liste doluysa, hata mesajı yerine boş metin göster.
                        binding.txtEmptyOrders.isVisible = false
                    }
                }
            }
        }
    }
}