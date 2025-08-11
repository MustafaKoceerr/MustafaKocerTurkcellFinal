package com.example.mustafakocer.presentation.feature_product_search

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mustafakocer.databinding.FragmentSearchBinding
import com.example.mustafakocer.presentation.base.BaseFragment
import com.example.mustafakocer.presentation.common.ProductListAdapter
import com.example.mustafakocer.presentation.feature_cart.CartViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::inflate) {

    private val viewModel: SearchViewModel by viewModels()

    // YENİ: CartViewModel, Activity kapsamında paylaşılan sepet state'ini yönetir.
    private val cartViewModel: CartViewModel by activityViewModels()

    // ProductSearchAdapter yerine, yeniden kullanılabilir ProductListAdapter'ı kullanıyoruz.
    private lateinit var productListAdapter: ProductListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchView()
        observeSearchResults()
        observeLoadState() // YENİ: Yükleme ve hata durumlarını dinle.
        observeCartState() // YENİ: Sepet durumunu dinlemeye başla.
    }

    private fun setupRecyclerView() {
        productListAdapter = ProductListAdapter(
            onProductClick = { product ->
                Toast.makeText(requireContext(), "${product.title} clicked", Toast.LENGTH_SHORT)
                    .show()
                // TODO: Ürün detay sayfasına navigasyon eklenecek.
            },
            onAddToCartClick = { product ->
                // DEĞİŞTİ: Tıklama olayını CartViewModel'a iletiyoruz.
                cartViewModel.onIncreaseClicked(product.id)
            },
            onRemoveFromCartClick = { product ->
                // DEĞİŞTİ: Tıklama olayını CartViewModel'a iletiyoruz.
                cartViewModel.onDecreaseClicked(product.id)
            }
        )

        binding.searchRecyclerView.apply {
            adapter = productListAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    // YENİ: CartViewModel'daki cartMap'i dinler ve adaptörü günceller.
    private fun observeCartState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                cartViewModel.cartMap.collectLatest { cartMap ->
                    productListAdapter.updateCartMap(cartMap)
                }
            }
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Submit'te bir şey yapmaya gerek yok, onQueryTextChange yeterli.
                binding.searchView.clearFocus() // Klavyeyi gizle
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.onSearchQueryChanged(newText.orEmpty())
                return true
            }
        })
    }

    private fun observeSearchResults() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.products.collectLatest { pagingData ->
                    productListAdapter.submitData(pagingData)
                }
            }
        }
    }

    // HomeFragment'tan kopyalanan, yükleme ve hata durumlarını yöneten fonksiyon.
    private fun observeLoadState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                productListAdapter.loadStateFlow.collectLatest { loadStates ->
                    val refreshState = loadStates.refresh
                    binding.progressbar.isVisible = refreshState is LoadState.Loading

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
}