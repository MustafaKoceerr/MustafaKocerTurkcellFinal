package com.example.mustafakocer.ui.home.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mustafakocer.data.model.Resource
import com.example.mustafakocer.databinding.FragmentProductsByCategoryBinding
import com.example.mustafakocer.ui.base.BaseFragment
import com.example.mustafakocer.ui.home.adapter.ProductAdapter
import com.example.mustafakocer.ui.home.viewmodel.CategoryViewModel
import com.example.mustafakocer.util.visibleProgressBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ProductsByCategoryFragment : BaseFragment<FragmentProductsByCategoryBinding>() {

    private val viewModel: CategoryViewModel by viewModels()
    private val args: ProductsByCategoryFragmentArgs by navArgs<ProductsByCategoryFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // toolbar'ı ayarlayabilirsin
        val categoryName = args.categoryName
        // we get categoryName from categoryFragment. Now we can send it as parameter to viewModel
        binding.productsByCategoryRecyclerView.also { recyclerView ->
            recyclerView.layoutManager = GridLayoutManager(requireContext(),2,GridLayoutManager.VERTICAL,false)
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
                    is Resource.Waiting ->{
                        binding.progressbar.visibleProgressBar(false)
                    }
                }

            }

        }
    }

}