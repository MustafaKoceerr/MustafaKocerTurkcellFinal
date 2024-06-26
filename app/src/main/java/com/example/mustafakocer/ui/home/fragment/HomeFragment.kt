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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(), LikeButtonClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var isAdapterAttached = 0


    private val viewModel: HomeViewModel by viewModels()
    private lateinit var productIdList: MutableList<Int>

    private var likedProductList: MutableList<LikedProduct>? = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.txtView.setText("SELAMLAR ISTE BASARDIN")




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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
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

                        productIdList = likedProductList!!.mapNotNull { it.productId }.toMutableList()
                        Log.d("likedProductList", "ID LIST $productIdList")

                        if (isAdapterAttached==0){
                            binding.homeRecyclerView.adapter =
                                ProductAdapter2(resource.value.products, this@HomeFragment, productIdList)
                            isAdapterAttached++
                        }else{
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

                    if(value==null){
                        val likedProduct = LikedProduct(null, UserId.userId, 0, product.id, product)
                        viewModel.insertProductDB(likedProduct)
                        productIdList.add(product.id!!)
                    }else{
                        viewModel.deleteProductDB(UserId.userId, product.id!!)
                        productIdList.remove(product.id)
                    }
                    updateLikeButton()
                }
            }
            R.id.btnPlus -> {
                Toast.makeText(requireContext(),"plus",Toast.LENGTH_SHORT).show()
                viewLifecycleOwner.lifecycleScope.launch {
                    val cart = viewModel.GetOneCart(UserId.userId, product.id!!)

                    if(cart==null){
                        // üründen yoktur 1 tane ekle
                        val addedCart = Cart(null, UserId.userId,product.id,1)
                        viewModel.InsertCart(addedCart)
                        // todo snackbar göster
                    }else{
                        // üründen var sayısını arttır.
                        val updatedCart = cart.copy(quantity = cart.quantity!! + 1)
                        viewModel.UpdateCart(updatedCart)
                        // todo snackbar göster
                    }
                }

            }
            R.id.btnMinus -> {
                Toast.makeText(requireContext(),"minus",Toast.LENGTH_SHORT).show()
                viewLifecycleOwner.lifecycleScope.launch {
                    val cart = viewModel.GetOneCart(UserId.userId, product.id!!)

                    if(cart==null){
                        // üründen yoktur bir işlem yapma
                        // todo snackbar göster
                    }else{
                        // üründen var sayısını azalt.
                        val quantity = cart.quantity!! -1
                        if (quantity==0){
                            // sil
                            viewModel.DeleteCart(cart.userId, cart.id!!)
                        }
                        else{
                            val updatedCart = cart.copy(quantity = quantity)
                            viewModel.UpdateCart(updatedCart)
                        }
                        // todo snackbar göster
                    }
                }
            }


        }
    }

    private fun updateLikeButton(){
        binding.homeRecyclerView.adapter!!.notifyDataSetChanged()

    }

    private fun likeButtonClick(likeButton: ImageButton) {
        var isLiked = likeButton.tag as? Boolean ?: false
        if (isLiked) {
            likeButton.setImageResource(R.drawable.ic_heart_empty)
        } else {
            likeButton.setImageResource(R.drawable.ic_heart_filled)
        }
        isLiked = !isLiked
        likeButton.tag = isLiked
    }
}
