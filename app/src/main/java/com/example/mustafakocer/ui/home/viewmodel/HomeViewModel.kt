package com.example.mustafakocer.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mustafakocer.data.model.Products
import com.example.mustafakocer.data.model.Resource
import com.example.mustafakocer.data.repository.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: NetworkRepository
) : ViewModel() {


    private val _products = MutableStateFlow<Resource<Products>>(Resource.Loading)
    val products: StateFlow<Resource<Products>> = _products.asStateFlow()
    fun getProducts() {
        viewModelScope.launch {
            _products.value = Resource.Loading
            // Make the API call
            val response = repository.getProductsRepo()
            // assign the value, it can be success or failure
            _products.value = response
        }
    }

}