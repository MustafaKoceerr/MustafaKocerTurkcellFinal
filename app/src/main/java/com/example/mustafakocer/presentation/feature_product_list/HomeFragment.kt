package com.example.mustafakocer.presentation.feature_product_list

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mustafakocer.databinding.FragmentHomeBinding
import com.example.mustafakocer.presentation.base.BaseFragment
import com.example.mustafakocer.presentation.common.ProductListAdapter
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var productListAdapter: ProductListAdapter

    // YENİ: Geri tuşuna basılma zamanını takip etmek için değişken.
    private var lastBackPressedTime = 0L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeProductPagingFlow()
        observeLoadState()
        setupBackButtonHandler() // YENİ: Geri tuşu dinleyicisini kur.
    }

    private fun setupRecyclerView() {
        productListAdapter = ProductListAdapter { productId ->
            // HomeFragment'e özel action'ı kullanıyoruz.
            val action = HomeFragmentDirections.actionHomeFragmentToProductDetailFragment(
                productId = productId
            )
            findNavController().navigate(action)
        }

        binding.homeRecyclerView.apply {
            adapter = productListAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    private fun observeProductPagingFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // ViewModel'den gelen PagingData<Product> akışını dinle
                viewModel.productsFlow.collectLatest { pagingData ->
                    // ve doğrudan adaptöre gönder. Fragment'ın başka bir şey yapmasına gerek yok.
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

                    // Yükleniyorsa progressBar'ı göster.
                    binding.progressbar.isVisible = refreshState is LoadState.Loading

                    // Hata varsa, Toast ile göster.
                    if (refreshState is LoadState.Error) {
                        Toast.makeText(
                            requireContext(),
                            "Hata: ${refreshState.error.localizedMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    // YENİ FONKSİYON: Geri tuşu davranışını yönetir.
    private fun setupBackButtonHandler() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Son basıştan bu yana 2 saniyeden fazla geçtiyse
                if (System.currentTimeMillis() - lastBackPressedTime > 2000) {
                    // DEĞİŞTİ: Toast yerine Snackbar gösteriyoruz.
                    // binding.root, Snackbar'ın hangi layout içinde gösterileceğini belirtir.
                    Snackbar.make(binding.root, "Çıkmak için tekrar basın", Snackbar.LENGTH_SHORT)
                        .show()
                    lastBackPressedTime = System.currentTimeMillis()
                } else {
                    requireActivity().finish()
                }
            }
        }
        // Callback'i, bu fragment'ın yaşam döngüsüne bağlı olarak dispatcher'a ekle.
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

}