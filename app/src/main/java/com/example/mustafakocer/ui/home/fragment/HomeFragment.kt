package com.example.mustafakocer.ui.home.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mustafakocer.R
import com.example.mustafakocer.data.db.entity.Cart
import com.example.mustafakocer.data.db.entity.LikedProduct
import com.example.mustafakocer.data.model.Product
import com.example.mustafakocer.data.model.Resource
import com.example.mustafakocer.databinding.FragmentHomeBinding
import com.example.mustafakocer.ui.base.BaseFragment
import com.example.mustafakocer.ui.home.LikeButtonClickListener
import com.example.mustafakocer.ui.home.adapter.ProductAdapterHome
import com.example.mustafakocer.ui.home.viewmodel.HomeViewModel
import com.example.mustafakocer.util.UserId
import com.example.mustafakocer.util.visibleProgressBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(), LikeButtonClickListener {

    private var isAdapterAttached = 0


    private val viewModel: HomeViewModel by viewModels()
    private lateinit var productIdList: MutableList<Int>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.homeRecyclerView.also { recyclerView ->
            recyclerView.layoutManager =
                GridLayoutManager(requireContext(), 1, GridLayoutManager.VERTICAL, false)
        }
        viewModel.getProducts()
        observeProducts()

    }

    override fun getFragmentDataBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }


    private fun observeProducts() {
        viewLifecycleOwner.lifecycleScope.launch {

            viewModel.products.collectLatest { resource ->
                binding.progressbar.visibleProgressBar(false)

                when (resource) {
                    is Resource.Loading -> {
                        binding.progressbar.visibleProgressBar(true)
                    }

                    is Resource.Success -> {
                        // Update UI with products data

                        val likedProductList = withContext(Dispatchers.IO) {
                            viewModel.gelAllLikedProductsDB(UserId.userId)
                        }

                        productIdList =
                            likedProductList!!.mapNotNull { it.productId }.toMutableList()

                        Log.d("likedProductList", "ID LIST $productIdList")

                        if (isAdapterAttached == 0) {
                            binding.homeRecyclerView.adapter =
                                ProductAdapterHome(
                                    resource.value.products,
                                    this@HomeFragment,
                                    productIdList
                                )
                            isAdapterAttached++
                        } else {
                            binding.homeRecyclerView.adapter!!.notifyDataSetChanged()
                        }

                        // her veri geldiğinde db'ye bakıyor ve oradan karşılaştırıyor
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


    override fun onRecyclerViewItemClick(view: View, product: Product) {
        when (view.id) {
            R.id.btnLike -> {

                viewLifecycleOwner.lifecycleScope.launch {
                    val value = viewModel.getOneProductsDB(UserId.userId, product.id!!)

                    if (value == null) {
                        val likedProduct = LikedProduct(null, UserId.userId, 0, product.id, product)
                        viewModel.insertProductDB(likedProduct)
                        productIdList.add(product.id!!)
                    } else {
                        viewModel.deleteProductDB(UserId.userId, product.id!!)
                        productIdList.remove(product.id)
                    }
                    updateLikeButton()
                }
            }

            R.id.btnPlus -> {
                Toast.makeText(requireContext(), "Item Added", Toast.LENGTH_SHORT).show()
                viewLifecycleOwner.lifecycleScope.launch {
                    val cart = viewModel.GetOneCart(UserId.userId, product.id!!)

                    if (cart == null) {
                        // üründen yoktur 1 tane ekle
                        val addedCart = Cart(null, UserId.userId, product.id, 1)
                        viewModel.InsertCart(addedCart)
                    } else {
                        // üründen var sayısını arttır.
                        val updatedCart = cart.copy(quantity = cart.quantity!! + 1)
                        viewModel.UpdateCart(updatedCart)
                    }
                }

            }

            R.id.btnMinus -> {
                Toast.makeText(requireContext(), "Item Deleted", Toast.LENGTH_SHORT).show()
                viewLifecycleOwner.lifecycleScope.launch {
                    val cart = viewModel.GetOneCart(UserId.userId, product.id!!)

                    if (cart == null) {
                        // üründen yoktur bir işlem yapma
                    } else {
                        // üründen var sayısını azalt.
                        val quantity = cart.quantity!! - 1
                        if (quantity == 0) {
                            // sil
                            viewModel.DeleteCart(cart.userId, cart.id!!)
                        } else {
                            val updatedCart = cart.copy(quantity = quantity)
                            viewModel.UpdateCart(updatedCart)
                        }
                    }
                }
            }


        }
    }

    private fun updateLikeButton() {
        binding.homeRecyclerView.adapter!!.notifyDataSetChanged()

    }


}
