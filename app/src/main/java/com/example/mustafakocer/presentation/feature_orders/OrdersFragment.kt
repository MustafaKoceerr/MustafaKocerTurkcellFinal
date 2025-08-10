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

        // Oturum açmış kullanıcının ID'sini ViewModel'e bildirerek veri akışını tetikle.
        // Gerçek bir uygulamada bu ID, AuthRepository veya benzeri bir yerden alınır.
        // Şimdilik test için statik bir ID veya UserId objesini kullanabiliriz.
        // TODO: Gerçek bir user id ile değiştir, user id'yi ya datastore'dan al ya da farklı bir çözüm bul.
        viewModel.onUserIdSet("6")
    }

    private fun setupRecyclerView() {
        orderListAdapter = OrderListAdapter { order ->
            // Tıklanan 'order' nesnesini kullanarak Safe Args ile action oluştur.
            val action = OrdersFragmentDirections.actionOrdersFragmentToOrderDetailsFragment(order)
            // Navigasyonu tetikle.
            findNavController().navigate(action)
        }

        binding.orderRecyclerView.apply {
            adapter = orderListAdapter
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

                    // Yüklenme durumunu yönet
                    binding.progressbar.isVisible = refreshState is LoadState.Loading

                    // Hata durumunu yönet
                    if (refreshState is LoadState.Error) {
                        binding.txtEmptyOrders.isVisible = true
                        binding.txtEmptyOrders.text = "Siparişler yüklenirken bir hata oluştu."
                        Toast.makeText(
                            requireContext(),
                            "Hata: ${refreshState.error.localizedMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    // Boş durumunu yönet (Listenin ilk yüklemesi bittiğinde ve liste boşsa)
                    val isListEmpty =
                        refreshState is LoadState.NotLoading && orderListAdapter.itemCount == 0
                    binding.txtEmptyOrders.isVisible = isListEmpty
                    if (isListEmpty) {
                        binding.txtEmptyOrders.text = "Henüz verilmiş bir siparişiniz bulunmuyor."
                    }
                }
            }
        }
    }
}