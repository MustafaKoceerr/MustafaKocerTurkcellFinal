package com.example.mustafakocer.presentation.feature_cart

import android.content.DialogInterface
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
import androidx.recyclerview.widget.RecyclerView
import com.example.mustafakocer.databinding.FragmentCartBinding
import com.example.mustafakocer.domain.util.Resource
import com.example.mustafakocer.presentation.base.BaseFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.mustafakocer.R
import java.text.NumberFormat

/**
 * Displays the user's shopping cart.
 * This fragment is responsible for setting up the UI, observing state changes from the
 * [CartViewModel], and delegating user interactions back to the ViewModel.
 */
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

    /**
     * Initializes the RecyclerView, its adapter, and handles item click events.
     */
    private fun setupRecyclerView() {
        cartListAdapter = CartListAdapter { event ->
            when (event) {
                is CartEvent.OnIncrease -> viewModel.onIncreaseClicked(event.productId)
                is CartEvent.OnDecrease -> viewModel.onDecreaseClicked(event.productId)
                is CartEvent.OnRemove -> showRemoveItemConfirmationDialog(event.productId)
                is CartEvent.OnProductClick -> {
                    val action =
                        CartFragmentDirections.actionCartFragmentToProductDetailFragment(event.productId)
                    findNavController().navigate(action)
                }
            }
        }

        binding.stateLayout.onRetry = { /* no-op */ }

        binding.stateLayout.findViewById<RecyclerView>(R.id.contentView).apply {
            adapter = cartListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    /**
     * Subscribes to the StateFlows exposed by the [CartViewModel] to update the UI
     * in a lifecycle-aware manner.
     */
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observe the main cart state (loading, error, success, empty)
                launch {
                    viewModel.cartState.collect { resource ->
                        binding.cardSummary.isVisible =
                            resource is Resource.Success && resource.data.isNotEmpty()

                        requireActivity().invalidateOptionsMenu()

                        when (resource) {
                            is Resource.Loading -> binding.stateLayout.showLoading()
                            is Resource.Error -> binding.stateLayout.showError(
                                subtitle = resource.exception.message
                            )

                            is Resource.Success -> {
                                val items = resource.data
                                if (items.isEmpty()) {
                                    binding.stateLayout.showEmpty()
                                } else {
                                    cartListAdapter.submitList(items)
                                    binding.stateLayout.showContent()
                                }
                            }

                            is Resource.Idle -> Unit
                        }
                    }
                }
                // Observe the total price separately
                launch {
                    viewModel.totalPrice.collectLatest { price ->
                        val locale = requireContext().resources.configuration.locales[0]
                        val currency = NumberFormat.getCurrencyInstance(locale).apply {
                            isGroupingUsed = true
                            minimumFractionDigits = 2
                            maximumFractionDigits = 2
                        }
                        val priceText = currency.format(price)
                        binding.txtTotalPrice.text =
                            getString(R.string.price_format_dollar, priceText) // %1$s
                    }
                }
            }
        }
    }

    /**
     * Sets up the toolbar menu for this fragment.
     */
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
            .setNegativeButton(R.string.action_cancel) { dialog: DialogInterface, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.action_confirm) { dialog: DialogInterface, _ ->
                viewModel.onClearCartConfirmed()
                dialog.dismiss()
            }
            .show()
    }

    private fun showRemoveItemConfirmationDialog(productId: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_title_remove_item)
            .setMessage(R.string.dialog_message_remove_item)
            .setNegativeButton(R.string.action_cancel) { dialog: DialogInterface, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.action_confirm) { dialog: DialogInterface, _ ->
                viewModel.onRemoveItemConfirmed(productId)
                dialog.dismiss()
            }
            .show()
    }
}