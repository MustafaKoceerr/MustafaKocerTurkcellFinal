package com.example.mustafakocer.presentation.feature_product_category

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mustafakocer.R
import com.example.mustafakocer.databinding.FragmentCategoryBinding
import com.example.mustafakocer.databinding.LayoutStateEmptyBinding
import com.example.mustafakocer.domain.model.Category
import com.example.mustafakocer.domain.util.Resource
import com.example.mustafakocer.presentation.base.BaseFragment
import com.example.mustafakocer.presentation.common.UiErrorMapper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CategoryFragment : BaseFragment<FragmentCategoryBinding>(FragmentCategoryBinding::inflate) {

    private val viewModel: CategoryViewModel by viewModels()
    private lateinit var categoryListAdapter: CategoryListAdapter

    @Inject
    lateinit var injectedUiErrorMapper: UiErrorMapper

    // --- BaseFragment Implementasyonu ---
    override val uiErrorMapper: UiErrorMapper by lazy { injectedUiErrorMapper }

    override fun onRetry() {
        viewModel.fetchCategories()
    }
    // ------------------------------------

    // ViewStub'lar inflate edildikten sonra binding'lerini tutmak için.
    private var emptyBinding: LayoutStateEmptyBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeCategories()
    }

    private fun setupRecyclerView() {
        categoryListAdapter = CategoryListAdapter { category ->
            navigateToProductsByCategory(category)
        }
        binding.contentView.apply {
            adapter = categoryListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeCategories() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.categoriesState.collect { resource ->
                    val isLoading = resource is Resource.Loading
                    val isError = resource is Resource.Error
                    val isSuccessAndEmpty = resource is Resource.Success && resource.data.isEmpty()

                    // Görünürlükleri yönet
                    binding.viewLoadingStub.isVisible = isLoading
                    binding.contentView.isVisible =
                        resource is Resource.Success && !isSuccessAndEmpty

                    if (isError) {
                        handleErrorState(
                            binding.viewErrorStub,
                            (resource as Resource.Error).exception
                        )
                    } else {
                        hideErrorState()
                    }

                    if (isSuccessAndEmpty) {
                        if (emptyBinding == null) {
                            emptyBinding =
                                LayoutStateEmptyBinding.bind(binding.viewEmptyStub.inflate())
                        }
                        emptyBinding?.root?.isVisible = true
                        emptyBinding?.txtEmptyTitle?.setText(R.string.empty_categories_title)
                        emptyBinding?.txtEmptySubtitle?.setText(R.string.empty_categories_subtitle)
                    } else {
                        emptyBinding?.root?.isVisible = false
                    }

                    if (resource is Resource.Success) {
                        categoryListAdapter.submitList(resource.data)
                    }
                }
            }
        }
    }

    private fun navigateToProductsByCategory(category: Category) {
        val action = CategoryFragmentDirections.actionCategoryFragmentToProductsByCategoryFragment(
            categoryName = category.slug,
            categoryDisplayName = category.name
        )
        findNavController().navigate(action)
    }
}