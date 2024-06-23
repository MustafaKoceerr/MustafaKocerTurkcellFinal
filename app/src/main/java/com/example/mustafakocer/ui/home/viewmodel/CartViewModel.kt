package com.example.mustafakocer.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mustafakocer.data.db.entity.CartRequest
import com.example.mustafakocer.data.model.CartResponse
import com.example.mustafakocer.data.model.Resource
import com.example.mustafakocer.data.model.User
import com.example.mustafakocer.data.repository.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repository: NetworkRepository
):ViewModel() {

    private val _cart = MutableStateFlow<Resource<CartResponse>>(Resource.Loading)
    val cart: StateFlow<Resource<CartResponse>> = _cart.asStateFlow()

    fun cartInfo(cartRequest: CartRequest) {
        viewModelScope.launch {
            _cart.value = Resource.Loading
            // Make the API call
            val response = repository.cartInfoRepo(cartRequest)
            // assign the value, it can be success or failure
            _cart.value = response
        }
    }
}