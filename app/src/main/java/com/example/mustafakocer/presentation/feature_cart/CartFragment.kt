package com.example.mustafakocer.presentation.feature_cart

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mustafakocer.R
import com.example.mustafakocer.databinding.FragmentCartBinding
import com.example.mustafakocer.domain.util.Resource
import com.example.mustafakocer.presentation.base.BaseFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CartFragment : BaseFragment<FragmentCartBinding>(FragmentCartBinding::inflate) {

    private val viewModel: CartViewModel by activityViewModels()
    private lateinit var cartListAdapter: CartListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMenu()
        setupRecyclerView()
        observeViewModel()
    }

    // YENİ: Menüyü ve davranışlarını ayarlayan fonksiyon.
    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Menüyü burada inflate ediyoruz.
                menuInflater.inflate(R.menu.cart_menu, menu)
            }

            override fun onPrepareMenu(menu: Menu) {
                // Bu metot, menü her gösterileceğinde çağrılır.
                // İkonun görünürlüğünü ayarlamak için en doğru yer burasıdır.
                val clearCartItem = menu.findItem(R.id.action_clear_cart)
                val currentState = viewModel.cartState.value
                clearCartItem?.isVisible =
                    currentState is Resource.Success && currentState.data.isNotEmpty()
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Tıklama olaylarını burada yönetiyoruz.
                return when (menuItem.itemId) {
                    R.id.action_clear_cart -> {
                        showClearCartConfirmationDialog()
                        true // Olayın bizim tarafımızdan işlendiğini belirtir.
                    }

                    else -> false // İşlemediğimiz diğer item'lar için false döndür.
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED) // Yaşam döngüsüne bağlıyoruz.
    }

    private fun showClearCartConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Sepeti Temizle")
            .setMessage("Sepetinizdeki tüm ürünleri silmek istediğinize emin misiniz?")
            .setNegativeButton("Hayır") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Evet") { dialog, _ ->
                viewModel.onClearCartConfirmed()
                dialog.dismiss()
            }
            .show()
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
                launch {
                    viewModel.cartState.collect { resource ->
                        // ... (bu blok aynı)
                        binding.progressbar.isVisible = resource is Resource.Loading
                        val isSuccessAndEmpty =
                            resource is Resource.Success && resource.data.isEmpty()
                        binding.txtEmptyCart.isVisible =
                            resource is Resource.Error || isSuccessAndEmpty
                        binding.txtTotalPrice.isVisible = !isSuccessAndEmpty
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
                        // DEĞİŞTİ: invalidateOptionsMenu() artık onPrepareMenu'yü tetikleyecek.
                        requireActivity().invalidateOptionsMenu()
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