package com.example.mustafakocer.presentation.feature_cart

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mustafakocer.R
import com.example.mustafakocer.databinding.FragmentCartBinding
import com.example.mustafakocer.domain.util.Resource
import com.example.mustafakocer.presentation.base.BaseFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CartFragment : BaseFragment<FragmentCartBinding>(FragmentCartBinding::inflate) {

    private val viewModel: CartViewModel by viewModels()
    private lateinit var cartListAdapter: CartListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMenu()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        // DEĞİŞTİ: Adapter artık tek bir 'onEvent' lambdası alıyor.
        cartListAdapter = CartListAdapter { event ->
            // Gelen olayın türüne göre doğru ViewModel fonksiyonunu çağırıyoruz.
            when (event) {
                is CartEvent.OnIncrease -> viewModel.onIncreaseClicked(event.productId)
                is CartEvent.OnDecrease -> viewModel.onDecreaseClicked(event.productId)
                is CartEvent.OnRemove -> {
                    showRemoveItemConfirmationDialog(event.productId)
                }

                is CartEvent.OnProductClick -> {
                    // 1. Safe Args ile action'ı oluştur ve productId'yi parametre olarak geç.
                    val action = CartFragmentDirections.actionCartFragmentToProductDetailFragment(
                        productId = event.productId
                    )
                    // 2. NavController'ı kullanarak navigasyonu gerçekleştir.
                    findNavController().navigate(action)
                }
            }
        }

        binding.cartRecyclerView.apply {
            adapter = cartListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    // observeViewModel, setupMenu ve showClearCartConfirmationDialog fonksiyonları
    // bir önceki versiyondaki gibi kalır, onlarda bir değişiklik gerekmez.

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.cartState.collect { resource ->
                        binding.progressbar.isVisible = resource is Resource.Loading
                        val isSuccessAndEmpty =
                            resource is Resource.Success && resource.data.isEmpty()

                        binding.txtEmptyCart.isVisible = isSuccessAndEmpty
                        binding.cardSummary.isVisible =
                            !isSuccessAndEmpty && resource !is Resource.Loading

                        when (resource) {
                            is Resource.Success -> cartListAdapter.submitList(resource.data)
                            is Resource.Error -> {
                                binding.txtEmptyCart.isVisible = true
                                binding.txtEmptyCart.text =
                                    resource.exception.message ?: "Bir hata oluştu."
                            }

                            else -> { /* Idle, Loading */
                            }
                        }
                        requireActivity().invalidateOptionsMenu()
                    }
                }

                launch {
                    viewModel.totalPrice.collect { totalPrice ->
                        binding.txtTotalPrice.text = totalPrice
                    }
                }
            }
        }
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.cart_menu, menu)
            }

            override fun onPrepareMenu(menu: Menu) {
                val clearCartItem = menu.findItem(R.id.action_clear_cart)
                val currentState = viewModel.cartState.value
                clearCartItem?.isVisible =
                    currentState is Resource.Success && currentState.data.isNotEmpty()
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_clear_cart -> {
                        showClearCartConfirmationDialog()
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun showClearCartConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Sepeti Temizle")
            .setMessage("Sepetinizdeki tüm ürünleri silmek istediğinize emin misiniz?")
            .setNegativeButton("Hayır") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("Evet") { dialog, _ ->
                viewModel.onClearCartConfirmed()
                dialog.dismiss()
            }
            .show()
    }

    // YENİ FONKSİYON: Tek bir ürünü silmek için onay diyaloğu gösterir.
    private fun showRemoveItemConfirmationDialog(productId: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Ürünü Kaldır")
            .setMessage("Bu ürünü sepetinizden tamamen kaldırmak istediğinize emin misiniz?")
            .setNegativeButton("Hayır") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("Evet") { dialog, _ ->
                // Kullanıcı "Evet" derse, ViewModel'deki ilgili fonksiyonu çağır.
                viewModel.onRemoveItemConfirmed(productId)
                dialog.dismiss()
            }
            .show()
    }
}