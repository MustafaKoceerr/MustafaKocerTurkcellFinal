package com.example.mustafakocer.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mustafakocer.data.model.Categories
import com.example.mustafakocer.data.model.Products
import com.example.mustafakocer.data.model.Resource
import com.example.mustafakocer.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.Response
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val repository: CategoryRepository
):ViewModel() {

    private val _categories = MutableStateFlow<Resource<Categories>>(Resource.Loading)
    val categories: StateFlow<Resource<Categories>> = _categories.asStateFlow()

    fun getCategories() {
        viewModelScope.launch {
            _categories.value = Resource.Loading
            // Make the API call
            val response = repository.getCategoriesRepo()
            // assign the value, it can be success or failure

            _categories.value = response
        }
    }

    private val _productsByCategory = MutableStateFlow<Resource<Products>>(Resource.Loading)
    val productsByCategory: StateFlow<Resource<Products>> = _productsByCategory.asStateFlow()

    fun getProductsByCategory(categoryName: String) {
        viewModelScope.launch {
            _productsByCategory.value = Resource.Loading
            // Make the API call
            val response = repository.getProductsByCategoryRepo(categoryName)
            // assign the value, it can be success or failure

            _productsByCategory.value = response
        }
    }

}