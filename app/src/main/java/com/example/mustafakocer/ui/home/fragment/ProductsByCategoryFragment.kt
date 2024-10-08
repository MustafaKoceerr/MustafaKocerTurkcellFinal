package com.example.mustafakocer.ui.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
                        binding.productsByCategoryRecyclerView.adapter = ProductAdapter(resource.value.products)
                    }

                    is Resource.Failure -> {

                    }
                    is Resource.Waiting ->{
                        binding.progressbar.visibleProgressBar(false)
                    }
                }

            }

        }
    }

}