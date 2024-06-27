package com.example.mustafakocer.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mustafakocer.data.model.OrderResponse
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
class OrderViewModel @Inject constructor(
    private val repository: NetworkRepository
):ViewModel() {

    private val _order = MutableStateFlow<Resource<OrderResponse>>(Resource.Waiting)
    val order: StateFlow<Resource<OrderResponse>> = _order.asStateFlow()

    fun getOrder(userId: String) {
        viewModelScope.launch {
            _order.value = Resource.Loading
            // Make the API call
            val response = repository.getCartsByUserRepo(userId)
            // assign the value, it can be success or failure
            _order.value = response
        }
    }
}