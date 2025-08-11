package com.example.mustafakocer.presentation.feature_cart

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
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

    private val viewModel: CartViewModel by activityViewModels()
    private lateinit var cartListAdapter: CartListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        cartListAdapter = CartListAdapter(
            onIncreaseClick = viewModel::onIncreaseClicked,
            onDecreaseClick = viewModel::onDecreaseClicked,
            onProductClick = { productId ->
                // TODO: Ürün detay sayfasına navigasyon eklenecek.
                Toast.makeText(requireContext(), "Ürün ID: $productId tıklandı", Toast.LENGTH_SHORT)
                    .show()
            }
        )

        binding.cartRecyclerView.apply {
            adapter = cartListAdapter
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

                        val isSuccessAndEmpty =
                            resource is Resource.Success && resource.data.isEmpty()
                        binding.txtEmptyCart.isVisible =
                            resource is Resource.Error || isSuccessAndEmpty
                        binding.txtTotalPrice.isVisible =
                            !isSuccessAndEmpty // YENİ: Fiyatı sadece sepet boş değilse göster

                        when (resource) {
                            is Resource.Success -> {
                                cartListAdapter.submitList(resource.data)
                                if (isSuccessAndEmpty) binding.txtEmptyCart.text =
                                    "Sepetinizde ürün bulunmuyor."
                            }

                            is Resource.Error -> {
                                binding.txtEmptyCart.text =
                                    resource.exception.message ?: "Bir hata oluştu."
                            }

                            else -> { /* Idle, Loading */
                            }
                        }
                    }
                }

                // 2. Toplam fiyatı dinle
                launch {
                    viewModel.totalPrice.collect { totalPrice ->
                        binding.txtTotalPrice.text = "Toplam: $${totalPrice}"
                    }
                }
            }
        }
    }
}