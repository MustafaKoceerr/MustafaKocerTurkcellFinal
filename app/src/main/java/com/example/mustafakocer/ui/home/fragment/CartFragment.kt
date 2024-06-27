package com.example.mustafakocer.ui.home.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mustafakocer.data.db.entity.CartRequest
import com.example.mustafakocer.data.model.Resource
import com.example.mustafakocer.databinding.FragmentCartBinding
import com.example.mustafakocer.ui.base.BaseFragment
import com.example.mustafakocer.ui.home.adapter.CartProductAdapter
import com.example.mustafakocer.ui.home.viewmodel.CartViewModel
import com.example.mustafakocer.util.UserId
import com.example.mustafakocer.util.visibleProgressBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CartFragment : BaseFragment<FragmentCartBinding>() {

    private val viewModel: CartViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cartRecyclerView.also { recyclerView ->
            recyclerView.layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        }

        getCartFromDB()
    }

    override fun getFragmentDataBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCartBinding {
        return FragmentCartBinding.inflate(inflater, container, false)
    }

    private fun getCartFromDB() {
        viewLifecycleOwner.lifecycleScope.launch {

            val cartList = viewModel.GetAllCarts(UserId.userId)
            Log.d("cartList", "cartList $cartList")

            val cartRequest = CartRequest(UserId.userId, cartList)
            viewModel.cartInfo(cartRequest)

            observeCartInfo()
        }
    }

    private suspend fun observeCartInfo() {
        viewModel.cart.collect { resource ->
            binding.progressbar.visibleProgressBar(false)

            when (resource) {
                is Resource.Loading -> {
                    // Show loading indicator
                    binding.progressbar.visibleProgressBar(true)
                }

                is Resource.Success -> {
                    // Update UI with products data

                    binding.cartRecyclerView.adapter = CartProductAdapter(resource.value.products!!)

                    binding.txtView.setText("Cart Total Price: $${resource.value.discountedTotal}")
                }

                is Resource.Failure -> {

                }

                is Resource.Waiting -> {
                    binding.progressbar.visibleProgressBar(false)
                }
            }

        }

    }

}