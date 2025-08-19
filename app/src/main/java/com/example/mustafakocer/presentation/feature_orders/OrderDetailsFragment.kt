package com.example.mustafakocer.presentation.feature_orders

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mustafakocer.R
import com.example.mustafakocer.databinding.FragmentOrderDetailsBinding
import com.example.mustafakocer.domain.model.Order
import com.example.mustafakocer.presentation.base.BaseFragment
import com.example.mustafakocer.presentation.common.UiErrorMapper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OrderDetailsFragment :
    BaseFragment<FragmentOrderDetailsBinding>(FragmentOrderDetailsBinding::inflate) {

    private val args: OrderDetailsFragmentArgs by navArgs()
    private lateinit var productListAdapter: OrderProductListAdapter

    // --- BaseFragment Implementasyonu ---
    // Bu fragment, BaseFragment'in hata yönetimi özelliklerini kullanmadığı için,
    // bu property'leri sağlamamız gerekiyor ama içleri boş kalabilir veya
    // bir NotImplementedError fırlatabilir.
    // Ancak enjeksiyonun çalışması için bu gereklidir.
    @Inject
    lateinit var injectedUiErrorMapper: UiErrorMapper
    override val uiErrorMapper: UiErrorMapper by lazy { injectedUiErrorMapper }

    override fun onRetry() {
        // Bu ekranda ağ isteği olmadığı için retry mantığına gerek yok.
    }
    // ------------------------------------

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val order = args.order

        setupRecyclerView(order)
        populateUi(order)
        setupToolbarTitle(order)
    }

    private fun setupToolbarTitle(order: Order) {
        val dynamicTitle = getString(R.string.title_order_details, order.id)
        (activity as? AppCompatActivity)?.supportActionBar?.title = dynamicTitle
    }

    private fun setupRecyclerView(order: Order) {
        productListAdapter = OrderProductListAdapter { productId ->
            val action = OrderDetailsFragmentDirections.actionOrderDetailsFragmentToProductDetailFragment(productId)
            findNavController().navigate(action)
        }

        binding.productsRecyclerView.apply {
            adapter = productListAdapter
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }
        productListAdapter.submitList(order.products)
    }

    private fun populateUi(order: Order) {
        binding.apply {
            txtOrderId.text = order.id.toString()
            txtTotalAmount.text = order.discountedTotal
            txtItemCount.text = getString(R.string.order_details_item_count_format, order.totalProducts)
        }
    }
}