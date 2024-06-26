package com.example.mustafakocer.ui.home.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
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
import com.example.mustafakocer.ui.home.adapter.ProductAdapter2
import com.example.mustafakocer.ui.home.viewmodel.HomeViewModel
import com.example.mustafakocer.util.UserId
import com.example.mustafakocer.util.visibleProgressBar
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
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


        /*
        database listesi getirilecek ve user id getirilecek.
         */

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
            /*
            When collecting from a StateFlow in your UI, you should typically use Dispatchers.Main.
            This is because you usually want to update the UI in response to the collected state,
            and all UI updates must occur on the main thread.
             */
            viewModel.products.collectLatest { resource ->
                binding.progressbar.visibleProgressBar(false)

                when (resource) {
                    is Resource.Loading -> {
                        // Show loading indicator
                        binding.progressbar.visibleProgressBar(true)
                    }

                    is Resource.Success -> {
                        // Update UI with products data
                        // todo recyler view DESING YAP
                        Toast.makeText(
                            requireContext(),
                            "Urunler Geldi${resource.value.products}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("flow", "${resource.value.products}")
                        // Use the products data to update the UI
                        /*
                        database'den islike = true olanı çek.
                        onların id'lerini al.
                        id'leri liste halinde alıp recylerview'e gönder.
                        recycler view'de karşılaştırma ya, eğer ürünün id'si çektiğin id listesinde var ise, kalbi kırmızı yaptırt
                        yok ise bir şey yaptırtma.

                        var ise ve tekrar tıklandıysa, update ile isliked'ı false yap, değiştir.

                         */
                        val likedProductList = withContext(Dispatchers.IO) {
                            viewModel.gelAllLikedProductsDB(UserId.userId)
                        }

                        Log.d("likedProductList", "Listem geldi $likedProductList")

                        productIdList =
                            likedProductList!!.mapNotNull { it.productId }.toMutableList()
                        Log.d("likedProductList", "ID LIST $productIdList")

                        if (isAdapterAttached == 0) {
                            binding.homeRecyclerView.adapter =
                                ProductAdapter2(
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
                        Toast.makeText(
                            requireContext(),
                            "Hata ${resource.errorCode}  ${resource.errorBody}",
                            Toast.LENGTH_SHORT
                        ).show()

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
                // todo db'ye ekleme işlemi
                Log.d("like", "product: $product")

                // todo eğer liked product list'te yoksa bunu yapacağım

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
                Toast.makeText(requireContext(), "plus", Toast.LENGTH_SHORT).show()
                viewLifecycleOwner.lifecycleScope.launch {
                    val cart = viewModel.GetOneCart(UserId.userId, product.id!!)

                    if (cart == null) {
                        // üründen yoktur 1 tane ekle
                        val addedCart = Cart(null, UserId.userId, product.id, 1)
                        viewModel.InsertCart(addedCart)
                        // todo snackbar göster
                    } else {
                        // üründen var sayısını arttır.
                        val updatedCart = cart.copy(quantity = cart.quantity!! + 1)
                        viewModel.UpdateCart(updatedCart)
                        // todo snackbar göster
                    }
                }

            }

            R.id.btnMinus -> {
                Toast.makeText(requireContext(), "minus", Toast.LENGTH_SHORT).show()
                viewLifecycleOwner.lifecycleScope.launch {
                    val cart = viewModel.GetOneCart(UserId.userId, product.id!!)

                    if (cart == null) {
                        // üründen yoktur bir işlem yapma
                        // todo snackbar göster
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
                        // todo snackbar göster
                    }
                }
            }


        }
    }

    private fun updateLikeButton() {
        binding.homeRecyclerView.adapter!!.notifyDataSetChanged()

    }


}
