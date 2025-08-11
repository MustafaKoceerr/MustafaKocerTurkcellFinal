package com.example.mustafakocer.presentation.feature_product_list

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mustafakocer.databinding.FragmentHomeBinding
import com.example.mustafakocer.presentation.base.BaseFragment
import com.example.mustafakocer.presentation.common.ProductListAdapter
import com.example.mustafakocer.presentation.feature_cart.CartViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var productListAdapter: ProductListAdapter

    // YENİ: CartViewModel, Activity kapsamında paylaşılan sepet state'ini yönetir.
    private val cartViewModel: CartViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeProductPagingFlow()
        observeLoadState()
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
                // DEĞİŞTİ: Tıklama olayını artık CartViewModel'a iletiyoruz.
                cartViewModel.onIncreaseClicked(product.id)
            },
            onRemoveFromCartClick = { product ->
                // DEĞİŞTİ: Tıklama olayını artık CartViewModel'a iletiyoruz.
                cartViewModel.onDecreaseClicked(product.id)
            }
        )

        binding.homeRecyclerView.apply {
            adapter = productListAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    // YENİ: CartViewModel'daki cartMap'i dinler ve adaptörü günceller.
    private fun observeCartState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                cartViewModel.cartMap.collectLatest { cartMap ->
                    // Adaptördeki yeni fonksiyonumuzu çağırarak haritayı iletiyoruz.
                    productListAdapter.updateCartMap(cartMap)
                }
            }
        }
    }

    private fun observeProductPagingFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED)
            {  // ViewModel'den gelen PagingData akışını dinliyoruz.
                // collectLatest, yeni bir PagingData geldiğinde (örn: yeni arama)
                // eskisini iptal edip yenisini işlemeye başlar.
                viewModel.productsFlow.collectLatest { pagingData ->
                    // Gelen yeni PagingData'yı adaptöre gönderiyoruz.
                    productListAdapter.submitData(pagingData)
                }
            }
        }
    }

    private fun observeLoadState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                productListAdapter.loadStateFlow.collectLatest { loadStates ->
                    // Sadece ilk yükleme (REFRESH) durumunu kontrol ediyoruz.
                    val refreshState = loadStates.refresh

                    // Yükleniyorsa progressBar'ı göster
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
}