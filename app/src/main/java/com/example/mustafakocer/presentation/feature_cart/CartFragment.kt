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
import com.example.mustafakocer.databinding.LayoutStateEmptyBinding
import com.example.mustafakocer.domain.util.Resource
import com.example.mustafakocer.presentation.base.BaseFragment
import com.example.mustafakocer.presentation.common.UiErrorMapper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CartFragment : BaseFragment<FragmentCartBinding>(FragmentCartBinding::inflate) {

    private val viewModel: CartViewModel by viewModels()
    private lateinit var cartListAdapter: CartListAdapter

    @Inject
    lateinit var injectedUiErrorMapper: UiErrorMapper

    // --- BaseFragment Implementasyonu ---
    override val uiErrorMapper: UiErrorMapper by lazy { injectedUiErrorMapper }

    override fun onRetry() {
        // Sepet verisi Firebase'den reaktif olarak geldiği için,
        // ViewModel'de manuel bir tetikleme fonksiyonu yok.
        // Bu yüzden bu fonksiyon şimdilik boş kalabilir.
    }
    // ------------------------------------

    // ViewStub'lar inflate edildikten sonra binding'lerini tutmak için.
    private var emptyBinding: LayoutStateEmptyBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMenu()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        cartListAdapter = CartListAdapter { event ->
            when (event) {
                is CartEvent.OnIncrease -> viewModel.onIncreaseClicked(event.productId)
                is CartEvent.OnDecrease -> viewModel.onDecreaseClicked(event.productId)
                is CartEvent.OnRemove -> showRemoveItemConfirmationDialog(event.productId)
                is CartEvent.OnProductClick -> {
                    val action = CartFragmentDirections.actionCartFragmentToProductDetailFragment(event.productId)
                    findNavController().navigate(action)
                }
            }
        }
        binding.contentView.apply {
            adapter = cartListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // 1. Ana sepet durumunu dinle
                launch {
                    viewModel.cartState.collect { resource ->
                        val isLoading = resource is Resource.Loading
                        val isError = resource is Resource.Error
                        val isSuccessAndEmpty = resource is Resource.Success && resource.data.isEmpty()

                        // Görünürlükleri yönet
                        binding.viewLoadingStub.isVisible = isLoading
                        binding.contentView.isVisible = resource is Resource.Success && !isSuccessAndEmpty
                        binding.cardSummary.isVisible = resource is Resource.Success && !isSuccessAndEmpty

                        if (isError) {
                            handleErrorState(binding.viewErrorStub, (resource as Resource.Error).exception)
                        } else {
                            hideErrorState()
                        }

                        if (isSuccessAndEmpty) {
                            if (emptyBinding == null) {
                                emptyBinding = LayoutStateEmptyBinding.bind(binding.viewEmptyStub.inflate())
                            }
                            emptyBinding?.root?.isVisible = true
                            emptyBinding?.txtEmptyTitle?.setText(R.string.cart_empty_title)
                            emptyBinding?.txtEmptySubtitle?.setText(R.string.cart_empty_subtitle)
                        } else {
                            emptyBinding?.root?.isVisible = false
                        }

                        if (resource is Resource.Success) {
                            cartListAdapter.submitList(resource.data)
                        }

                        requireActivity().invalidateOptionsMenu()
                    }
                }

                // 2. Toplam fiyatı dinle
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