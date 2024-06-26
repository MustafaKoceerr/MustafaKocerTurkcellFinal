package com.example.mustafakocer.ui.home.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mustafakocer.data.model.Product
import com.example.mustafakocer.data.model.Resource
import com.example.mustafakocer.ui.base.BaseFragment
import com.example.mustafakocer.databinding.FragmentFavoriteBinding
import com.example.mustafakocer.ui.home.adapter.OrderProductAdapter
import com.example.mustafakocer.ui.home.adapter.ProductAdapter
import com.example.mustafakocer.ui.home.viewmodel.FavoriteViewModel
import com.example.mustafakocer.util.UserId
import com.example.mustafakocer.util.visibleProgressBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FavoriteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class FavoriteFragment : BaseFragment<FragmentFavoriteBinding>() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val viewModel: FavoriteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.favoriteRecyclerView.also { recyclerView ->
            recyclerView.layoutManager =
                GridLayoutManager(requireContext(), 1, GridLayoutManager.VERTICAL, false)
        }
        viewModel.getAllLikedProducts(UserId.userId)
        observeFavorites()

    }

    override fun getFragmentDataBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFavoriteBinding {
        return FragmentFavoriteBinding.inflate(inflater,container,false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyFavoriteFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavoriteFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun observeFavorites() {
        viewLifecycleOwner.lifecycleScope.launch {
            /*
            When collecting from a StateFlow in your UI, you should typically use Dispatchers.Main.
            This is because you usually want to update the UI in response to the collected state,
            and all UI updates must occur on the main thread.
             */
            viewModel.likedproducts.collectLatest { resource ->

                val productList: List<Product> = resource.map { it.product }
                Log.d("Favorite","resource $resource,  productList $productList")
                binding.favoriteRecyclerView.adapter =
                    ProductAdapter(productList)

            }

        }
    }

}