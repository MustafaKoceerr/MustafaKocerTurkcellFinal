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

class OrderDetailsFragment :
    BaseFragment<FragmentOrderDetailsBinding>(FragmentOrderDetailsBinding::inflate) {

    private val args: OrderDetailsFragmentArgs by navArgs()
    private lateinit var productListAdapter: OrderProductListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val order = args.order

        setupRecyclerView()
        populateUi(order)
        setupToolbarTitle(order)
    }

    private fun setupToolbarTitle(order: Order) {
        val dynamicTitle = getString(R.string.title_order_details, order.id)
        (activity as? AppCompatActivity)?.supportActionBar?.title = dynamicTitle
    }

    private fun setupRecyclerView() {
        // DEĞİŞTİ: Adapter'ı, tıklama olayında navigasyonu tetikleyecek
        // bir lambda ile oluşturuyoruz.
        productListAdapter = OrderProductListAdapter { productId ->
            // Tıklanan ürünün ID'si ile navigasyonu tetikle.
            val action = OrderDetailsFragmentDirections.actionOrderDetailsFragmentToProductDetailFragment(
                productId = productId
            )
            findNavController().navigate(action)
        }

        binding.productsRecyclerView.apply {
            adapter = productListAdapter
            // Her ürün arasına bir ayırıcı çizgi ekleyelim.
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }
    }

    private fun populateUi(order: Order) {
        binding.apply {
            txtOrderId.text = order.id.toString()
            txtTotalAmount.text = order.discountedTotal
            // YENİ: Diğer UI elemanlarını da dolduralım.
            txtItemCount.text = "${order.totalProducts} ürün"
            // Tarih ve durum gibi diğer alanlar da burada doldurulabilir.
        }

        // Ürün listesini adaptöre gönder.
        productListAdapter.submitList(order.products)
    }
}