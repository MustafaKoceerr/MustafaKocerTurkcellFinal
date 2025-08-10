package com.example.mustafakocer.presentation.feature_cart

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mustafakocer.databinding.FragmentCartBinding
import com.example.mustafakocer.domain.util.Resource
import com.example.mustafakocer.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CartFragment : BaseFragment<FragmentCartBinding>(FragmentCartBinding::inflate) {

    private val viewModel: CartViewModel by viewModels()
    private lateinit var cartListAdapter: CartListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        cartListAdapter = CartListAdapter(
            onIncreaseClick = { productId ->
                viewModel.onIncreaseClicked(productId)
            },
            onDecreaseClick = { productId ->
                viewModel.onDecreaseClicked(productId)
            },
            onProductClick = { productId ->
                // TODO: Ürün detay sayfasına navigasyon eklenecek.
                Toast.makeText(requireContext(), "Ürün ID: $productId tıklandı", Toast.LENGTH_SHORT)
                    .show()
            }
        )

        binding.cartRecyclerView.apply {
            adapter = cartListAdapter
            // Home/Search ekranlarıyla aynı görünmesi için GridLayoutManager kullanıyoruz.
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                // 1. Sepet durumunu ve listesini dinle
                launch {
                    viewModel.cartState.collect { resource ->
                        binding.progressbar.isVisible = resource is Resource.Loading
                        binding.txtEmptyCart.isVisible = resource is Resource.Error ||
                                (resource is Resource.Success && resource.data.isEmpty())

                        when (resource) {
                            is Resource.Success -> {
                                cartListAdapter.submitList(resource.data)
                            }

                            is Resource.Error -> {
                                binding.txtEmptyCart.text = resource.exception.message
                            }

                            else -> { /* No-op */
                            }
                        }
                    }
                }


                // 2. Toplam fiyatı dinle
                launch {
                    viewModel.totalPrice.collect { totalPrice ->
                        // Toplam fiyatı gösteren TextView'i güncelle
                        binding.txtTotalPrice.text = "Toplam: $${totalPrice}"
                    }
                }
            }
        }
    }
}