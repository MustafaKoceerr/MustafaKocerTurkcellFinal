package com.example.mustafakocer.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mustafakocer.data.model.Products
import com.example.mustafakocer.data.repository.NetworkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(val repository: NetworkRepository): ViewModel() {


    private val _statedFlowProducts = MutableStateFlow<Products?>(null)
    val statedFlowProducts = _statedFlowProducts.asStateFlow()

    fun getProducts(){
        viewModelScope.launch {
            Log.d("apiRequest", "apirequest start")
            val response = repository.getProducts()

            if (response==null){
                Log.d("apiRequest", "Response successful but model is wrong, can't take data, null")
            }else{
                Log.d("apiRequest", "Response successful and model taken")
                _statedFlowProducts.value = response
            }

        }
    }

}