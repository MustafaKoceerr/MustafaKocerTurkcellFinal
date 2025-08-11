package com.example.mustafakocer.presentation.feature_orders

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mustafakocer.R
import com.example.mustafakocer.databinding.FragmentOrderDetailsBinding
import com.example.mustafakocer.domain.model.Order
import com.example.mustafakocer.presentation.base.BaseFragment

class OrderDetailsFragment :
    BaseFragment<FragmentOrderDetailsBinding>(FragmentOrderDetailsBinding::inflate) {

    // Safe Args kütüphanesi tarafından oluşturulan delegeyi kullanarak argümanları alıyoruz.
    private val args: OrderDetailsFragmentArgs by navArgs()

    private lateinit var productListAdapter: OrderProductListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Argüman olarak gelen 'Order' nesnesini al.
        val order = args.order

        setupRecyclerView()
        populateUi(order)
        // YENİ: Başlığı burada manuel olarak ayarlıyoruz.
        setupToolbarTitle(order)

    }

    // YENİ FONKSİYON
    private fun setupToolbarTitle(order: Order) {
        // String kaynağını alıp, içindeki yer tutucuyu sipariş ID'si ile dolduruyoruz.
        val dynamicTitle = getString(R.string.title_order_details, order.id)
        // Activity'nin ActionBar'ına erişip başlığı ayarlıyoruz.
        (activity as? AppCompatActivity)?.supportActionBar?.title = dynamicTitle
    }

    private fun setupRecyclerView() {
        productListAdapter = OrderProductListAdapter()
        binding.productsRecyclerView.apply {
            adapter = productListAdapter
            // Her ürün arasına bir ayırıcı çizgi ekleyelim.
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }
    }

    private fun populateUi(order: Order) {
        // Sipariş özeti kartını doldur.
        binding.apply {
            txtOrderId.text = order.id.toString()
            txtTotalAmount.text = order.discountedTotal
        }

        // Ürün listesini adaptöre gönder.
        productListAdapter.submitList(order.products)
    }
}