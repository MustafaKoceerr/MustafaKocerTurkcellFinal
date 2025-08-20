package com.example.mustafakocer.presentation.feature_product_category

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mustafakocer.R
import com.example.mustafakocer.databinding.FragmentCategoryBinding
import com.example.mustafakocer.domain.model.Category
import com.example.mustafakocer.domain.util.Resource
import com.example.mustafakocer.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Displays a list of all available product categories.
 * It observes the category list from the [CategoryViewModel] and handles user
 * interactions to navigate to the product list for a selected category.
 */
@AndroidEntryPoint
class CategoryFragment : BaseFragment<FragmentCategoryBinding>(FragmentCategoryBinding::inflate) {

    private val viewModel: CategoryViewModel by viewModels()
    private lateinit var categoryListAdapter: CategoryListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeCategories()
    }

    /**
     * Initializes the RecyclerView and its adapter, and sets up the retry mechanism.
     */
    private fun setupRecyclerView() {
        categoryListAdapter = CategoryListAdapter { category ->
            navigateToProductsByCategory(category)
        }
        binding.stateLayout.onRetry = {
            viewModel.fetchCategories()
        }
        binding.stateLayout.findViewById<RecyclerView>(R.id.contentView).apply {
            adapter = categoryListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    /**
     * Subscribes to the categories state flow from the ViewModel to update the UI.
     */
    private fun observeCategories() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.categoriesState.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            binding.stateLayout.showLoading()
                        }
                        is Resource.Error -> {
                            binding.stateLayout.showError(subtitle = resource.exception.message)
                        }
                        is Resource.Success -> {
                            val categories = resource.data
                            if (categories.isNullOrEmpty()) {
                                binding.stateLayout.showEmpty()
                            } else {
                                categoryListAdapter.submitList(categories)
                                binding.stateLayout.showContent()
                            }
                        }
                        is Resource.Idle -> { /* No-op */ }
                    }
                }
            }
        }
    }

    /**
     * Navigates to the screen that displays products for the selected category.
     */
    private fun navigateToProductsByCategory(category: Category) {
        val action = CategoryFragmentDirections.actionCategoryFragmentToProductsByCategoryFragment(
            categoryName = category.slug,
            categoryDisplayName = category.name
        )
        findNavController().navigate(action)
    }
}