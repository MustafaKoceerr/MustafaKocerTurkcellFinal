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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mustafakocer.R
import com.example.mustafakocer.ui.base.BaseFragment
import com.example.mustafakocer.data.model.Resource
import com.example.mustafakocer.databinding.FragmentHomeBinding
import com.example.mustafakocer.databinding.FragmentProductsByCategoryBinding
import com.example.mustafakocer.ui.home.adapter.ProductAdapter
import com.example.mustafakocer.ui.home.viewmodel.CategoryViewModel
import com.example.mustafakocer.util.visibleProgressBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProductsByCategoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class ProductsByCategoryFragment : BaseFragment<FragmentProductsByCategoryBinding>() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val viewModel: CategoryViewModel by viewModels()
    private val args: ProductsByCategoryFragmentArgs by navArgs<ProductsByCategoryFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // toolbar'ı ayarlayabilirsin
        val categoryName = args.categoryName
        // we get categoryName from categoryFragment. Now we can send it as parameter to viewModel
        binding.productsByCategoryRecyclerView.also { recyclerView ->
            recyclerView.layoutManager = GridLayoutManager(requireContext(),1,GridLayoutManager.VERTICAL,false)
        }

        // todo sepete veya favoriye tıklayınca gitmesi lazım
        observeProductsByCategories()

        viewModel.getProductsByCategory(categoryName)

    }

    override fun getFragmentDataBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProductsByCategoryBinding {
        return FragmentProductsByCategoryBinding.inflate(inflater,container,false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProductsByCategoryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProductsByCategoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun observeProductsByCategories() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.productsByCategory.collect { resource ->
                binding.progressbar.visibleProgressBar(false)

                when (resource) {
                    is Resource.Loading -> {
                        // Show loading indicator
                        binding.progressbar.visibleProgressBar(true)
                    }
                    is Resource.Success -> {
                        // Update UI with products data
                        Toast.makeText(requireContext(), "Kategoriler Geldi${resource.value}", Toast.LENGTH_SHORT).show()
                        Log.d("flow","${resource.value}")
                        // Use the products data to update the UI
                        binding.productsByCategoryRecyclerView.adapter = ProductAdapter(resource.value.products)
                    }

                    is Resource.Failure -> {
                        Toast.makeText(requireContext(), "Hata ${resource.errorCode}  ${resource.errorBody}", Toast.LENGTH_SHORT).show()
                        Log.d("Hata","${resource.errorCode}  ${resource.errorBody}")

                    }
                }

            }

        }
    }

}