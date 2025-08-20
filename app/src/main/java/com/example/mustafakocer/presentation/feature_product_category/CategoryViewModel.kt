package com.example.mustafakocer.presentation.feature_product_category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.mustafakocer.domain.model.Category
import com.example.mustafakocer.domain.model.Product
import com.example.mustafakocer.domain.usecase.GetCategoriesUseCase
import com.example.mustafakocer.domain.usecase.GetProductsByCategoryUseCase
import com.example.mustafakocer.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * Manages the UI state and business logic for screens related to product categories.
 * It handles fetching the list of all categories and also provides a reactive stream
 * of paginated products for a selected category.
 */
@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getProductsByCategoryUseCase: GetProductsByCategoryUseCase,
) : ViewModel() {

    private val _categoriesState = MutableStateFlow<Resource<List<Category>>>(Resource.Idle)
    val categoriesState: StateFlow<Resource<List<Category>>> = _categoriesState.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)

    /**
     * A reactive flow of paginated products that automatically updates when a new category is selected.
     *
     * It uses `flatMapLatest` to listen to changes in `_selectedCategory`. When the category
     * changes, the old product flow is cancelled, and a new one is created with the new
     * category name. This is a highly efficient way to handle dynamic data streams.
     *
     * The `.cachedIn(viewModelScope)` operator ensures the PagingData survives configuration changes.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val productsByCategoryFlow: Flow<PagingData<Product>> = _selectedCategory
        .flatMapLatest { categoryName ->
            if (!categoryName.isNullOrBlank()) {
                getProductsByCategoryUseCase(categoryName)
            } else {
                flowOf(PagingData.empty())
            }
        }
        .cachedIn(viewModelScope)

    init {
        fetchCategories()
    }

    /**
     * Triggers the use case to fetch the list of all available categories.
     */
    fun fetchCategories() {
        getCategoriesUseCase().onEach { resource ->
            _categoriesState.value = resource
        }.launchIn(viewModelScope)
    }


    /**
     * Called by the UI to set the currently selected category, which in turn triggers
     * the `productsByCategoryFlow` to emit new data.
     *
     * @param categoryName The 'slug' name of the category to fetch products for.
     */
    fun onCategorySelected(categoryName: String) {
        _selectedCategory.value = categoryName
    }
}