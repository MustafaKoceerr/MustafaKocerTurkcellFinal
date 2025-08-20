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
import com.example.mustafakocer.domain.model.CartItem
import com.example.mustafakocer.domain.util.Resource
import com.example.mustafakocer.presentation.base.BaseFragment
import com.example.mustafakocer.presentation.common.UiErrorMapper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CartFragment : BaseFragment<FragmentCartBinding>(FragmentCartBinding::inflate) {

    private val viewModel: CartViewModel by viewModels()
    private lateinit var cartListAdapter: CartListAdapter

    @Inject
    lateinit var injectedUiErrorMapper: UiErrorMapper
    override val uiErrorMapper: UiErrorMapper by lazy { injectedUiErrorMapper }
    override fun onRetry() { /* Firebase is reactive, no manual retry needed. */ }

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
                // Observe the main cart state
                launch {
                    viewModel.cartState.collect { resource ->
                        handleUiState(resource)
                        requireActivity().invalidateOptionsMenu() // Update menu visibility
                    }
                }
                // Observe the total price separately
                launch {
                    viewModel.totalPrice.collectLatest { totalPrice ->
                        binding.txtTotalPrice.text = totalPrice
                    }
                }
            }
        }
    }

    private fun handleUiState(resource: Resource<List<CartItem>>) {
        val isLoading = resource is Resource.Loading
        binding.viewLoadingStub.isVisible = isLoading

        when (resource) {
            is Resource.Success -> {
                val items = resource.data
                handleUiVisibility(items.isNotEmpty())
                handleEmptyState(items.isEmpty())
                cartListAdapter.submitList(items)
                hideErrorState()
            }
            is Resource.Error -> {
                handleUiVisibility(false)
                handleEmptyState(false)
                handleErrorState(binding.viewErrorStub, resource.exception)
            }
            else -> { /* Loading or Idle */
                handleUiVisibility(false)
                handleEmptyState(false)
                hideErrorState()
            }
        }
    }

    private fun handleUiVisibility(isSuccessAndNotEmpty: Boolean) {
        binding.contentView.isVisible = isSuccessAndNotEmpty
        binding.cardSummary.isVisible = isSuccessAndNotEmpty
    }

    private fun handleEmptyState(isSuccessAndEmpty: Boolean) {
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
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.cart_menu, menu)
            }

            override fun onPrepareMenu(menu: Menu) {
                val clearCartItem = menu.findItem(R.id.action_clear_cart)
                val currentState = viewModel.cartState.value
                clearCartItem?.isVisible = currentState is Resource.Success && currentState.data.isNotEmpty()
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return if (menuItem.itemId == R.id.action_clear_cart) {
                    showClearCartConfirmationDialog()
                    true
                } else false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun showClearCartConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_title_clear_cart)
            .setMessage(R.string.dialog_message_clear_cart)
            .setNegativeButton(R.string.action_cancel) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(R.string.action_confirm) { dialog, _ ->
                viewModel.onClearCartConfirmed()
                dialog.dismiss()
            }
            .show()
    }

    private fun showRemoveItemConfirmationDialog(productId: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_title_remove_item)
            .setMessage(R.string.dialog_message_remove_item)
            .setNegativeButton(R.string.action_cancel) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(R.string.action_confirm) { dialog, _ ->
                viewModel.onRemoveItemConfirmed(productId)
                dialog.dismiss()
            }
            .show()
    }
}