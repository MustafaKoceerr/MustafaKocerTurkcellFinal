package com.example.mustafakocer.presentation.feature_orders

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mustafakocer.R
import com.example.mustafakocer.databinding.FragmentOrderDetailsBinding
import com.example.mustafakocer.domain.model.Order
import com.example.mustafakocer.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * Displays the details of a single [Order] object received via navigation arguments.
 * This fragment does not have its own ViewModel and relies entirely on the passed data.
 */
@AndroidEntryPoint
class OrderDetailsFragment :
    BaseFragment<FragmentOrderDetailsBinding>(FragmentOrderDetailsBinding::inflate) {

    private val args: OrderDetailsFragmentArgs by navArgs()
    private lateinit var productListAdapter: OrderProductListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val order = args.order
        if (order != null) {
            binding.stateLayout.showContent()
            setupToolbarTitle(order)
            setupRecyclerView(order)
            populateUi(order)
        } else {
            // Handle the edge case where the order data is missing.
            binding.stateLayout.showError(
                title = getString(R.string.error_title_generic),
                subtitle = getString(R.string.error_message_order_not_found)
            )
            binding.stateLayout.onRetry = {
                findNavController().popBackStack()
            }
        }
    }

    /**
     * Sets the toolbar title dynamically with the order ID.
     */
    private fun setupToolbarTitle(order: Order) {
        val dynamicTitle = getString(R.string.title_order_details, order.id)
        (activity as? AppCompatActivity)?.supportActionBar?.title = dynamicTitle
    }

    /**
     * Initializes the RecyclerView to display the products within the order.
     */
    private fun setupRecyclerView(order: Order) {
        productListAdapter = OrderProductListAdapter { productId ->
            val action =
                OrderDetailsFragmentDirections.actionOrderDetailsFragmentToProductDetailFragment(
                    productId
                )
            findNavController().navigate(action)
        }

        binding.stateLayout.findViewById<RecyclerView>(R.id.productsRecyclerView).apply {
            adapter = productListAdapter
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }
        productListAdapter.submitList(order.products)
    }

    /**
     * Populates the order summary card with the order's details.
     */
    private fun populateUi(order: Order) {
        val summaryCard =
            binding.stateLayout.findViewById<com.google.android.material.card.MaterialCardView>(
                R.id.cardOrderSummary
            )

        summaryCard.findViewById<TextView>(R.id.txtOrderId).text = order.id.toString()
        summaryCard.findViewById<TextView>(R.id.txtTotalAmount).text = order.discountedTotal
        summaryCard.findViewById<TextView>(R.id.txtItemCount).text =
            getString(R.string.order_details_item_count_format, order.totalProducts)
    }
}