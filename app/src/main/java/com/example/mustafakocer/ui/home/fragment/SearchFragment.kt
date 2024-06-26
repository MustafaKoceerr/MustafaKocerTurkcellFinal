package com.example.mustafakocer.ui.home.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mustafakocer.data.model.Resource
import com.example.mustafakocer.databinding.FragmentSearchBinding
import com.example.mustafakocer.ui.base.BaseFragment
import com.example.mustafakocer.ui.home.adapter.ProductAdapter
import com.example.mustafakocer.ui.home.viewmodel.SearchViewModel
import com.example.mustafakocer.util.visibleProgressBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>() {

    private val viewModel: SearchViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchRecyclerView.also { recyclerView ->
            recyclerView.layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        }
        observeProducts()

        binding.searchView.clearFocus()
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // todo api isteği at
                query?.let {
                    viewModel.searchProducts(query)
                }
                return false
                // if you want to close keyboard then return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                return true
                /*
                true döndürmek: Olayı tamamen işlediğinizi belirtir ve varsayılan davranışı iptal eder.
                false döndürmek: Olayın tamamen işlenmediğini belirtir ve varsayılan davranışın devam etmesine izin verir.
                 */
            }

        })


    }


    override fun getFragmentDataBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(inflater, container, false)
    }


    private fun observeProducts() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.products.collect { resource ->
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
                        binding.searchRecyclerView.adapter = ProductAdapter(resource.value.products)
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

}