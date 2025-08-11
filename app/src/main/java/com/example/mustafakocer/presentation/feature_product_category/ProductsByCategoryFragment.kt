package com.example.mustafakocer.presentation.feature_product_category

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels // DEĞİŞTİ: activityViewModels'ı import et
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mustafakocer.databinding.FragmentProductsByCategoryBinding
import com.example.mustafakocer.presentation.base.BaseFragment
import com.example.mustafakocer.presentation.common.ProductListAdapter
import com.example.mustafakocer.presentation.feature_cart.CartViewModel // YENİ: CartViewModel'ı import et
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductsByCategoryFragment : BaseFragment<FragmentProductsByCategoryBinding>(
    FragmentProductsByCategoryBinding::inflate
) {
    // CategoryViewModel, bu fragment'a özel state'i (seçilen kategori ve ürünleri) yönetir.
    private val viewModel: CategoryViewModel by viewModels()

    // YENİ: CartViewModel, Activity kapsamında paylaşılan sepet state'ini yönetir.
    private val cartViewModel: CartViewModel by activityViewModels()

    private val args: ProductsByCategoryFragmentArgs by navArgs()
    private lateinit var productListAdapter: ProductListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeProductPagingFlow()
        observeLoadState()
        observeCartState() // YENİ: Sepet durumunu dinlemeye başla.

        val categoryName = args.categoryName
        viewModel.onCategorySelected(categoryName)
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

        binding.productsByCategoryRecyclerView.apply {
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

    private fun observeProductPagingFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.productsByCategoryFlow.collectLatest { pagingData ->
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