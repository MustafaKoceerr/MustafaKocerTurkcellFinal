package com.example.mustafakocer.presentation.feature_product_category

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductsByCategoryFragment : BaseFragment<FragmentProductsByCategoryBinding>(
    FragmentProductsByCategoryBinding::inflate
) {
    // ViewModel'i Hilt ile alıyoruz. Activity-scoped değil, Fragment-scoped.
    private val viewModel: CategoryViewModel by viewModels()
    private val args: ProductsByCategoryFragmentArgs by navArgs()

    // ProductAdapter yerine yeniden kullanılabilir ProductListAdapter'ı kullanıyoruz.
    private lateinit var productListAdapter: ProductListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeProductPagingFlow()
        observeLoadState()

        // Fragment oluşturulduğunda, ViewModel'e hangi kategorinin seçildiğini bildir.
        // Bu, PagingData akışını tetikleyecektir.
        val categoryName = args.categoryName
        viewModel.onCategorySelected(categoryName)

        // Toolbar başlığını ayarlayabilirsin
        // (activity as AppCompatActivity).supportActionBar?.title = categoryName.capitalize()
    }

    private fun setupRecyclerView() {
        productListAdapter = ProductListAdapter(
            onProductClick = { product ->
                Toast.makeText(requireContext(), "${product.title} clicked", Toast.LENGTH_SHORT)
                    .show()
                // TODO: Ürün detay sayfasına navigasyon eklenecek.
            },
            onAddToCartClick = { product ->
                Toast.makeText(
                    requireContext(),
                    "${product.title} added to cart",
                    Toast.LENGTH_SHORT
                ).show()
                // TODO: Sepete ekleme mantığı eklenecek.
            },
            onRemoveFromCartClick = { product ->
                Toast.makeText(
                    requireContext(),
                    "${product.title} removed from cart",
                    Toast.LENGTH_SHORT
                ).show()
                // TODO: Sepetten çıkarma mantığı eklenecek.
            }
        )

        binding.productsByCategoryRecyclerView.apply {
            adapter = productListAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    private fun observeProductPagingFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // ViewModel'den gelen PagingData akışını dinle.
                viewModel.productsByCategoryFlow.collectLatest { pagingData ->
                    // Gelen yeni PagingData'yı adaptöre gönder.
                    productListAdapter.submitData(pagingData)
                }
            }
        }
    }

    private fun observeLoadState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Adaptörün yükleme durumlarını dinle.
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
}