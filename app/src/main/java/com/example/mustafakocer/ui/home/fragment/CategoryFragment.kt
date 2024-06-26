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
import com.example.mustafakocer.ui.base.BaseFragment
import com.example.mustafakocer.data.model.Resource
import com.example.mustafakocer.databinding.FragmentCategoryBinding
import com.example.mustafakocer.ui.home.adapter.CategoryAdapter
import com.example.mustafakocer.ui.home.viewmodel.CategoryViewModel
import com.example.mustafakocer.util.visibleProgressBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CategoryFragment : BaseFragment<FragmentCategoryBinding>() {

    private val viewModel: CategoryViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.categoryRecyclerView.also { recyclerView ->
            recyclerView.layoutManager =
                GridLayoutManager(requireContext(), 1, GridLayoutManager.VERTICAL, false)
        }
        observeCategories()

        viewModel.getCategories()

    }

    override fun getFragmentDataBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCategoryBinding {
        return FragmentCategoryBinding.inflate(inflater, container, false)
    }

    private fun observeCategories() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categories.collect { resource ->
                binding.progressbar.visibleProgressBar(false)

                when (resource) {
                    is Resource.Loading -> {
                        // Show loading indicator
                        binding.progressbar.visibleProgressBar(true)
                    }

                    is Resource.Success -> {
                        // Update UI with products data
                        Toast.makeText(
                            requireContext(),
                            "Kategoriler Geldi${resource.value}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("flow", "${resource.value}")
                        // Use the products data to update the UI
                        binding.categoryRecyclerView.adapter = CategoryAdapter(resource.value)
                    }

                    is Resource.Failure -> {
                        Toast.makeText(
                            requireContext(),
                            "Hata ${resource.errorCode}  ${resource.errorBody}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("Hata", "${resource.errorCode}  ${resource.errorBody}")

                    }

                    is Resource.Waiting -> {
                        binding.progressbar.visibleProgressBar(false)
                    }
                }

            }

        }
    }


}