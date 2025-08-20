// com/example/mustafakocer/presentation/feature_product_list/HomeFragment.kt (Refactor Edilmiş Hali)
package com.example.mustafakocer.presentation.feature_product_list

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mustafakocer.R
import com.example.mustafakocer.databinding.FragmentHomeBinding
import com.example.mustafakocer.presentation.base.BaseFragment
import com.example.mustafakocer.presentation.common.PagingLoadStateAdapter
import com.example.mustafakocer.presentation.common.ProductListAdapter
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var productListAdapter: ProductListAdapter

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

        // ÖNEMLİ: RecyclerView'a artık binding.stateLayout.contentView üzerinden erişiyoruz.
        binding.stateLayout.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.contentView)
            .apply {
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
        // Retry butonuna basıldığında adaptörü tetikle.
        binding.stateLayout.onRetry = {
            productListAdapter.retry()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                productListAdapter.loadStateFlow.collectLatest { loadStates ->
                    // Ana yükleme durumunu (refresh) al.
                    when (val refreshState = loadStates.refresh) {
                        is LoadState.Loading -> {
                            // Sadece liste boşken tam ekran loading göster.
                            if (productListAdapter.itemCount == 0) {
                                binding.stateLayout.showLoading()
                            }
                        }

                        is LoadState.NotLoading -> {
                            // Yükleme bittiğinde, liste boş mu diye kontrol et.
                            if (productListAdapter.itemCount < 1) {
                                // XML'de tanımladığımız varsayılan boş ekranı göster.
                                binding.stateLayout.showEmpty()
                            } else {
                                // Liste doluysa içeriği göster.
                                binding.stateLayout.showContent()
                            }
                        }

                        is LoadState.Error -> {
                            // Hata durumunda, XML'de tanımlı hata ekranını göster.
                            // İstersen hatayı parse edip özel bir mesaj da gönderebilirsin.
                            // val errorMessage = (refreshState.error as? Exception)?.message
                            binding.stateLayout.showError()
                        }
                    }
                }
            }
        }
    }

    private fun setupBackButtonHandler() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (System.currentTimeMillis() - lastBackPressedTime > 2000) {
                    Snackbar.make(binding.root, "Çıkmak için tekrar basın", Snackbar.LENGTH_SHORT)
                        .show()
                    lastBackPressedTime = System.currentTimeMillis()
                } else {
                    requireActivity().finish()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
}